package thaumcraft.common.world.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

final class TreeGenerationUtil {
    private TreeGenerationUtil() {
    }

    static boolean isSoil(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.FARMLAND)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM);
    }

    static boolean canGrowThrough(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.canBeReplaced();
    }

    static boolean canReplaceWithLeaves(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.canBeReplaced();
    }

    static void setLeaf(Level level, BlockPos pos, BlockState leaves) {
        if (!canReplaceWithLeaves(level, pos)) {
            return;
        }
        if (leaves.hasProperty(LeavesBlock.PERSISTENT)) {
            leaves = leaves.setValue(LeavesBlock.PERSISTENT, false);
        }
        if (leaves.hasProperty(LeavesBlock.DISTANCE)) {
            leaves = leaves.setValue(LeavesBlock.DISTANCE, 1);
        }
        level.setBlock(pos, leaves, 3);
    }

    static void setLog(Level level, BlockPos pos, BlockState log, Direction.Axis axis) {
        if (log.hasProperty(RotatedPillarBlock.AXIS)) {
            log = log.setValue(RotatedPillarBlock.AXIS, axis);
        }
        level.setBlock(pos, log, 3);
    }
}
