package thaumcraft.server.infusion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.server.warp.ServerWarpService;

public final class ServerInfusionRuntime {
    private ServerInfusionRuntime() {
    }

    public static void tickRunicMatrix(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        int scanInterval = matrix.isCrafting() ? 20 : 100;
        boolean scanned = false;
        if (matrix.consumeSurroundingsCheckRequest(scanInterval)) {
            refreshRunicMatrixSurroundings(matrix, level, pos);
            scanned = true;
        }

        if (matrix.isActive() && scanned && !matrix.isStructureValid()) {
            matrix.setActive(false);
            return;
        }

        if (matrix.isActive() && matrix.isCrafting() && matrix.getTickCount() % matrix.getCountDelay() == 0) {
            craftCycle(matrix, level);
        }
    }

    public static boolean refreshRunicMatrixSurroundings(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        InfusionAltarScan scan = InfusionAltarScan.scan(level, pos);
        matrix.applySurroundingsScan(scan.valid(), scan.symmetry(), scan.pedestals());
        return scan.valid();
    }

    private static void craftCycle(RunicMatrixBlockEntity matrix, Level level) {
        boolean valid = isCenterInputValid(matrix, level);

        if (!valid || shouldTriggerInstabilityEvent(matrix, level)) {
            triggerInstabilityEvent(matrix, level);
            if (valid) {
                return;
            }
        }

        if (!valid) {
            matrix.failCrafting(level);
            return;
        }

        if (!(level.getBlockEntity(matrix.getBlockPos().below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)) {
            matrix.failCrafting(level);
            return;
        }

        ensureRecipeEssentiaBase(matrix, level);

        if (matrix.getRecipeType() == 1 && matrix.getRecipeXp() > 0) {
            drainNextExperience(matrix, level);
            return;
        }

        if (matrix.getRecipeType() == 1 && matrix.getRecipeXp() == 0) {
            matrix.setCountDelay(10);
        }

        if (matrix.getRecipeEssentia().visSize() > 0) {
            drainNextEssentia(matrix, level);
            return;
        }

        if (matrix.getRecipeIngredients().isEmpty()) {
            matrix.finishCrafting(level, centerPedestal);
            sendPedestalSparkleFx(matrix, level, centerPedestal.getBlockPos(), 12);
            return;
        }

        if (consumeNextIngredient(matrix, level)) {
            return;
        }

        addMissingIngredientInstability(matrix, level);
    }

    private static boolean isCenterInputValid(RunicMatrixBlockEntity matrix, Level level) {
        if (!(level.getBlockEntity(matrix.getBlockPos().below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)) {
            return false;
        }
        ItemStack stack = centerPedestal.getStoredItem();
        return !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, matrix.getRecipeInput());
    }

    private static boolean shouldTriggerInstabilityEvent(RunicMatrixBlockEntity matrix, Level level) {
        return matrix.getInstability() > 0 && level.random.nextInt(500) <= matrix.getInstability();
    }

    private static void ensureRecipeEssentiaBase(RunicMatrixBlockEntity matrix, Level level) {
        if (!matrix.getRecipeEssentiaBase().isEmpty() || matrix.getRecipeId() == null) {
            return;
        }
        if (matrix.getRecipeType() == 1) {
            level.getRecipeManager()
                    .getAllRecipesFor(TCRecipeTypes.INFUSION_ENCHANTMENT.get())
                    .stream()
                    .filter(holder -> holder.id().equals(matrix.getRecipeId()))
                    .findFirst()
                    .ifPresent(holder -> matrix.setRecipeEssentiaBase(
                            holder.value().getScaledEssentia(matrix.getRecipeInput())));
        } else {
            level.getRecipeManager()
                    .getAllRecipesFor(TCRecipeTypes.INFUSION.get())
                    .stream()
                    .filter(holder -> holder.id().equals(matrix.getRecipeId()))
                    .findFirst()
                    .ifPresent(holder -> matrix.setRecipeEssentiaBase(holder.value().getEssentia()));
        }
    }

    private static void drainNextExperience(RunicMatrixBlockEntity matrix, Level level) {
        List<Player> targets = level.getEntitiesOfClass(Player.class, effectBounds(matrix));
        if (targets.isEmpty()) {
            return;
        }

        for (Player target : targets) {
            if (target.experienceLevel > 0) {
                target.giveExperienceLevels(-1);
                matrix.setRecipeXp(matrix.getRecipeXp() - 1);
                target.hurt(level.damageSources().magic(), level.random.nextInt(2));
                sendInfusionEntitySourceFx(matrix, level, target.getId(), 15);
                level.playSound(null, target.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,
                        1.0F, 2.0F + level.random.nextFloat() * 0.4F);
                matrix.setCountDelay(20);
                return;
            }
        }

        AspectList recipeEssentia = matrix.getRecipeEssentia();
        List<Aspect> aspects = recipeEssentia.getAspects();
        if (!aspects.isEmpty() && level.random.nextInt(3) == 0) {
            Aspect aspect = aspects.get(level.random.nextInt(aspects.size()));
            recipeEssentia.add(aspect, 1);
            int instabilityBound = Math.max(1, 50 - matrix.getRecipeInstability() * 2);
            if (level.random.nextInt(instabilityBound) == 0) {
                matrix.setInstability(Math.min(25, matrix.getInstability() + 1));
            }
            matrix.setRecipeEssentia(recipeEssentia);
        }
    }

    private static void drainNextEssentia(RunicMatrixBlockEntity matrix, Level level) {
        boolean attemptedDrain = false;
        AspectList recipeEssentia = matrix.getRecipeEssentia();
        for (Aspect aspect : recipeEssentia.getAspects()) {
            if (recipeEssentia.getAmount(aspect) <= 0) {
                continue;
            }
            if (EssentiaHandler.drainEssentia(level, matrix.getBlockPos(), aspect, 12)) {
                recipeEssentia.reduce(aspect, 1);
                matrix.setRecipeEssentia(recipeEssentia);
                matrix.requestSurroundingsCheck();
                return;
            } else {
                attemptedDrain = true;
                int instabilityBound = Math.max(1, 100 - matrix.getRecipeInstability() * 3);
                if (level.random.nextInt(instabilityBound) == 0) {
                    matrix.setInstability(Math.min(25, matrix.getInstability() + 1));
                }
            }
        }
        matrix.requestSurroundingsCheck();
        if (attemptedDrain) {
            matrix.markChangedAndSync();
        }
    }

    private static boolean consumeNextIngredient(RunicMatrixBlockEntity matrix, Level level) {
        List<ItemStack> recipeIngredients = matrix.getRecipeIngredients();
        for (int ingredientIndex = 0; ingredientIndex < recipeIngredients.size(); ingredientIndex++) {
            ItemStack required = recipeIngredients.get(ingredientIndex);
            for (BlockPos pedestalPos : matrix.getPedestals()) {
                if (level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal) {
                    ItemStack stored = pedestal.getStoredItem();
                    if (!stored.isEmpty() && ItemStack.isSameItemSameComponents(stored, required)) {
                        int itemCount = matrix.getItemCount();
                        if (itemCount == 0) {
                            matrix.setItemCount(5);
                            sendInfusionSourceFx(matrix, level, pedestalPos, 60, 0);
                        } else if (itemCount <= 1) {
                            pedestal.setStoredItem(getCraftingRemainingItem(stored));
                            List<ItemStack> remaining = new ArrayList<>(recipeIngredients);
                            remaining.remove(ingredientIndex);
                            matrix.setRecipeIngredients(remaining);
                            matrix.setItemCount(0);
                        } else {
                            matrix.setItemCount(itemCount - 1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void addMissingIngredientInstability(RunicMatrixBlockEntity matrix, Level level) {
        List<Aspect> aspects = matrix.getRecipeEssentia().getAspects();
        if (aspects.isEmpty()) {
            AspectList baseEssentia = matrix.getRecipeEssentiaBase();
            aspects = baseEssentia.getAspects();
        }
        if (aspects.isEmpty()) {
            return;
        }

        List<ItemStack> recipeIngredients = matrix.getRecipeIngredients();
        AspectList recipeEssentia = matrix.getRecipeEssentia();
        for (int ingredientIndex = 0; ingredientIndex < recipeIngredients.size(); ingredientIndex++) {
            if (level.random.nextInt(1 + ingredientIndex) == 0) {
                Aspect aspect = aspects.get(level.random.nextInt(aspects.size()));
                recipeEssentia = recipeEssentia.add(aspect, 1);
                if (level.random.nextInt(Math.max(1, 50 - matrix.getRecipeInstability() * 2)) == 0) {
                    matrix.setInstability(Math.min(25, matrix.getInstability() + 1));
                }
                matrix.setRecipeEssentia(recipeEssentia);
            }
        }
    }

    private static ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem()) {
            return new ItemStack(stack.getItem().getCraftingRemainingItem());
        }
        return ItemStack.EMPTY;
    }

    private static void triggerInstabilityEvent(RunicMatrixBlockEntity matrix, Level level) {
        switch (level.random.nextInt(21)) {
            case 0, 2, 10, 13 -> instabilityEjectItem(matrix, level, 0);
            case 1, 11 -> instabilityEjectItem(matrix, level, 2);
            case 3, 8, 14 -> instabilityZap(matrix, level, false);
            case 4, 15 -> instabilityEjectItem(matrix, level, 5);
            case 5, 16 -> instabilityHarm(matrix, level, false);
            case 6, 17 -> instabilityEjectItem(matrix, level, 1);
            case 7 -> instabilityEjectItem(matrix, level, 4);
            case 9 -> level.explode(null, matrix.getBlockPos().getX() + 0.5D, matrix.getBlockPos().getY() + 0.5D,
                    matrix.getBlockPos().getZ() + 0.5D, 1.5F + level.random.nextFloat(),
                    Level.ExplosionInteraction.NONE);
            case 12 -> instabilityZap(matrix, level, true);
            case 18 -> instabilityHarm(matrix, level, true);
            case 19 -> instabilityEjectItem(matrix, level, 3);
            case 20 -> instabilityWarp(matrix, level);
            default -> {
            }
        }
    }

    private static void instabilityZap(RunicMatrixBlockEntity matrix, Level level, boolean all) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, effectBounds(matrix));
        for (LivingEntity target : targets) {
            sendBlockZapFx(matrix, level, matrix.getBlockPos().getX() + 0.5D, matrix.getBlockPos().getY() + 0.5D,
                    matrix.getBlockPos().getZ() + 0.5D, target.getX(), target.getY() + target.getBbHeight() / 2.0D,
                    target.getZ());
            DamageSource source = level.damageSources().magic();
            target.hurt(source, 4 + level.random.nextInt(4));
            if (!all) {
                break;
            }
        }
    }

    private static void instabilityHarm(RunicMatrixBlockEntity matrix, Level level, boolean all) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, effectBounds(matrix));
        for (LivingEntity target : targets) {
            if (level.random.nextBoolean()) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0, false, true));
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, 0, true, true));
            }
            if (!all) {
                break;
            }
        }
    }

    private static void instabilityWarp(RunicMatrixBlockEntity matrix, Level level) {
        List<Player> targets = level.getEntitiesOfClass(Player.class, effectBounds(matrix));
        if (!targets.isEmpty()) {
            Player target = targets.get(level.random.nextInt(targets.size()));
            if (level.random.nextFloat() < 0.25F) {
                ServerWarpService.addStickyWarpToPlayer(target, 1);
            } else {
                ServerWarpService.addWarpToPlayer(target, 1 + level.random.nextInt(5), true);
            }
        }
    }

    private static void instabilityEjectItem(RunicMatrixBlockEntity matrix, Level level, int type) {
        List<BlockPos> pedestals = matrix.getPedestals();
        for (int attempt = 0; attempt < 50 && !pedestals.isEmpty(); attempt++) {
            BlockPos pedestalPos = pedestals.get(level.random.nextInt(pedestals.size()));
            if (!(level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal)) {
                continue;
            }

            ItemStack stack = pedestal.getStoredItem();
            if (stack.isEmpty()) {
                continue;
            }

            if (type >= 3 && type != 5) {
                pedestal.setStoredItem(ItemStack.EMPTY);
            } else {
                pedestal.setStoredItem(ItemStack.EMPTY);
                Containers.dropItemStack(level, pedestalPos.getX(), pedestalPos.getY() + 1.0D, pedestalPos.getZ(),
                        stack.copy());
            }

            if (type == 1 || type == 3) {
                FluxBlock.placeFlux(level, pedestalPos.above(), false);
            } else if (type == 2 || type == 4) {
                FluxBlock.placeFlux(level, pedestalPos.above(), true);
            } else if (type == 5) {
                level.explode(null, pedestalPos.getX() + 0.5D, pedestalPos.getY() + 0.5D, pedestalPos.getZ() + 0.5D,
                        1.0F, Level.ExplosionInteraction.NONE);
            }

            sendBlockZapFx(matrix, level, matrix.getBlockPos().getX() + 0.5D, matrix.getBlockPos().getY() + 0.5D,
                    matrix.getBlockPos().getZ() + 0.5D, pedestalPos.getX() + 0.5D, pedestalPos.getY() + 1.5D,
                    pedestalPos.getZ() + 0.5D);
            sendPedestalSparkleFx(matrix, level, pedestalPos, 11);
            return;
        }
    }

    private static AABB effectBounds(RunicMatrixBlockEntity matrix) {
        return new AABB(matrix.getBlockPos()).inflate(10.0D);
    }

    private static void sendInfusionSourceFx(RunicMatrixBlockEntity matrix, Level level, BlockPos source, int ticks,
            int color) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, matrix.getBlockPos().getX(),
                    matrix.getBlockPos().getY(), matrix.getBlockPos().getZ(), 32.0D,
                    new InfusionSourceFxPayload(matrix.getBlockPos(), source, color, ticks, -1));
        }
    }

    private static void sendInfusionEntitySourceFx(RunicMatrixBlockEntity matrix, Level level, int entityId,
            int ticks) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, matrix.getBlockPos().getX(),
                    matrix.getBlockPos().getY(), matrix.getBlockPos().getZ(), 32.0D,
                    new InfusionSourceFxPayload(matrix.getBlockPos(), matrix.getBlockPos(), 0, ticks, entityId));
        }
    }

    private static void sendBlockZapFx(RunicMatrixBlockEntity matrix, Level level, double fromX, double fromY,
            double fromZ, double toX, double toY, double toZ) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, matrix.getBlockPos().getX(),
                    matrix.getBlockPos().getY(), matrix.getBlockPos().getZ(), 32.0D,
                    new BlockZapFxPayload(fromX, fromY, fromZ, toX, toY, toZ));
        }
    }

    private static void sendPedestalSparkleFx(RunicMatrixBlockEntity matrix, Level level, BlockPos pedestalPos,
            int eventId) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, matrix.getBlockPos().getX(),
                    matrix.getBlockPos().getY(), matrix.getBlockPos().getZ(), 32.0D,
                    new PedestalSparkleFxPayload(pedestalPos, eventId));
        }
    }
}
