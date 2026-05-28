package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public final class PedestalSparkleFxHandler {
    private static final int EJECT_COLOR = 0xC000C0;

    private PedestalSparkleFxHandler() {
    }

    public static void add(BlockPos pos, int eventId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        int outerCount = particleCount(level, eventId == 12 ? 10 : 5);
        int color = eventId == 12 ? -9999 : EJECT_COLOR;
        for (int i = 0; i < outerCount; i++) {
            blockSparkle(level, pos, color, 2);
        }
    }

    private static void blockSparkle(ClientLevel level, BlockPos pos, int color, int count) {
        float baseRed = ((color >> 16) & 0xFF) / 255.0F;
        float baseGreen = ((color >> 8) & 0xFF) / 255.0F;
        float baseBlue = (color & 0xFF) / 255.0F;
        int particleCount = particleCount(level, count);
        for (int i = 0; i < particleCount; i++) {
            float red = baseRed;
            float green = baseGreen;
            float blue = baseBlue;
            if (color == -9999) {
                red = 0.33F + level.random.nextFloat() * 0.67F;
                green = 0.33F + level.random.nextFloat() * 0.67F;
                blue = 0.33F + level.random.nextFloat() * 0.67F;
            }
            red = red - 0.2F + level.random.nextFloat() * 0.4F;
            green = green - 0.2F + level.random.nextFloat() * 0.4F;
            blue = blue - 0.2F + level.random.nextFloat() * 0.4F;
            Minecraft.getInstance().particleEngine.add(new PedestalSparkleParticle(level,
                    pos.getX() - 0.1D + level.random.nextFloat() * 1.2D,
                    pos.getY() + 0.9D + level.random.nextFloat() * 1.2D,
                    pos.getZ() - 0.1D + level.random.nextFloat() * 1.2D,
                    0.0D,
                    level.random.nextFloat() * 0.02D,
                    0.0D,
                    red, green, blue,
                    5 + level.random.nextInt(8),
                    level.random.nextInt(10),
                    0.7F + level.random.nextFloat() * 0.4F));
        }
    }

    private static int particleCount(ClientLevel level, int base) {
        return switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> 0;
            case DECREASED -> base;
            case ALL -> base * 2;
        };
    }
}
