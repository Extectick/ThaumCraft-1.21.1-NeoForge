package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class InfusionMatrixClientFx {
    private InfusionMatrixClientFx() {
    }

    public static void blockRunes(Level level, BlockPos pos) {
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }
        float red = 0.5F + clientLevel.random.nextFloat() * 0.2F;
        float blue = 0.7F + clientLevel.random.nextFloat() * 0.3F;
        Minecraft.getInstance().particleEngine.add(new BlockRunesParticle(clientLevel,
                pos.getX() + 0.5D, pos.getY() - 1.5D, pos.getZ() + 0.5D,
                red, 0.1F, blue, 25, -0.03F));
    }

    public static void instabilityBolt(Level level, BlockPos pos, int instability) {
        if (!(level instanceof ClientLevel clientLevel) || instability <= 0
                || clientLevel.random.nextInt(200) > instability) {
            return;
        }
        double fromX = pos.getX() + 0.5D;
        double fromY = pos.getY() + 0.5D;
        double fromZ = pos.getZ() + 0.5D;
        double toX = fromX + (clientLevel.random.nextFloat() - clientLevel.random.nextFloat()) * 2.0F;
        double toY = fromY + (clientLevel.random.nextFloat() - clientLevel.random.nextFloat()) * 2.0F;
        double toZ = fromZ + (clientLevel.random.nextFloat() - clientLevel.random.nextFloat()) * 2.0F;
        LightningBoltParticle.spawn(clientLevel, fromX, fromY, fromZ, toX, toY, toZ);
    }
}
