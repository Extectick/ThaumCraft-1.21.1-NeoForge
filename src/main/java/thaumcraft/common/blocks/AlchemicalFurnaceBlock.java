package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;

public class AlchemicalFurnaceBlock extends Block implements EntityBlock {
    public static final MapCodec<AlchemicalFurnaceBlock> CODEC = simpleCodec(AlchemicalFurnaceBlock::new);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public AlchemicalFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(FILLED, false));
    }

    @Override
    protected MapCodec<? extends AlchemicalFurnaceBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTicker(blockEntityType, TCBlockEntities.ALCHEMICAL_FURNACE.get());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof AlchemicalFurnaceBlockEntity furnace
                ? AbstractContainerMenu.getRedstoneSignalFromContainer(furnace)
                : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AlchemicalFurnaceBlockEntity furnace)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int slot = AlchemicalFurnaceBlockEntity.isItemFuel(stack)
                ? AlchemicalFurnaceBlockEntity.FUEL_SLOT
                : AlchemicalFurnaceBlockEntity.INPUT_SLOT;
        if (!furnace.canPlaceItem(slot, stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return level.isClientSide ? ItemInteractionResult.SUCCESS : insertHeldStack(stack, furnace, player, hand, slot);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AlchemicalFurnaceBlockEntity furnace)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            if (extractSlot(furnace, player, AlchemicalFurnaceBlockEntity.FUEL_SLOT)
                    || extractSlot(furnace, player, AlchemicalFurnaceBlockEntity.INPUT_SLOT)) {
                level.playSound(null, pos, TCSoundEvents.HHOFF.get(), SoundSource.BLOCKS, 0.25F, 1.0F);
            }
            return InteractionResult.CONSUME;
        }

        player.openMenu(furnace);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.2D + random.nextFloat() * 5.0D / 16.0D;
        double z = pos.getZ() + 0.5D;
        double offset = 0.52D;
        double spread = random.nextFloat() * 0.5D - 0.25D;
        level.addParticle(ParticleTypes.SMOKE, x - offset, y, z + spread, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x - offset, y, z + spread, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.SMOKE, x + offset, y, z + spread, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x + offset, y, z + spread, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.SMOKE, x + spread, y, z - offset, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x + spread, y, z - offset, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.SMOKE, x + spread, y, z + offset, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x + spread, y, z + offset, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FILLED);
    }

    private static ItemInteractionResult insertHeldStack(ItemStack heldStack, AlchemicalFurnaceBlockEntity furnace,
            Player player, InteractionHand hand, int slot) {
        ItemStack stored = furnace.getItem(slot);
        if (!stored.isEmpty() && (!ItemStack.isSameItemSameComponents(stored, heldStack)
                || stored.getCount() >= stored.getMaxStackSize())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack inserted = heldStack.split(1);
        if (stored.isEmpty()) {
            furnace.setItem(slot, inserted);
        } else {
            stored.grow(1);
            furnace.setItem(slot, stored);
        }
        player.setItemInHand(hand, heldStack);
        player.level().playSound(null, player.blockPosition(), TCSoundEvents.HHON.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    private static boolean extractSlot(AlchemicalFurnaceBlockEntity furnace, Player player, int slot) {
        ItemStack stack = furnace.removeItemNoUpdate(slot);
        if (stack.isEmpty()) {
            return false;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        furnace.setChanged();
        return true;
    }

    @Nullable
    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<T> actual,
            BlockEntityType<AlchemicalFurnaceBlockEntity> expected) {
        return actual == expected ? (level, pos, state, blockEntity) -> AlchemicalFurnaceBlockEntity.serverTick(level,
                pos, state, (AlchemicalFurnaceBlockEntity) blockEntity) : null;
    }
}
