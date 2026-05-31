package thaumcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.services.ServerServices;

public final class ServerEssentiaHooks {
    private ServerEssentiaHooks() {
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return drainEssentia(level, targetPos, aspect, null, range);
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        if (level.isClientSide) {
            return false;
        }
        return ServerServices.get().drainEssentia(level, targetPos, aspect, direction, range);
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return findEssentia(level, targetPos, aspect, null, range);
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        if (level.isClientSide) {
            return false;
        }
        return ServerServices.get().findEssentia(level, targetPos, aspect, direction, range);
    }

    public static void refreshSources(Level level, BlockPos targetPos) {
        if (level.isClientSide) {
            return;
        }
        ServerServices.get().refreshEssentiaSources(level, targetPos);
    }
}
