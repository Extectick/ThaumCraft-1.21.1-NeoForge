package thaumcraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import thaumcraft.client.fx.PedestalSparkleParticle;
import thaumcraft.common.blocks.ThaumcraftOreBlock;
import thaumcraft.common.blocks.ThaumcraftOreBlock.OreType;

public final class InfusedOreClientExtensions implements IClientBlockExtensions {
    public static final InfusedOreClientExtensions INSTANCE = new InfusedOreClientExtensions();

    private InfusedOreClientExtensions() {
    }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (!(level instanceof ClientLevel clientLevel)
                || !(state.getBlock() instanceof ThaumcraftOreBlock ore)
                || !hasOldHitEffect(ore.oreType())) {
            return false;
        }

        float[] color = oldSparkleColor(clientLevel, ore.oreType());
        BlockPos pos = target instanceof BlockHitResult blockHit ? blockHit.getBlockPos()
                : BlockPos.containing(target.getLocation());
        for (int i = 0; i < 3; i++) {
            manager.add(new PedestalSparkleParticle(clientLevel,
                    pos.getX() + clientLevel.random.nextFloat(),
                    pos.getY() + clientLevel.random.nextFloat(),
                    pos.getZ() + clientLevel.random.nextFloat(),
                    0.0D, -0.004D, 0.0D,
                    color[0], color[1], color[2],
                    9 + clientLevel.random.nextInt(7), 0, 1.75F));
        }
        return false;
    }

    private static boolean hasOldHitEffect(OreType type) {
        return type == OreType.AIR || type == OreType.FIRE || type == OreType.WATER
                || type == OreType.EARTH || type == OreType.ORDER;
    }

    private static float[] oldSparkleColor(ClientLevel level, OreType type) {
        return switch (type) {
            case AIR -> new float[] {
                    0.5F + level.random.nextFloat() * 0.3F,
                    0.5F + level.random.nextFloat() * 0.3F,
                    0.2F
            };
            case FIRE -> new float[] {
                    0.7F + level.random.nextFloat() * 0.3F,
                    0.2F,
                    0.2F
            };
            case WATER -> new float[] {
                    0.2F,
                    0.2F,
                    0.7F + level.random.nextFloat() * 0.3F
            };
            case EARTH -> new float[] {
                    0.2F,
                    0.7F + level.random.nextFloat() * 0.3F,
                    0.2F
            };
            case ORDER -> new float[] {
                    0.8F + level.random.nextFloat() * 0.2F,
                    0.8F + level.random.nextFloat() * 0.2F,
                    0.8F + level.random.nextFloat() * 0.2F
            };
            default -> new float[] {1.0F, 1.0F, 1.0F};
        };
    }
}
