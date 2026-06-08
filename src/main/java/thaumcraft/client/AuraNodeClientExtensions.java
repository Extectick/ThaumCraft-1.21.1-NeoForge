package thaumcraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import thaumcraft.client.fx.AuraNodeBurstParticle;
import thaumcraft.client.fx.PedestalSparkleParticle;
import thaumcraft.common.registry.TCSoundEvents;

public final class AuraNodeClientExtensions implements IClientBlockExtensions {
    public static final AuraNodeClientExtensions INSTANCE = new AuraNodeClientExtensions();

    private AuraNodeClientExtensions() {
    }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (level instanceof ClientLevel clientLevel && clientLevel.random.nextBoolean()) {
            BlockPos pos = BlockPos.containing(target.getLocation());
            for (int i = 0; i < 3; i++) {
                manager.add(new PedestalSparkleParticle(clientLevel,
                        pos.getX() + clientLevel.random.nextFloat(),
                        pos.getY() + clientLevel.random.nextFloat(),
                        pos.getZ() + clientLevel.random.nextFloat(),
                        0.0D, -0.02D, 0.0D, 1.0F, 1.0F, 0.2F,
                        8 + clientLevel.random.nextInt(4), 0, 1.75F));
            }
        }
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (level instanceof ClientLevel clientLevel) {
            AuraNodeBurstParticle.spawn(clientLevel, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        }
        return true;
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos) {
        level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                TCSoundEvents.CRAFTFAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
        return true;
    }
}
