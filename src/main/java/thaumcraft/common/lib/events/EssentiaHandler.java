package thaumcraft.common.lib.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.network.EssentiaSourceFxPayload;

public final class EssentiaHandler {
    private static final long SOURCE_RETRY_DELAY_MS = 5000L;
    private static final Map<SourceKey, List<SourceKey>> SOURCES = new HashMap<>();
    private static final Map<SourceKey, Long> SOURCES_DELAY = new HashMap<>();

    private EssentiaHandler() {
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return drainEssentia(level, targetPos, aspect, null, range);
    }

    public static boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        if (level.isClientSide || aspect == null || range <= 0) {
            return false;
        }

        SourceKey target = SourceKey.of(level, targetPos);
        if (!SOURCES.containsKey(target)) {
            getSources(level, target, direction, range);
            return SOURCES.containsKey(target) && drainEssentia(level, targetPos, aspect, direction, range);
        }

        for (SourceKey sourceKey : SOURCES.get(target)) {
            BlockEntity source = level.getBlockEntity(sourceKey.pos());
            if (!(source instanceof IAspectSource aspectSource)) {
                break;
            }

            if (aspectSource.takeFromContainer(aspect, 1)) {
                sendEssentiaSourceFx(level, sourceKey.pos(), targetPos, aspect);
                return true;
            }
        }

        SOURCES.remove(target);
        SOURCES_DELAY.put(target, System.currentTimeMillis() + SOURCE_RETRY_DELAY_MS);
        return false;
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, int range) {
        return findEssentia(level, targetPos, aspect, null, range);
    }

    public static boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        if (level.isClientSide || aspect == null || range <= 0) {
            return false;
        }

        SourceKey target = SourceKey.of(level, targetPos);
        if (!SOURCES.containsKey(target)) {
            getSources(level, target, direction, range);
            return SOURCES.containsKey(target) && findEssentia(level, targetPos, aspect, direction, range);
        }

        for (SourceKey sourceKey : SOURCES.get(target)) {
            BlockEntity source = level.getBlockEntity(sourceKey.pos());
            if (!(source instanceof IAspectSource aspectSource)) {
                break;
            }

            if (aspectSource.doesContainerContainAmount(aspect, 1)) {
                return true;
            }
        }

        SOURCES.remove(target);
        SOURCES_DELAY.put(target, System.currentTimeMillis() + SOURCE_RETRY_DELAY_MS);
        return false;
    }

    public static void refreshSources(Level level, BlockPos targetPos) {
        SOURCES.remove(SourceKey.of(level, targetPos));
    }

    private static void getSources(Level level, SourceKey target, Direction direction, int range) {
        Long delay = SOURCES_DELAY.get(target);
        if (delay != null) {
            if (delay > System.currentTimeMillis()) {
                return;
            }
            SOURCES_DELAY.remove(target);
        }

        List<SourceKey> foundSources = new ArrayList<>();
        Direction scanDirection = direction == null ? Direction.UP : direction;
        int start = direction == null ? -range : 0;
        BlockPos targetPos = target.pos();

        for (int aa = -range; aa <= range; aa++) {
            for (int bb = -range; bb <= range; bb++) {
                for (int cc = start; cc < range; cc++) {
                    if (aa == 0 && bb == 0 && cc == 0) {
                        continue;
                    }

                    BlockPos sourcePos = translateScanPosition(targetPos, scanDirection, aa, bb, cc);
                    BlockEntity source = level.getBlockEntity(sourcePos);
                    if (source instanceof IAspectSource) {
                        foundSources.add(SourceKey.of(level, sourcePos));
                    }
                }
            }
        }

        if (foundSources.isEmpty()) {
            SOURCES_DELAY.put(target, System.currentTimeMillis() + SOURCE_RETRY_DELAY_MS);
        } else {
            SOURCES.put(target, foundSources);
        }
    }

    private static BlockPos translateScanPosition(BlockPos origin, Direction direction, int aa, int bb, int cc) {
        return switch (direction.getAxis()) {
            case Y -> origin.offset(aa, cc * direction.getStepY(), bb);
            case Z -> origin.offset(aa, bb, cc * direction.getStepZ());
            case X -> origin.offset(cc * direction.getStepX(), aa, bb);
        };
    }

    private static void sendEssentiaSourceFx(Level level, BlockPos sourcePos, BlockPos targetPos, Aspect aspect) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                    32.0D, new EssentiaSourceFxPayload(targetPos, sourcePos, aspect.getColor()));
        }
    }

    private record SourceKey(ResourceKey<Level> dimension, BlockPos pos) {
        private static SourceKey of(Level level, BlockPos pos) {
            return new SourceKey(level.dimension(), pos.immutable());
        }
    }
}
