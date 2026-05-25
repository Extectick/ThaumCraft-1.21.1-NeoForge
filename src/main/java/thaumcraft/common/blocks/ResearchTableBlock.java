package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public class ResearchTableBlock extends SimpleTableBlock implements EntityBlock {
    public static final MapCodec<ResearchTableBlock> CODEC = simpleCodec(ResearchTableBlock::new);
    public static final BooleanProperty PRIMARY = BooleanProperty.create("primary");

    private static final VoxelShape WHOLE_EAST_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 32.0, 16.0, 16.0),
            Block.box(2.0, 0.0, 2.0, 6.0, 12.0, 6.0),
            Block.box(2.0, 0.0, 10.0, 6.0, 12.0, 14.0),
            Block.box(26.0, 0.0, 2.0, 30.0, 12.0, 6.0),
            Block.box(26.0, 0.0, 10.0, 30.0, 12.0, 14.0),
            Block.box(4.0, 2.0, 6.0, 28.0, 6.0, 10.0));
    private static final VoxelShape WHOLE_WEST_SHAPE = Shapes.or(
            Block.box(-16.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(10.0, 0.0, 2.0, 14.0, 12.0, 6.0),
            Block.box(10.0, 0.0, 10.0, 14.0, 12.0, 14.0),
            Block.box(-14.0, 0.0, 2.0, -10.0, 12.0, 6.0),
            Block.box(-14.0, 0.0, 10.0, -10.0, 12.0, 14.0),
            Block.box(-12.0, 2.0, 6.0, 12.0, 6.0, 10.0));
    private static final VoxelShape WHOLE_SOUTH_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 32.0),
            Block.box(2.0, 0.0, 2.0, 6.0, 12.0, 6.0),
            Block.box(10.0, 0.0, 2.0, 14.0, 12.0, 6.0),
            Block.box(2.0, 0.0, 26.0, 6.0, 12.0, 30.0),
            Block.box(10.0, 0.0, 26.0, 14.0, 12.0, 30.0),
            Block.box(6.0, 2.0, 4.0, 10.0, 6.0, 28.0));
    private static final VoxelShape WHOLE_NORTH_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, -16.0, 16.0, 16.0, 16.0),
            Block.box(2.0, 0.0, 10.0, 6.0, 12.0, 14.0),
            Block.box(10.0, 0.0, 10.0, 14.0, 12.0, 14.0),
            Block.box(2.0, 0.0, -14.0, 6.0, 12.0, -10.0),
            Block.box(10.0, 0.0, -14.0, 14.0, 12.0, -10.0),
            Block.box(6.0, 2.0, -12.0, 10.0, 6.0, 12.0));
    private static final VoxelShape PRIMARY_X_COLLISION = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(2.0, 0.0, 2.0, 6.0, 12.0, 6.0),
            Block.box(2.0, 0.0, 10.0, 6.0, 12.0, 14.0),
            Block.box(4.0, 2.0, 6.0, 16.0, 6.0, 10.0));
    private static final VoxelShape SECONDARY_X_COLLISION = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(10.0, 0.0, 2.0, 14.0, 12.0, 6.0),
            Block.box(10.0, 0.0, 10.0, 14.0, 12.0, 14.0),
            Block.box(0.0, 2.0, 6.0, 12.0, 6.0, 10.0));
    private static final VoxelShape PRIMARY_Z_COLLISION = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(2.0, 0.0, 2.0, 6.0, 12.0, 6.0),
            Block.box(10.0, 0.0, 2.0, 14.0, 12.0, 6.0),
            Block.box(6.0, 2.0, 4.0, 10.0, 6.0, 16.0));
    private static final VoxelShape SECONDARY_Z_COLLISION = Shapes.or(
            Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(2.0, 0.0, 10.0, 6.0, 12.0, 14.0),
            Block.box(10.0, 0.0, 10.0, 14.0, 12.0, 14.0),
            Block.box(6.0, 2.0, 0.0, 10.0, 6.0, 12.0));

    public ResearchTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(PRIMARY, true));
    }

    @Override
    protected MapCodec<? extends ResearchTableBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResearchTableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return !level.isClientSide && state.getValue(PRIMARY) && blockEntityType == TCBlockEntities.RESEARCH_TABLE.get()
                ? (tickLevel, pos, tickState, blockEntity) -> ResearchTableBlockEntity.serverTick(tickLevel, pos,
                        tickState, (ResearchTableBlockEntity) blockEntity)
                : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        return openResearchMenu(state, level, pos, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = openResearchMenu(state, level, pos, player);
        return result.consumesAction() ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static InteractionResult openResearchMenu(BlockState state, Level level, BlockPos pos, Player player) {
        BlockPos menuPos = state.getValue(PRIMARY) ? pos : getOtherHalfPos(pos, state);
        BlockEntity blockEntity = level.getBlockEntity(menuPos);
        if (!(blockEntity instanceof ResearchTableBlockEntity researchTable)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(researchTable);
        level.playSound(null, pos, TCSoundEvents.PAGE.get(), SoundSource.BLOCKS, 0.25F, 1.0F);
        return InteractionResult.CONSUME;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return wholeShape(state.getValue(FACING));
    }

    private static VoxelShape wholeShape(Direction direction) {
        return switch (direction) {
            case EAST -> WHOLE_EAST_SHAPE;
            case WEST -> WHOLE_WEST_SHAPE;
            case SOUTH -> WHOLE_SOUTH_SHAPE;
            case NORTH -> WHOLE_NORTH_SHAPE;
            default -> WHOLE_EAST_SHAPE;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean primary = state.getValue(PRIMARY);
        return state.getValue(FACING).getAxis() == Direction.Axis.X
                ? (primary ? PRIMARY_X_COLLISION : SECONDARY_X_COLLISION)
                : (primary ? PRIMARY_Z_COLLISION : SECONDARY_Z_COLLISION);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction linkDirection = state.getValue(FACING);
        if (direction == linkDirection && !isValidOtherHalf(state, neighborState)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(PRIMARY) && level.getBlockEntity(pos) instanceof ResearchTableBlockEntity researchTable) {
                Containers.dropContents(level, pos, researchTable);
            }
            BlockPos otherPos = getOtherHalfPos(pos, state);
            BlockState otherState = level.getBlockState(otherPos);
            if (isValidOtherHalf(state, otherState)) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(2001, otherPos, Block.getId(otherState));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BlockPos otherPos = getOtherHalfPos(pos, state);
            BlockState otherState = level.getBlockState(otherPos);
            if (isValidOtherHalf(state, otherState)) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getClockWise();
        BlockPos otherPos = context.getClickedPos().relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(otherPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(otherPos)
                ? this.defaultBlockState().setValue(FACING, direction).setValue(PRIMARY, true)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos otherPos = pos.relative(state.getValue(FACING));
            level.setBlock(otherPos, state.setValue(FACING, state.getValue(FACING).getOpposite())
                    .setValue(PRIMARY, false), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PRIMARY);
    }

    public static BlockPos getOtherHalfPos(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING));
    }

    private static boolean isValidOtherHalf(BlockState state, BlockState otherState) {
        return otherState.is(TCBlocks.RESEARCH_TABLE.get())
                && otherState.getValue(PRIMARY) != state.getValue(PRIMARY)
                && otherState.getValue(FACING) == state.getValue(FACING).getOpposite();
    }
}
