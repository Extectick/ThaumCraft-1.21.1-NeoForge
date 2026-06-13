package thaumcraft.common.blocks;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.NodeTransducerBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class NodeTransducerBlock extends Block implements EntityBlock {
    public static final MapCodec<NodeTransducerBlock> CODEC = simpleCodec(NodeTransducerBlock::new);

    public NodeTransducerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends NodeTransducerBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NodeTransducerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.NODE_TRANSDUCER.get()
                ? (tickLevel, pos, tickState, blockEntity) -> NodeTransducerBlockEntity.tick(
                        tickLevel, pos, tickState, (NodeTransducerBlockEntity) blockEntity)
                : null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof NodeTransducerBlockEntity transducer) {
            transducer.requestStatusCheck();
        }
    }
}
