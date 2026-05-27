package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.registry.TCItems;

public class SimpleTubeBlock extends Block {
    public static final MapCodec<SimpleTubeBlock> CODEC = simpleCodec(SimpleTubeBlock::new);
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty DOWN_EXTENDED = BooleanProperty.create("down_extended");
    public static final BooleanProperty UP_EXTENDED = BooleanProperty.create("up_extended");
    public static final BooleanProperty NORTH_EXTENDED = BooleanProperty.create("north_extended");
    public static final BooleanProperty SOUTH_EXTENDED = BooleanProperty.create("south_extended");
    public static final BooleanProperty WEST_EXTENDED = BooleanProperty.create("west_extended");
    public static final BooleanProperty EAST_EXTENDED = BooleanProperty.create("east_extended");

    private static final VoxelShape CORE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final VoxelShape DOWN_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D);
    private static final VoxelShape UP_SHAPE = Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape NORTH_SHAPE = Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 6.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(7.0D, 7.0D, 10.0D, 9.0D, 9.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0D, 7.0D, 7.0D, 6.0D, 9.0D, 9.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(10.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D);
    private static final VoxelShape WAND_CORE = Block.box(5.5D, 5.5D, 5.5D, 10.5D, 10.5D, 10.5D);
    private static final VoxelShape WAND_DOWN_SHAPE = Block.box(6.72D, 0.0D, 6.72D, 9.28D, 8.0D, 9.28D);
    private static final VoxelShape WAND_UP_SHAPE = Block.box(6.72D, 8.0D, 6.72D, 9.28D, 16.0D, 9.28D);
    private static final VoxelShape WAND_NORTH_SHAPE = Block.box(6.72D, 6.72D, 0.0D, 9.28D, 9.28D, 8.0D);
    private static final VoxelShape WAND_SOUTH_SHAPE = Block.box(6.72D, 6.72D, 8.0D, 9.28D, 9.28D, 16.0D);
    private static final VoxelShape WAND_WEST_SHAPE = Block.box(0.0D, 6.72D, 6.72D, 8.0D, 9.28D, 9.28D);
    private static final VoxelShape WAND_EAST_SHAPE = Block.box(8.0D, 6.72D, 6.72D, 16.0D, 9.28D, 9.28D);
    private static final VoxelShape[] SHAPES = buildShapeCache(CORE, DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE,
            WEST_SHAPE, EAST_SHAPE);
    private static final VoxelShape[] WAND_SHAPES = buildShapeCache(WAND_CORE, WAND_DOWN_SHAPE, WAND_UP_SHAPE,
            WAND_NORTH_SHAPE, WAND_SOUTH_SHAPE, WAND_WEST_SHAPE, WAND_EAST_SHAPE);

    public SimpleTubeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(DOWN_EXTENDED, false)
                .setValue(UP_EXTENDED, false)
                .setValue(NORTH_EXTENDED, false)
                .setValue(SOUTH_EXTENDED, false)
                .setValue(WEST_EXTENDED, false)
                .setValue(EAST_EXTENDED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(DOWN, canConnect(level, pos, Direction.DOWN))
                .setValue(UP, canConnect(level, pos, Direction.UP))
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .setValue(WEST, canConnect(level, pos, Direction.WEST))
                .setValue(EAST, canConnect(level, pos, Direction.EAST))
                .setValue(DOWN_EXTENDED, isExtendedConnection(level, pos, Direction.DOWN))
                .setValue(UP_EXTENDED, isExtendedConnection(level, pos, Direction.UP))
                .setValue(NORTH_EXTENDED, isExtendedConnection(level, pos, Direction.NORTH))
                .setValue(SOUTH_EXTENDED, isExtendedConnection(level, pos, Direction.SOUTH))
                .setValue(WEST_EXTENDED, isExtendedConnection(level, pos, Direction.WEST))
                .setValue(EAST_EXTENDED, isExtendedConnection(level, pos, Direction.EAST));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(propertyFor(direction), canConnect(level, pos, direction))
                .setValue(extendedPropertyFor(direction), isExtendedConnection(level, pos, direction));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            level.setBlock(pos, stateWithConnections(level, pos, state), 3);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context.isHoldingItem(TCItems.WAND_CASTING.get())) {
            return getWandShape(level, pos);
        }
        return SHAPES[connectionMask(state)];
    }

    @Override
    protected MapCodec<? extends SimpleTubeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, DOWN_EXTENDED, UP_EXTENDED, NORTH_EXTENDED, SOUTH_EXTENDED,
                WEST_EXTENDED, EAST_EXTENDED);
    }

    public BlockState stateWithConnections(BlockGetter level, BlockPos pos, BlockState state) {
        return state.setValue(DOWN, canConnect(level, pos, Direction.DOWN))
                .setValue(UP, canConnect(level, pos, Direction.UP))
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .setValue(WEST, canConnect(level, pos, Direction.WEST))
                .setValue(EAST, canConnect(level, pos, Direction.EAST))
                .setValue(DOWN_EXTENDED, isExtendedConnection(level, pos, Direction.DOWN))
                .setValue(UP_EXTENDED, isExtendedConnection(level, pos, Direction.UP))
                .setValue(NORTH_EXTENDED, isExtendedConnection(level, pos, Direction.NORTH))
                .setValue(SOUTH_EXTENDED, isExtendedConnection(level, pos, Direction.SOUTH))
                .setValue(WEST_EXTENDED, isExtendedConnection(level, pos, Direction.WEST))
                .setValue(EAST_EXTENDED, isExtendedConnection(level, pos, Direction.EAST));
    }

    private static boolean canConnect(BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof IEssentiaTransport self && !self.isConnectable(direction)) {
            return false;
        }
        return level.getBlockEntity(pos.relative(direction)) instanceof IEssentiaTransport transport
                && transport.isConnectable(direction.getOpposite());
    }

    private static VoxelShape getWandShape(BlockGetter level, BlockPos pos) {
        int mask = 0;
        if (canTraceSide(level, pos, Direction.DOWN)) {
            mask |= 1;
        }
        if (canTraceSide(level, pos, Direction.UP)) {
            mask |= 1 << 1;
        }
        if (canTraceSide(level, pos, Direction.NORTH)) {
            mask |= 1 << 2;
        }
        if (canTraceSide(level, pos, Direction.SOUTH)) {
            mask |= 1 << 3;
        }
        if (canTraceSide(level, pos, Direction.WEST)) {
            mask |= 1 << 4;
        }
        if (canTraceSide(level, pos, Direction.EAST)) {
            mask |= 1 << 5;
        }
        return WAND_SHAPES[mask];
    }

    private static VoxelShape[] buildShapeCache(VoxelShape core, VoxelShape down, VoxelShape up, VoxelShape north,
            VoxelShape south, VoxelShape west, VoxelShape east) {
        VoxelShape[] cache = new VoxelShape[64];
        for (int mask = 0; mask < cache.length; mask++) {
            VoxelShape shape = core;
            if ((mask & 1) != 0) {
                shape = Shapes.or(shape, down);
            }
            if ((mask & (1 << 1)) != 0) {
                shape = Shapes.or(shape, up);
            }
            if ((mask & (1 << 2)) != 0) {
                shape = Shapes.or(shape, north);
            }
            if ((mask & (1 << 3)) != 0) {
                shape = Shapes.or(shape, south);
            }
            if ((mask & (1 << 4)) != 0) {
                shape = Shapes.or(shape, west);
            }
            if ((mask & (1 << 5)) != 0) {
                shape = Shapes.or(shape, east);
            }
            cache[mask] = shape;
        }
        return cache;
    }

    private static int connectionMask(BlockState state) {
        int mask = 0;
        if (state.getValue(DOWN)) {
            mask |= 1;
        }
        if (state.getValue(UP)) {
            mask |= 1 << 1;
        }
        if (state.getValue(NORTH)) {
            mask |= 1 << 2;
        }
        if (state.getValue(SOUTH)) {
            mask |= 1 << 3;
        }
        if (state.getValue(WEST)) {
            mask |= 1 << 4;
        }
        if (state.getValue(EAST)) {
            mask |= 1 << 5;
        }
        return mask;
    }

    private static boolean canTraceSide(BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos.relative(direction)) instanceof IEssentiaTransport;
    }

    private static boolean isExtendedConnection(BlockGetter level, BlockPos pos, Direction direction) {
        return canConnect(level, pos, direction)
                && level.getBlockEntity(pos.relative(direction)) instanceof IEssentiaTransport transport
                && transport.renderExtendedTube();
    }

    private static BooleanProperty propertyFor(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private static BooleanProperty extendedPropertyFor(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN_EXTENDED;
            case UP -> UP_EXTENDED;
            case NORTH -> NORTH_EXTENDED;
            case SOUTH -> SOUTH_EXTENDED;
            case WEST -> WEST_EXTENDED;
            case EAST -> EAST_EXTENDED;
        };
    }
}
