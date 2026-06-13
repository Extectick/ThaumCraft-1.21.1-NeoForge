package thaumcraft.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.nodes.NodeType;

public final class SinisterLodestoneClientTracker {
    private static final long TIMEOUT_MS = 10_000L;
    private static final double RANGE = 256.0D;
    private static final double FOV_DOT = 0.66D;
    private static final Map<NodeKey, Long> DARK_NODES = new HashMap<>();

    private SinisterLodestoneClientTracker() {
    }

    public static void track(Level level, BlockPos pos, NodeType type) {
        NodeKey key = new NodeKey(level.dimension().location().toString(), pos.immutable());
        if (type == NodeType.DARK) {
            DARK_NODES.put(key, System.currentTimeMillis());
        } else {
            DARK_NODES.remove(key);
        }
    }

    public static boolean hasVisibleDarkNode(Entity entity) {
        long now = System.currentTimeMillis();
        Vec3 eye = entity.getEyePosition();
        Vec3 look = entity.getLookAngle().normalize();
        String dimension = entity.level().dimension().location().toString();

        Iterator<Map.Entry<NodeKey, Long>> iterator = DARK_NODES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<NodeKey, Long> entry = iterator.next();
            if (entry.getValue() < now - TIMEOUT_MS) {
                iterator.remove();
                continue;
            }
            NodeKey key = entry.getKey();
            if (!key.dimension.equals(dimension)) {
                continue;
            }

            Vec3 delta = Vec3.atCenterOf(key.pos).subtract(eye);
            if (delta.lengthSqr() <= RANGE * RANGE && look.dot(delta.normalize()) > FOV_DOT) {
                return true;
            }
        }
        return false;
    }

    private record NodeKey(String dimension, BlockPos pos) {
    }
}
