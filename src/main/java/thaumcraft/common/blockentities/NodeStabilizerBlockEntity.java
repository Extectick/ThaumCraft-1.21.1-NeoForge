package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

public class NodeStabilizerBlockEntity extends BlockEntity {
    private int animationCount;

    public NodeStabilizerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.NODE_STABILIZER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, NodeStabilizerBlockEntity stabilizer) {
        if (!level.isClientSide) {
            return;
        }

        boolean active = pos.getY() < level.getMaxBuildHeight() - 1
                && !level.hasNeighborSignal(pos)
                && level.getBlockState(pos.above()).is(TCBlocks.AURA_NODE.get())
                && level.getBlockEntity(pos.above()) instanceof AuraNodeBlockEntity;
        if (active && stabilizer.animationCount < 37) {
            stabilizer.animationCount++;
        } else if (!active && stabilizer.animationCount > 0) {
            stabilizer.animationCount--;
        }
    }

    public int getAnimationCount() {
        return this.animationCount;
    }

    public boolean isAdvanced() {
        return this.getBlockState().is(TCBlocks.ADVANCED_NODE_STABILIZER.get());
    }

}
