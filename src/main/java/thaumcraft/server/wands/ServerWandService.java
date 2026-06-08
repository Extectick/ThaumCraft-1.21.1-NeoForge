package thaumcraft.server.wands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.server.infusion.InfusionAltarBuilder;
import thaumcraft.server.infusion.InfusionCrafting;
import thaumcraft.server.aura.ServerNodeJarService;

public final class ServerWandService {
    private ServerWandService() {
    }

    public static InteractionResultHolder<ItemStack> use(WandCastingItem wandItem, Level level, Player player,
            InteractionHand hand, ItemStack wand) {
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);

        if (WandFocusHelper.isFocus(other) && !WandFocusHelper.hasFocus(wand)) {
            if (!level.isClientSide) {
                WandFocusHelper.setFocus(wand, other);
                if (!player.getAbilities().instabuild) {
                    other.shrink(1);
                }
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.set",
                        WandFocusHelper.getFocusItem(wand).getHoverName()), true);
                level.playSound(null, player.blockPosition(), TCSoundEvents.HHON.get(), SoundSource.PLAYERS, 0.35F,
                        1.0F);
            }
            return InteractionResultHolder.sidedSuccess(wand, level.isClientSide);
        }

        if (player.isShiftKeyDown() && WandFocusHelper.hasFocus(wand)) {
            if (!level.isClientSide) {
                ItemStack focus = WandFocusHelper.removeFocus(wand);
                if (!FocusPouchCurioItem.addFocusToEquipped(player, focus) && !player.getInventory().add(focus)) {
                    player.drop(focus, false);
                }
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.removed"), true);
                level.playSound(null, player.blockPosition(), TCSoundEvents.HHOFF.get(), SoundSource.PLAYERS, 0.35F,
                        1.0F);
            }
            return InteractionResultHolder.sidedSuccess(wand, level.isClientSide);
        }

        return InteractionResultHolder.pass(wand);
    }

    public static InteractionResult useOnAfterWandable(WandCastingItem wandItem, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack wand = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        InteractionResult nodeJarResult = ServerNodeJarService.tryCapture(level, pos, wand, player);
        if (nodeJarResult != InteractionResult.PASS) {
            return nodeJarResult;
        }

        InteractionResult infusionCraftingResult = InfusionCrafting.tryStart(level, pos, player);
        if (infusionCraftingResult != InteractionResult.PASS) {
            return infusionCraftingResult;
        }

        InteractionResult infusionAltarResult = InfusionAltarBuilder.tryCreate(level, pos, wand, player);
        if (infusionAltarResult != InteractionResult.PASS) {
            return infusionAltarResult;
        }

        if (state.is(TCBlocks.TABLE.get())) {
            return createArcaneWorktable(level, pos, state, wand, player);
        }

        if (state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON)) {
            return createCrucible(level, pos);
        }

        if (state.is(Blocks.BOOKSHELF)) {
            return createThaumonomicon(level, pos);
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult createArcaneWorktable(Level level, BlockPos pos, BlockState state, ItemStack wand,
            Player player) {
        if (!level.isClientSide) {
            BlockState worktableState = TCBlocks.ARCANE_WORKTABLE.get().defaultBlockState()
                    .setValue(SimpleTableBlock.FACING, state.getValue(SimpleTableBlock.FACING));
            level.setBlock(pos, worktableState, 3);

            if (player == null || !player.getAbilities().instabuild) {
                if (level.getBlockEntity(pos) instanceof ArcaneWorktableBlockEntity worktable) {
                    worktable.setWand(wand.copyWithCount(1));
                    worktable.setChanged();
                }
                wand.shrink(1);
            }

            level.playSound(null, pos, TCSoundEvents.WAND.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
            level.playSound(null, pos, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static InteractionResult createThaumonomicon(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            level.removeBlock(pos, false);

            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                    new ItemStack(TCItems.THAUMONOMICON.get()));
            entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            entity.setNoGravity(true);
            serverLevel.addFreshEntity(entity);

            serverLevel.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 0.5D,
                    pos.getZ() + 0.5D, 32, 0.55D, 0.55D, 0.55D, 0.35D);
            level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    TCSoundEvents.WAND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static InteractionResult createCrucible(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState oldState = level.getBlockState(pos);
            int waterAmount = oldState.is(Blocks.WATER_CAULDRON)
                    ? oldState.getValue(LayeredCauldronBlock.LEVEL) * 1000 / 3
                    : 0;
            level.setBlock(pos, TCBlocks.CRUCIBLE.get().defaultBlockState(), 3);
            if (waterAmount > 0 && level.getBlockEntity(pos) instanceof thaumcraft.common.blockentities.CrucibleBlockEntity crucible) {
                crucible.addWater(waterAmount);
            }
            level.blockEvent(pos, TCBlocks.CRUCIBLE.get(), 1, 1);
            level.playSound(null, pos, TCSoundEvents.WAND.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
