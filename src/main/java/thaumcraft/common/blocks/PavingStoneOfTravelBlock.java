package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

public class PavingStoneOfTravelBlock extends Block {

    private static final DustParticleOptions SPEED_SPARKLE = new DustParticleOptions(new Vector3f(0.6F, 0.9F, 0.6F), 1.0F);

    public static final MapCodec<PavingStoneOfTravelBlock> CODEC = simpleCodec(PavingStoneOfTravelBlock::new);

    public PavingStoneOfTravelBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (!level.isClientSide) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                living.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, false, false));
            } else {

                RandomSource random = level.random;
                for (int i = 0; i < 3; i++) {
                    double px = pos.getX() + 0.2 + random.nextFloat() * 0.6;
                    double py = pos.getY() + 1.0;
                    double pz = pos.getZ() + 0.2 + random.nextFloat() * 0.6;
                    level.addParticle(SPEED_SPARKLE, px, py, pz, 0.0, -0.05, 0.0);
                }
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}


