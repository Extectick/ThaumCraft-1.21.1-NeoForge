package thaumcraft.common.blocks;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.InfusionPillarBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.registry.TCBlocks;

public class InfusionPillarBlock extends Block implements EntityBlock {
    public static final MapCodec<InfusionPillarBlock> CODEC = simpleCodec(InfusionPillarBlock::new);
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    private static final VoxelShape LOWER_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape UPPER_SHAPE = Shapes.empty();
    private static boolean restoringAltar;

    public InfusionPillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CORNER, Corner.NORTH_WEST)
                .setValue(TOP, false));
    }

    @Override
    protected MapCodec<? extends InfusionPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CORNER, TOP);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionPillarBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForState(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return shapeForState(state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !restoringAltar) {
            collapseAltar(state, level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    private static VoxelShape shapeForState(BlockState state) {
        return state.getValue(TOP) ? UPPER_SHAPE : LOWER_SHAPE;
    }

    public static void restoreAltarFromMatrix(Level level, BlockPos matrixPos) {
        if (level.isClientSide) {
            return;
        }

        restoringAltar = true;
        try {
            for (Corner corner : Corner.values()) {
                BlockPos lowerPos = lowerPillarFromMatrix(matrixPos, corner);
                BlockPos upperPos = lowerPos.above();
                if (isPillarHalf(level.getBlockState(lowerPos), corner, false)) {
                    level.setBlock(lowerPos, TCBlocks.ARCANE_STONE_BRICKS.get().defaultBlockState(), 3);
                }
                if (isPillarHalf(level.getBlockState(upperPos), corner, true)) {
                    level.setBlock(upperPos, TCBlocks.ARCANE_STONE.get().defaultBlockState(), 3);
                }
            }
        } finally {
            restoringAltar = false;
        }
    }

    private static void collapseAltar(BlockState brokenState, Level level, BlockPos brokenPos) {
        if (level.isClientSide) {
            return;
        }

        BlockPos brokenLowerPos = brokenState.getValue(TOP) ? brokenPos.below() : brokenPos;
        Corner brokenCorner = brokenState.getValue(CORNER);
        BlockPos matrixPos = matrixPosFromLowerPillar(brokenLowerPos, brokenCorner);
        if (level.getBlockEntity(matrixPos) instanceof RunicMatrixBlockEntity matrix) {
            matrix.setActive(false);
            matrix.requestSurroundingsCheck();
        }

        restoringAltar = true;
        try {
            for (Corner corner : Corner.values()) {
                BlockPos lowerPos = lowerPillarFromMatrix(matrixPos, corner);
                BlockPos upperPos = lowerPos.above();
                if (corner == brokenCorner) {
                    dropOriginalPair(level, lowerPos);
                    if (!lowerPos.equals(brokenPos) && isPillarHalf(level.getBlockState(lowerPos), corner, false)) {
                        level.removeBlock(lowerPos, false);
                    }
                    if (!upperPos.equals(brokenPos) && isPillarHalf(level.getBlockState(upperPos), corner, true)) {
                        level.removeBlock(upperPos, false);
                    }
                    continue;
                }

                if (isPillarHalf(level.getBlockState(lowerPos), corner, false)) {
                    level.setBlock(lowerPos, TCBlocks.ARCANE_STONE_BRICKS.get().defaultBlockState(), 3);
                }
                if (isPillarHalf(level.getBlockState(upperPos), corner, true)) {
                    level.setBlock(upperPos, TCBlocks.ARCANE_STONE.get().defaultBlockState(), 3);
                }
            }
        } finally {
            restoringAltar = false;
        }
    }

    private static void dropOriginalPair(Level level, BlockPos pos) {
        popResource(level, pos, new ItemStack(TCBlocks.ARCANE_STONE_BRICKS.get()));
        popResource(level, pos, new ItemStack(TCBlocks.ARCANE_STONE.get()));
    }

    private static boolean isPillarHalf(BlockState state, Corner corner, boolean top) {
        return state.getBlock() instanceof InfusionPillarBlock
                && state.getValue(CORNER) == corner
                && state.getValue(TOP) == top;
    }

    private static BlockPos matrixPosFromLowerPillar(BlockPos lowerPos, Corner corner) {
        return switch (corner) {
            case NORTH_WEST -> lowerPos.offset(1, 2, 1);
            case NORTH_EAST -> lowerPos.offset(-1, 2, 1);
            case SOUTH_EAST -> lowerPos.offset(-1, 2, -1);
            case SOUTH_WEST -> lowerPos.offset(1, 2, -1);
        };
    }

    private static BlockPos lowerPillarFromMatrix(BlockPos matrixPos, Corner corner) {
        return switch (corner) {
            case NORTH_WEST -> matrixPos.offset(-1, -2, -1);
            case NORTH_EAST -> matrixPos.offset(1, -2, -1);
            case SOUTH_EAST -> matrixPos.offset(1, -2, 1);
            case SOUTH_WEST -> matrixPos.offset(-1, -2, 1);
        };
    }

    public enum Corner implements StringRepresentable {
        NORTH_WEST("north_west"),
        NORTH_EAST("north_east"),
        SOUTH_EAST("south_east"),
        SOUTH_WEST("south_west");

        private final String name;

        Corner(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
