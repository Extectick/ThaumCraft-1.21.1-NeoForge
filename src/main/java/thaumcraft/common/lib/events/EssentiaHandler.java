package thaumcraft.common.lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainer;

public final class EssentiaHandler {
    private EssentiaHandler() {
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        if (level.isClientSide || aspect == null || range <= 0) {
            return false;
        }

        BlockPos min = targetPos.offset(-range, -range, -range);
        BlockPos max = targetPos.offset(range, range, range);
        for (BlockPos sourcePos : BlockPos.betweenClosed(min, max)) {
            if (sourcePos.equals(targetPos)) {
                continue;
            }
            BlockEntity source = level.getBlockEntity(sourcePos);
            if (source instanceof IEssentiaContainer container
                    && container.drainEssentia(aspect, 1, false) == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        if (level.isClientSide || aspect == null || range <= 0) {
            return false;
        }

        BlockPos min = targetPos.offset(-range, -range, -range);
        BlockPos max = targetPos.offset(range, range, range);
        for (BlockPos sourcePos : BlockPos.betweenClosed(min, max)) {
            if (sourcePos.equals(targetPos)) {
                continue;
            }
            BlockEntity source = level.getBlockEntity(sourcePos);
            if (source instanceof IEssentiaContainer container
                    && container.drainEssentia(aspect, 1, true) == 1) {
                return true;
            }
        }
        return false;
    }
}
