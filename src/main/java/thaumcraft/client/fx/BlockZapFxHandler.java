package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCSoundEvents;

public final class BlockZapFxHandler {
    private BlockZapFxHandler() {
    }

    public static void add(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 start = new Vec3(fromX, fromY, fromZ);
        Vec3 delta = new Vec3(toX, toY, toZ).subtract(start);
        int steps = Math.max(8, (int)(delta.length() * 10.0D));
        for (int i = 0; i <= steps; i++) {
            double t = i / (double)steps;
            Vec3 pos = start.add(delta.scale(t))
                    .add((level.random.nextDouble() - 0.5D) * 0.12D,
                            (level.random.nextDouble() - 0.5D) * 0.12D,
                            (level.random.nextDouble() - 0.5D) * 0.12D);
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }
        level.playLocalSound(fromX, fromY, fromZ, TCSoundEvents.ZAP.get(), SoundSource.BLOCKS, 0.35F,
                0.9F + level.random.nextFloat() * 0.2F, false);
    }
}
