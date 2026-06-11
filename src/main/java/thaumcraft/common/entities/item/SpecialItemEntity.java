package thaumcraft.common.entities.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// EntitySpecialItem
public class SpecialItemEntity extends ItemEntity {

    public SpecialItemEntity(EntityType<SpecialItemEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setPickUpDelay(0);
    }

    public static SpecialItemEntity create(EntityType<SpecialItemEntity> type, Level level) {
        return new SpecialItemEntity(type, level);
    }

    public SpecialItemEntity(EntityType<SpecialItemEntity> type, Level level, double x, double y, double z, ItemStack stack) {
        super(type, level);
        this.setPos(x, y, z);
        this.setItem(stack);
        this.setYRot((float) (Math.random() * 360.0F));
        double motionX = Math.random() * 0.2D - 0.1D;
        double motionY = 0.2D;
        double motionZ = Math.random() * 0.2D - 0.1D;
        this.setDeltaMovement(motionX, motionY, motionZ);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        Vec3 movement = this.getDeltaMovement();
        double my = movement.y;
        if (my > 0.0D) {
            my *= 0.9D;
        }
        this.setDeltaMovement(movement.x, my + 0.04D, movement.z);
        super.tick();
    }
}
