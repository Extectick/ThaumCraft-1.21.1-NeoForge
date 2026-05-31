package thaumcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.services.ServerServices;

public final class ServerInfusionHooks {
    private ServerInfusionHooks() {
    }

    public static boolean refreshRunicMatrixSurroundings(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        return ServerServices.get().refreshRunicMatrixSurroundings(matrix, level, pos);
    }

    public static void tickRunicMatrix(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        ServerServices.get().tickRunicMatrix(matrix, level, pos);
    }
}
