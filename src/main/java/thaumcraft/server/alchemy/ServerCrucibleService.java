package thaumcraft.server.alchemy;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.crafting.CrucibleRecipe;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchRegistry;

public final class ServerCrucibleService {
    private static final String OUTPUT_MARKER = "ThaumcraftCrucibleOutput";

    private ServerCrucibleService() {
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        long counter = crucible.incrementCounter();
        updateHeat(serverLevel, pos, crucible);

        if (crucible.tagAmount() > CrucibleBlockEntity.MAX_TAGS && counter % 5L == 0L) {
            takeRandomFromSource(serverLevel, crucible);
            spill(serverLevel, pos);
        }

        if (counter > 100L && crucible.getHeat() > 150 && !crucible.getAspects().isEmpty()) {
            crucible.setCounter(0L);
            degradeAspect(serverLevel, pos, crucible);
        }
    }

    public static void entityInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible,
            Entity entity) {
        if (!crucible.isBoiling()) {
            return;
        }

        if (entity instanceof ItemEntity itemEntity && !itemEntity.getItem().isEmpty()) {
            if (!itemEntity.getPersistentData().getBoolean(OUTPUT_MARKER)) {
                attemptSmelt((ServerLevel) level, pos, crucible, itemEntity);
            }
            return;
        }

        if (entity instanceof LivingEntity living) {
            int delay = crucible.getEntityDamageDelay() + 1;
            crucible.setEntityDamageDelay(delay);
            if (delay % 10 == 0) {
                living.hurt(level.damageSources().inFire(), 1.0F);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 2.0F);
            }
        }
    }

    public static void spillRemnants(Level level, BlockPos pos, CrucibleBlockEntity crucible) {
        int spills = crucible.hasWater() || crucible.tagAmount() > 0 ? crucible.tagAmount() / 2 : 0;
        for (int i = 0; i < spills; i++) {
            spill((ServerLevel) level, pos);
        }
        crucible.clearContents();
    }

    private static void updateHeat(ServerLevel level, BlockPos pos, CrucibleBlockEntity crucible) {
        int heat = crucible.getHeat();
        if (!crucible.hasWater()) {
            if (heat > 0) {
                crucible.setHeat(heat - 1);
                crucible.markChanged();
            }
            return;
        }

        BlockState below = level.getBlockState(pos.below());
        boolean heatSource = below.is(Blocks.FIRE)
                || below.is(Blocks.SOUL_FIRE)
                || below.is(Blocks.LAVA)
                || below.getFluidState().is(FluidTags.LAVA);

        if (heatSource) {
            int newHeat = Math.min(200, heat + 1);
            if (newHeat != heat) {
                crucible.setHeat(newHeat);
                if (heat < 151 && newHeat >= 151) {
                    crucible.markChanged();
                } else {
                    crucible.setChanged();
                }
            }
        } else if (heat > 0) {
            int newHeat = heat - 1;
            crucible.setHeat(newHeat);
            if (heat >= 150 && newHeat < 150) {
                crucible.markChanged();
            } else {
                crucible.setChanged();
            }
        }
    }

    private static void degradeAspect(ServerLevel level, BlockPos pos, CrucibleBlockEntity crucible) {
        AspectList aspects = crucible.getAspects();
        Aspect selected = randomAspect(level, aspects);
        if (selected == null) {
            return;
        }
        if (selected.isPrimal()) {
            selected = randomAspect(level, aspects);
        }
        if (selected == null) {
            return;
        }

        crucible.drainWater(2);
        crucible.removeAspect(selected, 1);
        Aspect[] components = selected.getComponents();
        if (!selected.isPrimal() && components != null && components.length > 0) {
            crucible.addAspects(new AspectList().add(components[level.random.nextInt(components.length)], 1));
        } else {
            spill(level, pos);
        }
        crucible.markChanged();
    }

    private static void attemptSmelt(ServerLevel level, BlockPos pos, CrucibleBlockEntity crucible,
            ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        int originalCount = stack.getCount();
        boolean bubble = false;
        boolean event = false;
        for (int i = 0; i < originalCount; i++) {
            ItemStack single = stack.copyWithCount(1);
            CrucibleRecipe recipe = findMatchingRecipe(level, crucible.getAspects(), single, findThrower(level,
                    itemEntity));
            if (recipe != null && crucible.hasWater()) {
                crucible.setAspects(recipe.removeMatching(crucible.getAspects()));
                crucible.drainWater(50);
                ejectItem(level, pos, recipe.getResultItem(level.registryAccess()).copy());
                event = true;
                stack.shrink(1);
                crucible.setCounter(-250L);
                crucible.markChanged();
                continue;
            }

            AspectList objectTags = ObjectAspectRegistry.getObjectTagsWithBonus(single);
            if (objectTags.isEmpty()) {
                bounceItem(level, itemEntity);
                return;
            }
            crucible.addAspects(objectTags);
            bubble = true;
            stack.shrink(1);
            crucible.setCounter(-150L);
            crucible.markChanged();
        }

        if (bubble) {
            level.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), TCSoundEvents.BUBBLE.get(),
                    SoundSource.MASTER, 0.2F, 1.0F + level.random.nextFloat() * 0.4F);
            level.blockEvent(pos, TCBlocks.CRUCIBLE.get(), 2, 1);
        }

        if (event) {
            level.blockEvent(pos, TCBlocks.CRUCIBLE.get(), 2, 5);
        }

        if (stack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(stack);
        }
    }

    private static CrucibleRecipe findMatchingRecipe(ServerLevel level, AspectList aspects, ItemStack catalyst,
            ServerPlayer player) {
        List<RecipeHolder<CrucibleRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(TCRecipeTypes.CRUCIBLE.get());
        CrucibleRecipe best = null;
        int highest = 0;
        for (RecipeHolder<CrucibleRecipe> holder : recipes) {
            CrucibleRecipe recipe = holder.value();
            if (requiresResearch(recipe) && player != null && !ResearchManager.isComplete(player,
                    recipe.getResearch())) {
                continue;
            }
            if (recipe.matches(aspects, catalyst) && recipe.matchWeight() > highest) {
                highest = recipe.matchWeight();
                best = recipe;
            }
        }
        return best;
    }

    private static boolean requiresResearch(CrucibleRecipe recipe) {
        return !recipe.getResearch().isBlank() && ResearchRegistry.get(recipe.getResearch()).isPresent();
    }

    private static ServerPlayer findThrower(ServerLevel level, ItemEntity itemEntity) {
        return itemEntity.getOwner() instanceof ServerPlayer player ? player : null;
    }

    private static void ejectItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity output = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack);
        output.getPersistentData().putBoolean(OUTPUT_MARKER, true);
        output.setDeltaMovement((level.random.nextFloat() - level.random.nextFloat()) * 0.1D,
                0.25D, (level.random.nextFloat() - level.random.nextFloat()) * 0.1D);
        level.addFreshEntity(output);
    }

    private static void bounceItem(ServerLevel level, ItemEntity itemEntity) {
        itemEntity.setDeltaMovement((level.random.nextFloat() - level.random.nextFloat()) * 0.2D,
                0.35D, (level.random.nextFloat() - level.random.nextFloat()) * 0.2D);
        level.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), TCSoundEvents.RANDOM_POP.get(),
                SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F);
    }

    private static Aspect randomAspect(ServerLevel level, AspectList aspects) {
        if (aspects.isEmpty()) {
            return null;
        }
        return aspects.getAspects().get(level.random.nextInt(aspects.size()));
    }

    private static void takeRandomFromSource(ServerLevel level, CrucibleBlockEntity crucible) {
        Aspect aspect = randomAspect(level, crucible.getAspects());
        if (aspect != null) {
            crucible.removeAspect(aspect, 1);
        }
    }

    private static void spill(ServerLevel level, BlockPos pos) {
        if (level.random.nextInt(4) != 0) {
            return;
        }

        BlockPos above = pos.above();
        if (level.isEmptyBlock(above)) {
            FluxBlock.placeFlux(level, above, level.random.nextBoolean(), 0);
            return;
        }

        BlockState aboveState = level.getBlockState(above);
        if (aboveState.is(thaumcraft.common.registry.TCBlocks.FLUX_GOO.get())
                || aboveState.is(thaumcraft.common.registry.TCBlocks.FLUX_GAS.get())) {
            FluxBlock.addFlux(level, above, aboveState.is(thaumcraft.common.registry.TCBlocks.FLUX_GAS.get()), 1);
            return;
        }

        int x = level.random.nextInt(3) - 1;
        int y = level.random.nextInt(3) - 1;
        int z = level.random.nextInt(3) - 1;
        BlockPos randomPos = pos.offset(x, y, z);
        if (level.isEmptyBlock(randomPos)) {
            FluxBlock.placeFlux(level, randomPos, level.random.nextBoolean(), 0);
        }
    }
}
