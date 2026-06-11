package thaumcraft.common.entities.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
// import thaumcraft.client.fx.particles.FXSparkle;
import thaumcraft.common.registry.TCEntityTypes;

public class FollowingItemEntity extends ItemEntity {
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(FollowingItemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_TYPE = SynchedEntityData.defineId(FollowingItemEntity.class, EntityDataSerializers.INT);

    private int ticksInState = 20;

    public FollowingItemEntity(EntityType<FollowingItemEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setPickUpDelay(0);
    }

    public static FollowingItemEntity create(EntityType<FollowingItemEntity> type, Level level) {
        return new FollowingItemEntity(type, level);
    }

    public FollowingItemEntity(Level level, double x, double y, double z, ItemStack stack, Player target) {
        this(level, x, y, z, stack, target, 2);
    }

    public FollowingItemEntity(Level level, double x, double y, double z, ItemStack stack, Player target, int particleColorType) {
        this(TCEntityTypes.FOLLOWING_ITEM.get(), level);
        this.setPos(x, y, z);
        this.setItem(stack);
        this.setFollowingTarget(target);
        this.entityData.set(DATA_COLOR_TYPE, particleColorType);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TARGET_ID, -1);
        builder.define(DATA_COLOR_TYPE, 2); // default blue
    }

    @Override
    public void tick() {
        Entity target = getFollowingTarget();

        if (target != null) {
            double tx = target.getX();
            double ty = target.getBoundingBox().minY + target.getBbHeight() / 2.0;
            double tz = target.getZ();

            double dx = tx - getX();
            double dy = ty - getY();
            double dz = tz - getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (ticksInState > 1) {
                ticksInState--;
            }

            if (dist > 0.5) {
                double speed = dist * ticksInState;
                this.setDeltaMovement(dx / speed, dy / speed, dz / speed);
            } else {
                this.setDeltaMovement(getDeltaMovement().scale(0.1));
                this.noPhysics = false;
                this.entityData.set(DATA_TARGET_ID, -1);
            }
        } else {
            this.setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));
        }

        // Client-side sparkle particles
        if (level().isClientSide && getFollowingTarget() != null) {
            int colorType = this.entityData.get(DATA_COLOR_TYPE);
            double px = xo + (random.nextFloat() - random.nextFloat()) * 0.125;
            double py = yo + getBbHeight() / 2.0 + (random.nextFloat() - random.nextFloat()) * 0.125;
            double pz = zo + (random.nextFloat() - random.nextFloat()) * 0.125;
            // SparkleParticle sparkle = SparkleParticle.createDirect((net.minecraft.client.multiplayer.ClientLevel) level(), px, py, pz, 1.5F, colorType, 6);
            // sparkle.setNoClip(true);
            // net.minecraft.client.Minecraft.getInstance().particleEngine.add(sparkle);
        }

        super.tick();
    }

    public void setFollowingTarget(Player player) {
        this.entityData.set(DATA_TARGET_ID, player != null ? player.getId() : -1);
    }

    public Entity getFollowingTarget() {
        int id = this.entityData.get(DATA_TARGET_ID);
        return id > 0 ? level().getEntity(id) : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TargetId", this.entityData.get(DATA_TARGET_ID));
        tag.putInt("ColorType", this.entityData.get(DATA_COLOR_TYPE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("TargetId")) {
            this.entityData.set(DATA_TARGET_ID, tag.getInt("TargetId"));
        }
        if (tag.contains("ColorType")) {
            this.entityData.set(DATA_COLOR_TYPE, tag.getInt("ColorType"));
        }
    }
}


