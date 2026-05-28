package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;
import thaumcraft.common.registry.TCBlocks;

public record InfusionAltarScan(boolean valid, BlockPos matrixPos, BlockPos centerPedestal, List<BlockPos> pedestals,
        int symmetry) {
    public static InfusionAltarScan scan(Level level, BlockPos matrixPos) {
        BlockPos centerPedestal = matrixPos.below(2);
        List<BlockPos> pedestals = findPedestals(level, matrixPos);
        List<BlockPos> stabilizers = findStabilizers(level, matrixPos);
        int symmetry = calculateSymmetry(level, matrixPos, pedestals, stabilizers);
        return new InfusionAltarScan(isValidLocation(level, matrixPos, centerPedestal), matrixPos, centerPedestal,
                List.copyOf(pedestals), symmetry);
    }

    public static boolean isValidLocation(Level level, BlockPos matrixPos) {
        return isValidLocation(level, matrixPos, matrixPos.below(2));
    }

    private static boolean isValidLocation(Level level, BlockPos matrixPos, BlockPos centerPedestal) {
        if (!(level.getBlockEntity(centerPedestal) instanceof ArcanePedestalBlockEntity)) {
            return false;
        }

        return isInfusionPillar(level, matrixPos.offset(1, -2, 1))
                && isInfusionPillar(level, matrixPos.offset(1, -2, -1))
                && isInfusionPillar(level, matrixPos.offset(-1, -2, -1))
                && isInfusionPillar(level, matrixPos.offset(-1, -2, 1));
    }

    private static List<BlockPos> findPedestals(Level level, BlockPos matrixPos) {
        List<BlockPos> pedestals = new ArrayList<>();
        for (int xx = -12; xx <= 12; xx++) {
            for (int zz = -12; zz <= 12; zz++) {
                boolean foundColumnPedestal = false;
                for (int yy = -5; yy <= 10; yy++) {
                    if (xx == 0 && zz == 0) {
                        continue;
                    }

                    BlockPos pos = matrixPos.offset(xx, -yy, zz);
                    if (!foundColumnPedestal && yy > 0 && Math.abs(xx) <= 8 && Math.abs(zz) <= 8
                            && level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity) {
                        pedestals.add(pos);
                        foundColumnPedestal = true;
                    }
                }
            }
        }
        return pedestals;
    }

    private static List<BlockPos> findStabilizers(Level level, BlockPos matrixPos) {
        List<BlockPos> stabilizers = new ArrayList<>();
        for (int xx = -12; xx <= 12; xx++) {
            for (int zz = -12; zz <= 12; zz++) {
                for (int yy = -5; yy <= 10; yy++) {
                    if (xx == 0 && zz == 0) {
                        continue;
                    }
                    BlockPos pos = matrixPos.offset(xx, -yy, zz);
                    if (isStabilizer(level.getBlockState(pos))) {
                        stabilizers.add(pos);
                    }
                }
            }
        }
        return stabilizers;
    }

    private static int calculateSymmetry(Level level, BlockPos matrixPos, List<BlockPos> pedestals,
            List<BlockPos> stabilizers) {
        int symmetry = 0;
        for (BlockPos pedestalPos : pedestals) {
            boolean hasItem = hasPedestalItem(level, pedestalPos);
            if (level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity) {
                symmetry += 2;
                if (hasItem) {
                    symmetry++;
                }
            }

            int dx = matrixPos.getX() - pedestalPos.getX();
            int dz = matrixPos.getZ() - pedestalPos.getZ();
            BlockPos mirrorPos = new BlockPos(matrixPos.getX() + dx, pedestalPos.getY(), matrixPos.getZ() + dz);
            if (level.getBlockEntity(mirrorPos) instanceof ArcanePedestalBlockEntity) {
                symmetry -= 2;
                if (hasItem && hasPedestalItem(level, mirrorPos)) {
                    symmetry--;
                }
            }
        }

        float stabilizerSymmetry = 0.0F;
        for (BlockPos stabilizerPos : stabilizers) {
            int dx = matrixPos.getX() - stabilizerPos.getX();
            int dz = matrixPos.getZ() - stabilizerPos.getZ();
            stabilizerSymmetry += 0.1F;

            BlockPos mirrorPos = new BlockPos(matrixPos.getX() + dx, stabilizerPos.getY(), matrixPos.getZ() + dz);
            if (isStabilizer(level.getBlockState(mirrorPos))) {
                stabilizerSymmetry -= 0.2F;
            }
        }

        return (int)(symmetry + stabilizerSymmetry);
    }

    private static boolean isInfusionPillar(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(TCBlocks.INFUSION_PILLAR.get());
    }

    private static boolean hasPedestalItem(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal
                && !pedestal.getStoredItem().isEmpty();
    }

    private static boolean isStabilizer(BlockState state) {
        return state.getBlock() instanceof CandleBlock || state.getBlock() instanceof AbstractSkullBlock;
    }
}
