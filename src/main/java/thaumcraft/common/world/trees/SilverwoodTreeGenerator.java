package thaumcraft.common.world.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.world.AuraNodeGenerator;

public final class SilverwoodTreeGenerator {
    private SilverwoodTreeGenerator() {
    }

    public static boolean generate(Level level, RandomSource random, BlockPos pos, int minTreeHeight, int randomTreeHeight) {
        int height = random.nextInt(randomTreeHeight) + minTreeHeight;
        if (pos.getY() < 1 || pos.getY() + height + 1 > level.getMaxBuildHeight()) {
            return false;
        }

        if (!hasRoom(level, pos, height)) {
            return false;
        }

        if (!TreeGenerationUtil.isSoil(level, pos.below())) {
            return false;
        }

        BlockState log = TCBlocks.SILVERWOOD_LOG.get().defaultBlockState();
        BlockState leaves = TCBlocks.SILVERWOOD_LEAVES.get().defaultBlockState();

        int start = pos.getY() + height - 5;
        int end = pos.getY() + height + 3 + random.nextInt(3);
        for (int y = start; y <= end; y++) {
            int centerY = Mth.clamp(y, pos.getY() + height - 3, pos.getY() + height);
            for (int x = pos.getX() - 5; x <= pos.getX() + 5; x++) {
                for (int z = pos.getZ() - 5; z <= pos.getZ() + 5; z++) {
                    double dx = x - pos.getX();
                    double dy = y - centerY;
                    double dz = z - pos.getZ();
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (distance < 10 + random.nextInt(8)) {
                        TreeGenerationUtil.setLeaf(level, new BlockPos(x, y, z), leaves);
                    }
                }
            }
        }

        int chance = (int) (height * 1.5D);
        boolean lastBlockWasNode = false;
        int trunkY;
        for (trunkY = 0; trunkY < height; trunkY++) {
            BlockPos trunkPos = pos.above(trunkY);
            if (TreeGenerationUtil.canGrowThrough(level, trunkPos)) {
                if (trunkY > 0 && !lastBlockWasNode && random.nextInt(chance) == 0) {
                    level.setBlock(trunkPos, TCBlocks.SILVERWOOD_KNOT.get().defaultBlockState(), 3);
                    AuraNodeGenerator.configureSilverwoodNode(level, trunkPos, random);
                    chance += height;
                    lastBlockWasNode = true;
                } else {
                    TreeGenerationUtil.setLog(level, trunkPos, log, Direction.Axis.Y);
                    lastBlockWasNode = false;
                }
                TreeGenerationUtil.setLog(level, trunkPos.offset(-1, 0, 0), log, Direction.Axis.Y);
                TreeGenerationUtil.setLog(level, trunkPos.offset(1, 0, 0), log, Direction.Axis.Y);
                TreeGenerationUtil.setLog(level, trunkPos.offset(0, 0, -1), log, Direction.Axis.Y);
                TreeGenerationUtil.setLog(level, trunkPos.offset(0, 0, 1), log, Direction.Axis.Y);
            }
        }

        TreeGenerationUtil.setLog(level, pos.above(trunkY), log, Direction.Axis.Y);
        placeBase(level, random, pos, height, log);
        return true;
    }

    private static boolean hasRoom(Level level, BlockPos pos, int height) {
        for (int y = pos.getY(); y <= pos.getY() + 1 + height; y++) {
            int spread = 1;
            if (y == pos.getY()) {
                spread = 0;
            }
            if (y >= pos.getY() + 1 + height - 2) {
                spread = 3;
            }
            for (int x = pos.getX() - spread; x <= pos.getX() + spread; x++) {
                for (int z = pos.getZ() - spread; z <= pos.getZ() + spread; z++) {
                    BlockPos check = new BlockPos(x, y, z);
                    if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
                        return false;
                    }
                    if (y > pos.getY() && !TreeGenerationUtil.canGrowThrough(level, check)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeBase(Level level, RandomSource random, BlockPos pos, int height, BlockState log) {
        TreeGenerationUtil.setLog(level, pos.offset(-1, 0, -1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(1, 0, 1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(-1, 0, 1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(1, 0, -1), log, Direction.Axis.Y);
        if (random.nextInt(3) != 0) {
            TreeGenerationUtil.setLog(level, pos.offset(-1, 1, -1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) != 0) {
            TreeGenerationUtil.setLog(level, pos.offset(1, 1, 1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) != 0) {
            TreeGenerationUtil.setLog(level, pos.offset(-1, 1, 1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) != 0) {
            TreeGenerationUtil.setLog(level, pos.offset(1, 1, -1), log, Direction.Axis.Y);
        }

        TreeGenerationUtil.setLog(level, pos.offset(-2, 0, 0), log, Direction.Axis.X);
        TreeGenerationUtil.setLog(level, pos.offset(2, 0, 0), log, Direction.Axis.X);
        TreeGenerationUtil.setLog(level, pos.offset(0, 0, -2), log, Direction.Axis.Z);
        TreeGenerationUtil.setLog(level, pos.offset(0, 0, 2), log, Direction.Axis.Z);
        TreeGenerationUtil.setLog(level, pos.offset(-2, -1, 0), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(2, -1, 0), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(0, -1, -2), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(0, -1, 2), log, Direction.Axis.Y);

        int top = height - 4;
        TreeGenerationUtil.setLog(level, pos.offset(-1, top, -1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(1, top, 1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(-1, top, 1), log, Direction.Axis.Y);
        TreeGenerationUtil.setLog(level, pos.offset(1, top, -1), log, Direction.Axis.Y);
        if (random.nextInt(3) == 0) {
            TreeGenerationUtil.setLog(level, pos.offset(-1, height - 5, -1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) == 0) {
            TreeGenerationUtil.setLog(level, pos.offset(1, height - 5, 1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) == 0) {
            TreeGenerationUtil.setLog(level, pos.offset(-1, height - 5, 1), log, Direction.Axis.Y);
        }
        if (random.nextInt(3) == 0) {
            TreeGenerationUtil.setLog(level, pos.offset(1, height - 5, -1), log, Direction.Axis.Y);
        }
        TreeGenerationUtil.setLog(level, pos.offset(-2, top, 0), log, Direction.Axis.X);
        TreeGenerationUtil.setLog(level, pos.offset(2, top, 0), log, Direction.Axis.X);
        TreeGenerationUtil.setLog(level, pos.offset(0, top, -2), log, Direction.Axis.Z);
        TreeGenerationUtil.setLog(level, pos.offset(0, top, 2), log, Direction.Axis.Z);
    }
}
