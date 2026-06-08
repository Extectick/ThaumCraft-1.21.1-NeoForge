package thaumcraft.common.research;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ThaumometerRaycast {
    private ThaumometerRaycast() {
    }

    public static HitResult pick(Level level, Entity viewer, double range) {
        Vec3 start = viewer.getEyePosition();
        Vec3 end = start.add(viewer.getViewVector(1.0F).scale(range));
        return level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, viewer));
    }

    public static Optional<EntityHitResult> pickEntity(Level level, Entity viewer, double range,
            Predicate<Entity> predicate) {
        Vec3 start = viewer.getEyePosition();
        Vec3 end = start.add(viewer.getViewVector(1.0F).scale(range));
        HitResult blockHit = pick(level, viewer, range);
        double blockDistance = blockHit.getType() == HitResult.Type.MISS
                ? range * range
                : blockHit.getLocation().distanceToSqr(start);
        AABB searchBox = new AABB(start, end).inflate(1.0D);

        return level.getEntities(viewer, searchBox, predicate).stream()
                .map(entity -> intersect(entity, start, end))
                .flatMap(Optional::stream)
                .filter(hit -> hit.getLocation().distanceToSqr(start) <= blockDistance)
                .min(Comparator.comparingDouble(hit -> hit.getLocation().distanceToSqr(start)));
    }

    private static Optional<EntityHitResult> intersect(Entity entity, Vec3 start, Vec3 end) {
        double padding = Math.max(0.8D, entity.getPickRadius());
        AABB bounds = entity.getBoundingBox().inflate(padding);
        if (bounds.contains(start)) {
            return Optional.of(new EntityHitResult(entity, start));
        }
        return bounds.clip(start, end).map(location -> new EntityHitResult(entity, location));
    }
}
