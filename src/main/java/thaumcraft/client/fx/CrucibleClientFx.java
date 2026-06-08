package thaumcraft.client.fx;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.registry.TCSoundEvents;

public final class CrucibleClientFx {
    private CrucibleClientFx() {
    }

    public static void tick(CrucibleBlockEntity crucible) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || crucible.getWaterAmount() <= 0) {
            return;
        }

        BlockPos pos = crucible.getBlockPos();
        if (crucible.getHeat() > 150) {
            spawnFroth(level, pos.getX() + 0.2F + level.random.nextFloat() * 0.6F,
                    pos.getY() + crucible.getFluidHeight(),
                    pos.getZ() + 0.2F + level.random.nextFloat() * 0.6F);
            if (crucible.tagAmount() > CrucibleBlockEntity.MAX_TAGS) {
                for (int i = 0; i < 2; i++) {
                    spawnFrothDown(level, pos.getX(), pos.getY() + 1.0D, pos.getZ() + level.random.nextFloat());
                    spawnFrothDown(level, pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + level.random.nextFloat());
                    spawnFrothDown(level, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0D, pos.getZ());
                    spawnFrothDown(level, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0D, pos.getZ() + 1.0D);
                }
            }
        }

        if (level.random.nextInt(6) == 0 && !crucible.getAspects().isEmpty()) {
            List<Aspect> aspects = crucible.getAspects().getAspects();
            Aspect aspect = aspects.get(level.random.nextInt(aspects.size()));
            Color color = new Color(aspect.getColor());
            int x = 5 + level.random.nextInt(22);
            int y = 5 + level.random.nextInt(22);
            spawnBubble(level,
                    pos.getX() + x / 32.0F + 0.015625F,
                    pos.getY() + 0.05F + crucible.getFluidHeight(),
                    pos.getZ() + y / 32.0F + 0.015625F,
                    color.getRed() / 255.0F,
                    color.getGreen() / 255.0F,
                    color.getBlue() / 255.0F,
                    1,
                    0.002D);
        }
    }

    public static void blockEvent(CrucibleBlockEntity crucible, int eventId, int eventParam) {
        if (eventId == 1) {
            PedestalSparkleFxHandler.add(crucible.getBlockPos(), 12);
            return;
        }
        if (eventId != 2 || Minecraft.getInstance().level == null) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        BlockPos pos = crucible.getBlockPos();
        level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                TCSoundEvents.SPILL.get(), SoundSource.MASTER, 0.2F, 1.0F, false);

        int count = particleCount(level, 10);
        for (int i = 0; i < count; i++) {
            float red = 1.0F;
            float green = 1.0F;
            float blue = 1.0F;
            List<Aspect> aspects = crucible.getAspects().getAspects();
            if (!aspects.isEmpty()) {
                Color color = new Color(aspects.get(level.random.nextInt(aspects.size())).getColor());
                red = color.getRed() / 255.0F;
                green = color.getGreen() / 255.0F;
                blue = color.getBlue() / 255.0F;
            }
            CrucibleBubbleParticle particle = CrucibleBubbleParticle.bubble(level,
                    pos.getX() + 0.2F + level.random.nextFloat() * 0.6F,
                    pos.getY() + 0.1F + crucible.getFluidHeight(),
                    pos.getZ() + 0.2F + level.random.nextFloat() * 0.6F,
                    3, red, green, blue);
            particle.setBubbleSpeed(0.003D * eventParam);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    private static void spawnBubble(ClientLevel level, double x, double y, double z, float red, float green,
            float blue, int age, double speed) {
        CrucibleBubbleParticle particle = CrucibleBubbleParticle.bubble(level, x, y, z, age, red, green, blue);
        particle.setBubbleSpeed(speed);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    private static void spawnFroth(ClientLevel level, double x, double y, double z) {
        Minecraft.getInstance().particleEngine.add(CrucibleBubbleParticle.froth(level, x, y, z));
    }

    private static void spawnFrothDown(ClientLevel level, double x, double y, double z) {
        Minecraft.getInstance().particleEngine.add(CrucibleBubbleParticle.frothDown(level, x, y, z));
    }

    private static int particleCount(ClientLevel level, int base) {
        return switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> 0;
            case DECREASED -> base;
            case ALL -> base * 2;
        };
    }
}
