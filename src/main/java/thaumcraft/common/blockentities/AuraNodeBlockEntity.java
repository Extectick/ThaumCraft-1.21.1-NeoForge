package thaumcraft.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.research.EntityAspectRegistry;
import thaumcraft.common.services.ServerServices;

public class AuraNodeBlockEntity extends BlockEntity implements INode, IWandable {
    private AspectList aspects = defaultAspects();
    private AspectList aspectsBase = this.aspects.copy();
    private NodeType nodeType = NodeType.NORMAL;
    @Nullable
    private NodeModifier nodeModifier;
    private UUID nodeId = UUID.randomUUID();
    private int tickCount;
    private int rechargeWait;
    private int stabilizerLock;
    private long lastActive;
    private boolean catchUp;
    private int drainPlayerId = -1;
    private int drainColor = 0xFFFFFF;

    public AuraNodeBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.AURA_NODE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity node) {
        node.tickCount++;
        if (node.tickCount <= 1 || node.tickCount % 50 == 0) {
            node.updateStabilizerLock(level, pos, state);
        }
        if (level.isClientSide) {
            node.handleHungryBlockFx(level, pos);
            return;
        }

        boolean changed = node.handleHungryPull(level, pos);
        changed |= node.handleRecharge(level);
        changed |= node.handleUnstable(level);
        changed |= node.handleNodeDrain(level, pos);
        changed |= node.handleHungryDestruction(level, pos);
        changed |= node.handleDegradation(level);
        if (changed && level.getBlockEntity(pos) == node) {
            node.markChangedAndSync();
        }
    }

    private boolean handleRecharge(Level level) {
        int regeneration = this.regenerationInterval();
        boolean changed = false;
        if (this.catchUp) {
            this.catchUp = false;
            int interval = regeneration * 75;
            int amount = interval > 0 ? (int) ((System.currentTimeMillis() - this.lastActive) / interval) : 0;
            for (int i = 0; i < Math.min(amount, this.aspectsBase.visSize()); i++) {
                List<Aspect> missing = this.missingAspects();
                if (missing.isEmpty()) {
                    break;
                }
                this.aspects.add(missing.get(level.random.nextInt(missing.size())), 1);
                changed = true;
            }
        }
        if (this.rechargeWait > 0) {
            this.rechargeWait--;
        }
        if (regeneration <= 0 || this.rechargeWait > 0 || this.tickCount % regeneration != 0) {
            return changed;
        }

        this.lastActive = System.currentTimeMillis();
        List<Aspect> missing = this.missingAspects();
        if (missing.isEmpty()) {
            return changed;
        }

        this.aspects.add(missing.get(level.random.nextInt(missing.size())), 1);
        return true;
    }

    private List<Aspect> missingAspects() {
        return this.aspects.getAspects().stream()
                .filter(aspect -> this.aspects.getAmount(aspect) < this.aspectsBase.getAmount(aspect))
                .toList();
    }

    private boolean handleUnstable(Level level) {
        if (this.nodeType != NodeType.UNSTABLE || this.tickCount % 100 != 0 || !level.random.nextBoolean()) {
            return false;
        }
        if (this.stabilizerLock > 0) {
            if (level.random.nextInt(10000 / this.stabilizerLock) == 42) {
                this.nodeType = NodeType.NORMAL;
                return true;
            }
            return false;
        }
        List<Aspect> primals = this.aspects.getPrimalAspects();
        if (primals.isEmpty()) {
            return false;
        }
        return this.reduceCurrentAspect(primals.get(level.random.nextInt(primals.size())), 1);
    }

    private boolean handleNodeDrain(Level level, BlockPos pos) {
        if (this.stabilizerLock == 1 || this.nodeModifier == NodeModifier.FADING
                || this.tickCount % drainInterval() != 0) {
            return false;
        }
        if (this.nodeModifier == NodeModifier.PALE && level.random.nextBoolean()) {
            return false;
        }

        BlockPos targetPos = pos.offset(level.random.nextInt(5) - level.random.nextInt(5),
                level.random.nextInt(5) - level.random.nextInt(5),
                level.random.nextInt(5) - level.random.nextInt(5));
        if (targetPos.equals(pos) || !(level.getBlockEntity(targetPos) instanceof AuraNodeBlockEntity other)
                || other.stabilizerLock > 0 || other.aspects.isEmpty()
                || other.averageStrength() >= this.averageStrength()) {
            return false;
        }

        List<Aspect> available = other.aspects.getAspects();
        Aspect aspect = available.get(level.random.nextInt(available.size()));
        if (!other.takeFromContainer(aspect, 1)) {
            return false;
        }

        if (this.aspects.getAmount(aspect) < this.getNodeVisBase(aspect)) {
            this.addToContainer(aspect, 1);
        } else {
            boolean bright = this.nodeModifier == NodeModifier.BRIGHT || this.nodeType == NodeType.HUNGRY;
            int bound = 1 + Math.max(0, (int) (this.getNodeVisBase(aspect) / (bright ? 1.5F : 1.0F)));
            if (level.random.nextInt(bound) == 0) {
                this.aspectsBase.add(aspect, 1);
                if (level.random.nextInt(3) == 0) {
                    other.setNodeVisBase(aspect, other.getNodeVisBase(aspect) - 1);
                }
            }
        }
        other.rechargeWait = Math.max(0, other.regenerationInterval() / 2);
        other.markChangedAndSync();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 from = Vec3.atCenterOf(targetPos);
            Vec3 to = Vec3.atCenterOf(pos);
            PacketDistributor.sendToPlayersNear(serverLevel, null, to.x, to.y, to.z, 32.0D,
                    new BlockZapFxPayload(from.x, from.y, from.z, to.x, to.y, to.z));
        }
        return true;
    }

    private boolean handleHungryPull(Level level, BlockPos pos) {
        if (this.nodeType != NodeType.HUNGRY) {
            return false;
        }
        boolean changed = false;
        Vec3 center = Vec3.atCenterOf(pos);
        for (Entity entity : level.getEntities((Entity) null, new AABB(pos).inflate(15.0D),
                entity -> entity.isAlive() && (!(entity instanceof Player player) || !player.isCreative()))) {
            double deltaX = center.x - entity.getX();
            double deltaY = center.y - entity.getY();
            double deltaZ = center.z - entity.getZ();
            double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared < 2.0D && !entity.isInvulnerable()) {
                AspectList consumedAspects = getConsumedEntityAspects(entity);
                entity.hurt(level.damageSources().fellOutOfWorld(), 1.0F);
                if (!entity.isAlive()) {
                    changed |= this.absorbConsumedAspects(level, consumedAspects);
                }
            }

            double normalizedX = deltaX / 15.0D;
            double normalizedY = deltaY / 15.0D;
            double normalizedZ = deltaZ / 15.0D;
            double normalizedDistance = Math.sqrt(
                    normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ);
            double force = 1.0D - normalizedDistance;
            if (force > 0.0D && normalizedDistance > 0.0D) {
                force *= force;
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        normalizedX / normalizedDistance * force * 0.15D,
                        normalizedY / normalizedDistance * force * 0.25D,
                        normalizedZ / normalizedDistance * force * 0.15D));
                entity.hasImpulse = true;
            }
        }
        return changed;
    }

    private void handleHungryBlockFx(Level level, BlockPos pos) {
        if (this.nodeType != NodeType.HUNGRY) {
            return;
        }

        int targetX = pos.getX() + level.random.nextInt(16) - level.random.nextInt(16);
        int targetY = pos.getY() + level.random.nextInt(16) - level.random.nextInt(16);
        int targetZ = pos.getZ() + level.random.nextInt(16) - level.random.nextInt(16);
        int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, targetX, targetZ);
        if (targetY > surface) {
            targetY = surface;
        }

        Vec3 nodeCenter = Vec3.atCenterOf(pos);
        Vec3 randomTarget = new Vec3(targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D);
        BlockHitResult hit = level.clip(new ClipContext(nodeCenter, randomTarget,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        BlockPos hitPos = hit.getBlockPos();
        if (nodeCenter.distanceToSqr(Vec3.atCenterOf(hitPos)) >= 256.0D) {
            return;
        }

        BlockState hitState = level.getBlockState(hitPos);
        if (!hitState.isAir()) {
            thaumcraft.common.services.ClientServices.get().hungryNodeBlockFx(level, hitPos, pos, hitState);
        }
    }

    private boolean absorbConsumedAspects(Level level, AspectList source) {
        AspectList primals = reduceToPrimals(source);
        if (primals.isEmpty()) {
            return false;
        }

        List<Aspect> available = primals.getAspects();
        Aspect aspect = available.get(level.random.nextInt(available.size()));
        if (this.aspects.getAmount(aspect) < this.getNodeVisBase(aspect)) {
            this.addToContainer(aspect, 1);
            return true;
        }

        int bound = 1 + this.getNodeVisBase(aspect) * 2;
        if (level.random.nextInt(bound) < primals.getAmount(aspect)) {
            this.aspectsBase.add(aspect, 1);
            return true;
        }
        return false;
    }

    private static AspectList getConsumedEntityAspects(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem().copy();
            stack.setCount(1);
            return ObjectAspectRegistry.getObjectTagsWithBonus(stack);
        }
        return EntityAspectRegistry.getEntityAspects(entity);
    }

    private static AspectList reduceToPrimals(AspectList source) {
        AspectList result = new AspectList();
        for (Aspect aspect : source.getAspects()) {
            reduceToPrimals(result, aspect, source.getAmount(aspect));
        }
        return result;
    }

    private static void reduceToPrimals(AspectList result, Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return;
        }
        if (aspect.isPrimal()) {
            result.add(aspect, amount);
            return;
        }
        Aspect[] components = aspect.getComponents();
        reduceToPrimals(result, components[0], amount);
        reduceToPrimals(result, components[1], amount);
    }

    private boolean handleHungryDestruction(Level level, BlockPos pos) {
        if (this.nodeType != NodeType.HUNGRY || this.tickCount % 50 != 0) {
            return false;
        }

        BlockPos target = pos.offset(level.random.nextInt(16) - level.random.nextInt(16),
                level.random.nextInt(16) - level.random.nextInt(16),
                level.random.nextInt(16) - level.random.nextInt(16));
        BlockHitResult hit = level.clip(new ClipContext(Vec3.atCenterOf(pos), Vec3.atCenterOf(target),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        BlockPos hitPos = hit.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);
        float hardness = hitState.getDestroySpeed(level, hitPos);
        if (!hitState.isAir() && !hitState.is(TCBlocks.AURA_NODE.get()) && hardness >= 0.0F && hardness < 5.0F) {
            level.destroyBlock(hitPos, true);
        }
        return false;
    }

    private boolean handleDegradation(Level level) {
        if (this.tickCount % 100 == 0 && this.nodeModifier == NodeModifier.FADING && this.stabilizerLock > 0
                && level.random.nextInt(12500 / this.stabilizerLock) == 69) {
            this.nodeModifier = NodeModifier.PALE;
            return true;
        }
        if (this.tickCount % 1200 != 0) {
            return false;
        }

        for (Aspect aspect : new ArrayList<>(this.aspects.getAspects())) {
            if (this.aspects.getAmount(aspect) > 0) {
                continue;
            }

            this.setNodeVisBase(aspect, this.getNodeVisBase(aspect) - 1);
            if (level.random.nextInt(20) == 0 || this.getNodeVisBase(aspect) <= 0) {
                this.aspects.remove(aspect);
                if (level.random.nextInt(5) == 0) {
                    if (this.nodeModifier == NodeModifier.BRIGHT) {
                        this.nodeModifier = null;
                    } else if (this.nodeModifier == null) {
                        this.nodeModifier = NodeModifier.PALE;
                    }
                    if (this.nodeModifier == NodeModifier.PALE && level.random.nextInt(5) == 0) {
                        this.nodeModifier = NodeModifier.FADING;
                    }
                }
            }
            if (this.aspects.isEmpty() && this.level != null) {
                this.level.removeBlock(this.worldPosition, false);
            }
            return true;
        }
        return false;
    }

    private int regenerationInterval() {
        int interval = switch (this.nodeModifier == null ? NodeModifier.BRIGHT : this.nodeModifier) {
            case BRIGHT -> this.nodeModifier == null ? 600 : 400;
            case PALE -> 900;
            case FADING -> 0;
        };
        return interval * switch (this.stabilizerLock) {
            case 1 -> 2;
            case 2 -> 20;
            default -> 1;
        };
    }

    private void updateStabilizerLock(Level level, BlockPos pos, BlockState state) {
        this.stabilizerLock = 0;
        if (!state.is(TCBlocks.AURA_NODE.get()) || pos.getY() <= level.getMinBuildHeight()) {
            return;
        }

        BlockPos stabilizerPos = pos.below();
        if (level.hasNeighborSignal(stabilizerPos)) {
            return;
        }

        BlockState stabilizer = level.getBlockState(stabilizerPos);
        if (stabilizer.is(TCBlocks.NODE_STABILIZER.get())) {
            this.stabilizerLock = 1;
        } else if (stabilizer.is(TCBlocks.ADVANCED_NODE_STABILIZER.get())) {
            this.stabilizerLock = 2;
        }
    }

    public int getStabilizerLock() {
        return this.stabilizerLock;
    }

    private int drainInterval() {
        if (this.nodeType == NodeType.HUNGRY || this.nodeModifier == NodeModifier.BRIGHT) {
            return 1;
        }
        return this.nodeModifier == NodeModifier.PALE ? 3 : 2;
    }

    private int averageStrength() {
        return (this.aspects.visSize() + this.aspectsBase.visSize()) / 2;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.aspects = AspectList.load(tag, "Aspects");
        this.aspectsBase = AspectList.load(tag, "AspectsBase");
        if (this.aspectsBase.isEmpty()) {
            this.aspectsBase = this.aspects.copy();
        }
        this.nodeType = safeNodeType(tag.getInt("type"));
        this.nodeModifier = NodeModifier.byOrdinal(tag.getInt("modifier"));
        if (tag.hasUUID("nodeId")) {
            this.nodeId = tag.getUUID("nodeId");
        }
        this.tickCount = tag.getInt("tickCount");
        this.rechargeWait = tag.getInt("rechargeWait");
        this.lastActive = tag.getLong("lastActive");
        int regeneration = this.regenerationInterval();
        int interval = regeneration * 75;
        this.catchUp = regeneration > 0 && this.lastActive > 0L
                && System.currentTimeMillis() > this.lastActive + interval;
        this.drainPlayerId = tag.contains("drainPlayer") ? tag.getInt("drainPlayer") : -1;
        this.drainColor = tag.contains("drainColor") ? tag.getInt("drainColor") : 0xFFFFFF;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.aspects.writeToNBT(tag, "Aspects");
        this.aspectsBase.writeToNBT(tag, "AspectsBase");
        tag.putInt("type", this.nodeType.ordinal());
        tag.putInt("modifier", this.nodeModifier == null ? -1 : this.nodeModifier.ordinal());
        tag.putUUID("nodeId", this.nodeId);
        tag.putInt("tickCount", this.tickCount);
        tag.putInt("rechargeWait", this.rechargeWait);
        tag.putLong("lastActive", this.lastActive);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);
        tag.putInt("drainPlayer", this.drainPlayerId);
        tag.putInt("drainColor", this.drainColor);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void configure(NodeType type, @Nullable NodeModifier modifier, AspectList aspects) {
        this.configure(type, modifier, aspects, aspects, UUID.randomUUID());
    }

    public void configure(NodeType type, @Nullable NodeModifier modifier, AspectList aspects, AspectList baseAspects,
            UUID nodeId) {
        this.nodeType = type;
        this.nodeModifier = modifier;
        this.aspects = aspects.copy();
        this.aspectsBase = baseAspects == null || baseAspects.isEmpty() ? aspects.copy() : baseAspects.copy();
        this.nodeId = nodeId == null ? UUID.randomUUID() : nodeId;
        this.markChangedAndSync();
    }

    public int getTickCount() {
        return this.tickCount;
    }

    private void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        InteractionHand hand = player.getMainHandItem() == wand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        player.startUsingItem(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ServerServices.get().startAuraNodeTap(serverPlayer, hand, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public int getDrainPlayerId() {
        return this.drainPlayerId;
    }

    public int getDrainColor() {
        return this.drainColor;
    }

    public void setDrainState(int playerId, int color) {
        if (this.drainPlayerId == playerId && this.drainColor == color) {
            return;
        }
        this.drainPlayerId = playerId;
        this.drainColor = color;
        this.markChangedAndSync();
    }

    public void clearDrainState(int playerId) {
        if (this.drainPlayerId != playerId) {
            return;
        }
        this.drainPlayerId = -1;
        this.markChangedAndSync();
    }

    @Override
    public String getNodeId() {
        return this.nodeId.toString();
    }

    @Override
    public AspectList getAspects() {
        return this.aspects;
    }

    @Override
    public AspectList getAspectsBase() {
        return this.aspectsBase;
    }

    @Override
    public NodeType getNodeType() {
        return this.nodeType;
    }

    @Override
    public void setNodeType(NodeType type) {
        this.nodeType = type;
        this.markChangedAndSync();
    }

    @Nullable
    @Override
    public NodeModifier getNodeModifier() {
        return this.nodeModifier;
    }

    @Override
    public void setNodeModifier(@Nullable NodeModifier modifier) {
        this.nodeModifier = modifier;
        this.markChangedAndSync();
    }

    @Override
    public int getNodeVisBase(Aspect aspect) {
        return this.aspectsBase.getAmount(aspect);
    }

    @Override
    public void setNodeVisBase(Aspect aspect, int amount) {
        int current = this.aspectsBase.getAmount(aspect);
        if (amount > current) {
            this.aspectsBase.add(aspect, amount - current);
        } else {
            this.aspectsBase.remove(aspect, current - Math.max(0, amount));
        }
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        int accepted = Math.min(amount, Math.max(0, this.aspectsBase.getAmount(aspect) - this.aspects.getAmount(aspect)));
        this.aspects.add(aspect, accepted);
        return amount - accepted;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return this.reduceCurrentAspect(aspect, amount);
    }

    private boolean reduceCurrentAspect(Aspect aspect, int amount) {
        int current = this.aspects.getAmount(aspect);
        if (amount <= 0 || current < amount) {
            return false;
        }
        this.aspects.setAmount(aspect, current - amount);
        return true;
    }

    private static NodeType safeNodeType(int ordinal) {
        return ordinal >= 0 && ordinal < NodeType.values().length ? NodeType.values()[ordinal] : NodeType.NORMAL;
    }

    private static AspectList defaultAspects() {
        AspectList aspects = new AspectList();
        Aspect.getPrimalAspects().forEach(aspect -> aspects.add(aspect, 25));
        return aspects;
    }
}
