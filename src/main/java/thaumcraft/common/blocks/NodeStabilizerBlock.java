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
import thaumcraft.common.blockentities.NodeStabilizerBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class NodeStabilizerBlock extends Block implements EntityBlock {
    private final MapCodec<NodeStabilizerBlock> codec;
    private final boolean advanced;

    public NodeStabilizerBlock(BlockBehaviour.Properties properties, boolean advanced) {
        super(properties);
        this.advanced = advanced;
        this.codec = simpleCodec(blockProperties -> new NodeStabilizerBlock(blockProperties, advanced));
    }

    @Override
    protected MapCodec<? extends NodeStabilizerBlock> codec() {
        return this.codec;
    }

    public boolean isAdvanced() {
        return this.advanced;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NodeStabilizerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.NODE_STABILIZER.get()
                ? (tickLevel, pos, tickState, blockEntity) -> NodeStabilizerBlockEntity.tick(
                        tickLevel, pos, tickState, (NodeStabilizerBlockEntity) blockEntity)
                : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
