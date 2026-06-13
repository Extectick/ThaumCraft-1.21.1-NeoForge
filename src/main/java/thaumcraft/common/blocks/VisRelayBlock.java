package thaumcraft.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.VisRelayBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;

public class VisRelayBlock extends SimpleDirectionalBlock implements EntityBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private static final VoxelShape SHAPE_DOWN = box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D);
    private static final VoxelShape SHAPE_UP = box(5.0D, 8.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape SHAPE_NORTH = box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 8.0D);
    private static final VoxelShape SHAPE_SOUTH = box(5.0D, 5.0D, 8.0D, 11.0D, 11.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = box(0.0D, 5.0D, 5.0D, 8.0D, 11.0D, 11.0D);
    private static final VoxelShape SHAPE_EAST = box(8.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);

    public VisRelayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.FACING, Direction.UP)
                .setValue(CONNECTED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, CONNECTED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing) {
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case SOUTH -> SHAPE_SOUTH;
            case NORTH -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, net.minecraft.world.phys.BlockHitResult hitResult) {
        return tuneWithShard(stack, level, pos);
    }

    public static ItemInteractionResult tuneWithShard(ItemStack stack, Level level, BlockPos pos) {
        byte color = shardColor(stack);
        if (color < -1) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.getBlockEntity(pos) instanceof VisRelayBlockEntity relay) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            relay.setColor(relay.getAttunement() == color ? (byte) -1 : color);
            level.playSound(null, pos, TCSoundEvents.CRYSTAL.get(), SoundSource.BLOCKS, 0.2F, 1.0F);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static byte shardColor(ItemStack stack) {
        if (stack.is(TCItems.AIR_SHARD.get())) {
            return 0;
        }
        if (stack.is(TCItems.FIRE_SHARD.get())) {
            return 1;
        }
        if (stack.is(TCItems.WATER_SHARD.get())) {
            return 2;
        }
        if (stack.is(TCItems.EARTH_SHARD.get())) {
            return 3;
        }
        if (stack.is(TCItems.ORDER_SHARD.get())) {
            return 4;
        }
        if (stack.is(TCItems.ENTROPY_SHARD.get())) {
            return 5;
        }
        if (stack.is(TCItems.BALANCED_SHARD.get())) {
            return -1;
        }
        return -2;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VisRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.VIS_RELAY.get()
                ? (tickLevel, pos, tickState, blockEntity) -> VisRelayBlockEntity.tick(
                        tickLevel, pos, tickState, (VisRelayBlockEntity) blockEntity)
                : null;
    }
}
