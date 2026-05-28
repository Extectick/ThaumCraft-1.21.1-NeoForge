package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundSource;
import thaumcraft.common.registry.TCSoundEvents;

public final class BlockZapFxHandler {
    private BlockZapFxHandler() {
    }

    public static void add(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        LightningBoltParticle.spawn(level, fromX, fromY, fromZ, toX, toY, toZ);
        level.playLocalSound(fromX, fromY, fromZ, TCSoundEvents.ZAP.get(), SoundSource.BLOCKS, 0.1F,
                1.0F + level.random.nextFloat() * 0.2F, false);
    }
}
