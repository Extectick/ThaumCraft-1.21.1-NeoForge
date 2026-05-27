package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;

public class BrainInAJarBlockEntity extends BlockEntity {
    private float targetRotation;
    private float rotation;
    private float previousRotation;
    private long nextSighTick = 30L;

    public BrainInAJarBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.BRAIN_IN_A_JAR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BrainInAJarBlockEntity brain) {
        if (!level.isClientSide) {
            return;
        }

        brain.previousRotation = brain.rotation;
        if (brain.findNearbyOrb(level, pos) == null) {
            Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 6.0D,
                    false);
            if (player != null) {
                double dx = player.getX() - (pos.getX() + 0.5D);
                double dz = player.getZ() - (pos.getZ() + 0.5D);
                brain.targetRotation = (float) Math.atan2(dz, dx);
                brain.playAmbientBrainSound(level, pos);
            } else {
                brain.targetRotation += 0.01F;
            }
        }

        brain.rotation = wrapRadians(brain.rotation);
        brain.targetRotation = wrapRadians(brain.targetRotation);
        float delta = wrapRadians(brain.targetRotation - brain.rotation);
        brain.rotation += delta * 0.04F;
    }

    public float getRenderedRotation(float partialTick) {
        float delta = wrapRadians(this.rotation - this.previousRotation);
        return this.previousRotation + delta * partialTick;
    }

    private ExperienceOrb findNearbyOrb(Level level, BlockPos pos) {
        AABB searchBox = new AABB(pos).inflate(6.0D);
        ExperienceOrb orb = null;
        double closestDistance = Double.MAX_VALUE;
        for (ExperienceOrb candidate : level.getEntitiesOfClass(ExperienceOrb.class, searchBox)) {
            double distance = candidate.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (distance < closestDistance) {
                closestDistance = distance;
                orb = candidate;
            }
        }
        return orb;
    }

    private void playAmbientBrainSound(Level level, BlockPos pos) {
        if (level.getGameTime() < this.nextSighTick) {
            return;
        }

        level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                TCSoundEvents.BRAIN.get(), SoundSource.BLOCKS, 0.15F, 0.8F + level.random.nextFloat() * 0.4F,
                false);
        this.nextSighTick = level.getGameTime() + 100L + level.random.nextInt(500);
    }

    private static float wrapRadians(float angle) {
        while (angle >= Math.PI) {
            angle -= Math.PI * 2.0F;
        }
        while (angle < -Math.PI) {
            angle += Math.PI * 2.0F;
        }
        return angle;
    }
}
