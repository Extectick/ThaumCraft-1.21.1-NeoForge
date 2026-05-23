package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.lib.crafting.ArcaneWorktableRecipes;
import thaumcraft.common.registry.TCSoundEvents;

public class ArcaneWorktableBlock extends SimpleTableBlock implements EntityBlock {
    public static final MapCodec<ArcaneWorktableBlock> CODEC = simpleCodec(ArcaneWorktableBlock::new);

    public ArcaneWorktableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ArcaneWorktableBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneWorktableBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ArcaneWorktableBlockEntity worktable)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.getItem() instanceof WandCastingItem) {
            if (player.isShiftKeyDown()) {
                return level.isClientSide ? ItemInteractionResult.SUCCESS
                        : insertOrExtractWand(stack, worktable, player, hand);
            }
            return level.isClientSide ? ItemInteractionResult.SUCCESS : craftWithWand(level, pos, worktable, stack, player);
        }

        if (player.isShiftKeyDown()) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : extractLastGridItem(worktable, player);
        }

        return level.isClientSide ? ItemInteractionResult.SUCCESS : insertGridItem(stack, worktable, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ArcaneWorktableBlockEntity worktable)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            player.openMenu(worktable);
            return InteractionResult.CONSUME;
        }

        if (!worktable.getWand().isEmpty()) {
            ItemStack wand = worktable.removeItemNoUpdate(ArcaneWorktableBlockEntity.WAND_SLOT);
            if (!player.getInventory().add(wand)) {
                player.drop(wand, false);
            }
            worktable.setChanged();
            return InteractionResult.CONSUME;
        }

        extractLastGridItem(worktable, player);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static ItemInteractionResult craftWithWand(Level level, BlockPos pos, ArcaneWorktableBlockEntity worktable,
            ItemStack wand, Player player) {
        if (ArcaneWorktableRecipes.tryCraft(level, worktable, wand, player)) {
            level.playSound(null, pos, TCSoundEvents.WAND.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
            return ItemInteractionResult.CONSUME;
        }

        player.displayClientMessage(Component.translatable("container.thaumcraft.arcane_worktable.no_recipe"), true);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult insertOrExtractWand(ItemStack heldStack, ArcaneWorktableBlockEntity worktable,
            Player player, InteractionHand hand) {
        ItemStack storedWand = worktable.getWand();
        if (storedWand.isEmpty()) {
            worktable.setWand(heldStack.split(1));
        } else if (heldStack.isEmpty()) {
            player.setItemInHand(hand, storedWand);
            worktable.setWand(ItemStack.EMPTY);
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult insertGridItem(ItemStack heldStack, ArcaneWorktableBlockEntity worktable,
            Player player) {
        if (heldStack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        for (int slot = 0; slot < ArcaneWorktableBlockEntity.GRID_SIZE; slot++) {
            if (worktable.getItem(slot).isEmpty()) {
                worktable.setItem(slot, heldStack.split(1));
                return ItemInteractionResult.CONSUME;
            }
        }

        player.displayClientMessage(Component.translatable("container.thaumcraft.arcane_worktable.full"), true);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult extractLastGridItem(ArcaneWorktableBlockEntity worktable, Player player) {
        for (int slot = ArcaneWorktableBlockEntity.GRID_SIZE - 1; slot >= 0; slot--) {
            ItemStack stack = worktable.removeItemNoUpdate(slot);
            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                worktable.setChanged();
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.CONSUME;
    }
}
