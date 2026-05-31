package thaumcraft.common.lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.util.ServerEssentiaHooks;

public final class EssentiaHandler {
    private EssentiaHandler() {
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return ServerEssentiaHooks.drainEssentia(level, targetPos, aspect, range);
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return ServerEssentiaHooks.drainEssentia(level, targetPos, aspect, direction, range);
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return ServerEssentiaHooks.findEssentia(level, targetPos, aspect, range);
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return ServerEssentiaHooks.findEssentia(level, targetPos, aspect, direction, range);
    }

    public static void refreshSources(Level level, BlockPos targetPos) {
        ServerEssentiaHooks.refreshSources(level, targetPos);
    }
}
