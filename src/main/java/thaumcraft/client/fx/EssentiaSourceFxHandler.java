package thaumcraft.client.fx;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;

public final class EssentiaSourceFxHandler {
    private static final Map<String, SourceFx> SOURCE_FX = new HashMap<>();
    private static int tickCount;

    private EssentiaSourceFxHandler() {
    }

    public static void add(BlockPos target, BlockPos source, int color) {
        String key = target.getX() + ":" + target.getY() + ":" + target.getZ() + ":"
                + source.getX() + ":" + source.getY() + ":" + source.getZ() + ":" + color;
        SourceFx current = SOURCE_FX.get(key);
        if (current != null) {
            SOURCE_FX.put(key, new SourceFx(current.target(), current.source(), 15, current.color()));
        } else {
            SOURCE_FX.put(key, new SourceFx(target.immutable(), source.immutable(), 15, color));
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SOURCE_FX.clear();
            return;
        }

        tickCount++;
        for (String key : SOURCE_FX.keySet().toArray(String[]::new)) {
            SourceFx fx = SOURCE_FX.get(key);
            if (fx == null || fx.ticks() <= 0) {
                SOURCE_FX.remove(key);
                continue;
            }

            if (fx.ticks() > 5) {
                spawnTrail(level, fx, tickCount, 1.0F);
            } else {
                float scale = fx.ticks() * fx.ticks() / 25.0F;
                spawnTrail(level, fx, tickCount - (5 - fx.ticks()), scale);
            }
            SOURCE_FX.put(key, new SourceFx(fx.target(), fx.source(), fx.ticks() - 1, fx.color()));
        }
    }

    private static void spawnTrail(ClientLevel level, SourceFx fx, int count, float scale) {
        Vec3 source = Vec3.atCenterOf(fx.source());
        double targetY = fx.target().getY() + 0.5D;
        if (level.getBlockEntity(fx.target()) instanceof RunicMatrixBlockEntity) {
            targetY -= 1.0D;
        }
        Vec3 target = new Vec3(fx.target().getX() + 0.5D, targetY, fx.target().getZ() + 0.5D);
        Minecraft.getInstance().particleEngine.add(new EssentiaTrailParticle(level, source.x, source.y, source.z,
                target.x, target.y, target.z, count, fx.color(), scale));
    }

    private record SourceFx(BlockPos target, BlockPos source, int ticks, int color) {
    }
}
