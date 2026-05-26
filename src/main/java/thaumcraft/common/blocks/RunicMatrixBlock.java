package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class RunicMatrixBlock extends Block implements EntityBlock {
    public static final MapCodec<RunicMatrixBlock> CODEC = simpleCodec(RunicMatrixBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public RunicMatrixBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends RunicMatrixBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicMatrixBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.RUNIC_MATRIX.get()
                ? (tickLevel, pos, tickState, blockEntity) -> RunicMatrixBlockEntity.tick(tickLevel, pos, tickState,
                        (RunicMatrixBlockEntity) blockEntity)
                : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof RunicMatrixBlockEntity matrix) {
            matrix.requestSurroundingsCheck();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            InfusionPillarBlock.restoreAltarFromMatrix(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
