package thaumcraft.common.visnet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;

public final class VisNetHandler {
    private VisNetHandler() {
    }

    public static int drainVis(Level level, BlockPos pos, Aspect aspect, int amount) {
        if (level.isClientSide || aspect == null || amount <= 0) {
            return 0;
        }
        int drained = 0;
        for (IVisNetNode node : closestNodesByRoot(level, pos, 8)) {
            int taken = node.consumeVis(aspect, amount);
            if (taken > 0) {
                drained += taken;
                amount -= taken;
            }
            if (amount <= 0) {
                break;
            }
        }
        return drained;
    }

    @Nullable
    public static BlockPos findParent(Level level, IVisNetNode target) {
        BlockPos targetPos = target.getVisNetPos();
        List<IVisNetNode> candidates = nearbyNodes(level, targetPos, target.getRange());
        IVisNetNode closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (IVisNetNode candidate : candidates) {
            BlockPos candidatePos = candidate.getVisNetPos();
            if (candidatePos.equals(targetPos) || !attunementMatches(target, candidate)) {
                continue;
            }
            if (!candidate.isSource() && candidate.getParentPos() == null) {
                continue;
            }
            if (rootSource(level, candidate) == null) {
                continue;
            }
            if (createsLoop(level, targetPos, candidate)) {
                continue;
            }
            if (!canNodeBeSeen(level, targetPos, candidatePos)) {
                continue;
            }
            double distance = parentScore(target, candidate, targetPos, candidatePos);
            if (distance < closestDistance) {
                closest = candidate;
                closestDistance = distance;
            }
        }
        return closest == null ? null : closest.getVisNetPos();
    }

    public static boolean isNodeValid(Level level, @Nullable BlockPos pos) {
        if (pos == null || !level.isLoaded(pos)) {
            return false;
        }
        IVisNetNode node = asNode(level.getBlockEntity(pos));
        return node != null && node.isValidVisNode(level) && rootSource(level, node) != null;
    }

    public static boolean canNodeBeSeen(Level level, BlockPos source, BlockPos target) {
        Vec3 from = Vec3.atCenterOf(source);
        Vec3 to = Vec3.atCenterOf(target);
        BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, CollisionContext.empty()));
        if (hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(target)) {
            return true;
        }
        if (!hit.getBlockPos().equals(source)) {
            return false;
        }

        Vec3 delta = to.subtract(from);
        int steps = Math.max(1, (int) Math.ceil(delta.length() * 4.0D));
        for (int step = 1; step < steps; step++) {
            Vec3 sample = from.add(delta.scale(step / (double) steps));
            BlockPos samplePos = BlockPos.containing(sample);
            if (samplePos.equals(source) || samplePos.equals(target)) {
                continue;
            }
            BlockState state = level.getBlockState(samplePos);
            if (!state.getCollisionShape(level, samplePos, CollisionContext.empty()).isEmpty()
                    && asNode(level.getBlockEntity(samplePos)) == null) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static IVisNetNode asNode(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof IVisNetNode node) {
            return node;
        }
        if (blockEntity instanceof AuraNodeBlockEntity node && node.isEnergized()) {
            return new EnergizedAuraNodeAdapter(node);
        }
        return null;
    }

    private static List<IVisNetNode> nearbyNodes(Level level, BlockPos center, int range) {
        List<IVisNetNode> nodes = new ArrayList<>();
        int rangeSq = range * range;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = center.getX() - range; x <= center.getX() + range; x++) {
            for (int y = center.getY() - range; y <= center.getY() + range; y++) {
                for (int z = center.getZ() - range; z <= center.getZ() + range; z++) {
                    cursor.set(x, y, z);
                    if (center.distSqr(cursor) > rangeSq || !level.isLoaded(cursor)) {
                        continue;
                    }
                    IVisNetNode node = asNode(level.getBlockEntity(cursor.immutable()));
                    if (node != null && node.isValidVisNode(level)) {
                        nodes.add(node);
                    }
                }
            }
        }
        nodes.sort(Comparator.comparingDouble(node -> node.getVisNetPos().distSqr(center)));
        return nodes;
    }

    private static List<IVisNetNode> closestNodesByRoot(Level level, BlockPos center, int range) {
        Map<BlockPos, IVisNetNode> nodesByRoot = new HashMap<>();
        Map<BlockPos, Double> distancesByRoot = new HashMap<>();
        for (IVisNetNode node : nearbyNodes(level, center, range)) {
            if (!node.isSource() && node.getParentPos() == null) {
                continue;
            }
            BlockPos root = rootSource(level, node);
            if (root == null) {
                continue;
            }
            double distance = node.getVisNetPos().distSqr(center);
            Double previous = distancesByRoot.get(root);
            if (previous == null || distance < previous) {
                nodesByRoot.put(root, node);
                distancesByRoot.put(root, distance);
            }
        }
        List<IVisNetNode> nodes = new ArrayList<>(nodesByRoot.values());
        nodes.sort(Comparator.comparingDouble(node -> node.getVisNetPos().distSqr(center)));
        return nodes;
    }

    @Nullable
    private static BlockPos rootSource(Level level, IVisNetNode node) {
        IVisNetNode current = node;
        for (int depth = 0; depth < 512 && current != null; depth++) {
            if (current.isSource()) {
                return current.getVisNetPos();
            }
            BlockPos parentPos = current.getParentPos();
            if (parentPos == null || !level.isLoaded(parentPos)) {
                return null;
            }
            current = asNode(level.getBlockEntity(parentPos));
        }
        return null;
    }

    private static double parentScore(IVisNetNode target, IVisNetNode candidate, BlockPos targetPos,
            BlockPos candidatePos) {
        double distance = targetPos.distSqr(candidatePos);
        return candidate.isSource() ? distance - target.getRange() * 2.0D : distance;
    }

    private static boolean attunementMatches(IVisNetNode target, IVisNetNode candidate) {
        byte targetAttunement = target.getAttunement();
        byte candidateAttunement = candidate.getAttunement();
        return targetAttunement == -1 || candidateAttunement == -1 || targetAttunement == candidateAttunement;
    }

    private static boolean createsLoop(Level level, BlockPos targetPos, IVisNetNode candidate) {
        IVisNetNode current = candidate;
        for (int depth = 0; depth < 512 && current != null; depth++) {
            if (current.getVisNetPos().equals(targetPos)) {
                return true;
            }
            if (current.isSource()) {
                return false;
            }
            BlockPos parentPos = current.getParentPos();
            if (parentPos == null) {
                return false;
            }
            current = asNode(level.getBlockEntity(parentPos));
        }
        return true;
    }

    private record EnergizedAuraNodeAdapter(AuraNodeBlockEntity node) implements IVisNetNode {
        @Override
        public BlockPos getVisNetPos() {
            return this.node.getBlockPos();
        }

        @Override
        public int getRange() {
            return 8;
        }

        @Override
        public boolean isSource() {
            return true;
        }

        @Override
        public byte getAttunement() {
            return -1;
        }

        @Nullable
        @Override
        public BlockPos getParentPos() {
            return null;
        }

        @Override
        public void setParentPos(@Nullable BlockPos parentPos) {
        }

        @Override
        public int consumeVis(Aspect aspect, int amount) {
            return this.node.consumeEnergizedVis(aspect, amount);
        }

        @Override
        public boolean isValidVisNode(Level level) {
            return this.node.isEnergized();
        }
    }
}
