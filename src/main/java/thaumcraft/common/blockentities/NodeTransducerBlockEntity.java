package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public class NodeTransducerBlockEntity extends BlockEntity {
    public static final int MAX_PROGRESS = 1000;
    public static final int REVERSE_COMPLETE_PROGRESS = 50;
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_FORWARD = 1;
    public static final int STATUS_REVERSE = 2;

    private int progress;
    private int status;
    private int tickCount;
    private boolean checkStatus = true;

    public NodeTransducerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.NODE_TRANSDUCER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, NodeTransducerBlockEntity transducer) {
        transducer.tickCount++;
        if (level.isClientSide) {
            return;
        }
        transducer.serverTick(level, pos);
    }

    private void serverTick(Level level, BlockPos pos) {
        if (this.checkStatus || this.tickCount % 20 == 0) {
            this.checkStatus = false;
            this.refreshStatus(level, pos);
        }

        boolean powered = level.hasNeighborSignal(pos);
        boolean changed = false;
        if (this.status != STATUS_IDLE && powered) {
            int next = Math.min(MAX_PROGRESS, this.progress + 1);
            changed |= next != this.progress;
            this.progress = next;
            if (this.status == STATUS_FORWARD && level.getBlockEntity(pos.below()) instanceof AuraNodeBlockEntity node
                    && node.drainRandomCurrentAspect(level, 1)
                    && (this.progress % 5 == 0 || node.getAspects().visSize() == 0)) {
                node.markChangedAndSync();
            }
        } else if (this.progress > 0) {
            int next = Math.max(0, this.progress - 1);
            changed |= next != this.progress;
            this.progress = next;
        }

        if (this.status == STATUS_FORWARD && this.progress >= MAX_PROGRESS) {
            changed |= this.convertForward(level, pos);
        } else if (this.status == STATUS_REVERSE && this.progress <= REVERSE_COMPLETE_PROGRESS) {
            changed |= this.convertReverse(level, pos);
        } else if (this.status == STATUS_REVERSE && this.progress > REVERSE_COMPLETE_PROGRESS
                && !this.hasActiveStabilizer(level, pos.below())) {
            changed |= this.failUnstableEnergizedStack(level, pos);
        }

        if (this.shouldShowConversionBolts(level, pos, powered) && this.tickCount % 10 == 0) {
            this.sendConversionBolts(level, pos);
        }

        if (changed || this.tickCount % 20 == 0) {
            this.markChangedAndSync();
        }
    }

    private void refreshStatus(Level level, BlockPos pos) {
        int previousStatus = this.status;
        BlockPos nodePos = pos.below();
        if (level.getBlockEntity(nodePos) instanceof AuraNodeBlockEntity node) {
            if (node.isEnergized()) {
                this.status = STATUS_REVERSE;
                if (this.progress <= REVERSE_COMPLETE_PROGRESS) {
                    this.progress = MAX_PROGRESS;
                }
            } else if (level.hasNeighborSignal(pos) && this.hasActiveStabilizer(level, nodePos)) {
                this.status = STATUS_FORWARD;
            } else if (this.progress <= 0) {
                this.status = STATUS_IDLE;
            }
        } else if (this.progress <= 0) {
            this.status = STATUS_IDLE;
        }

        if (previousStatus != this.status) {
            this.markChangedAndSync();
        }
    }

    private boolean convertForward(Level level, BlockPos pos) {
        BlockPos nodePos = pos.below();
        if (!(level.getBlockEntity(nodePos) instanceof AuraNodeBlockEntity node)
                || node.isEnergized()
                || !this.hasActiveStabilizer(level, nodePos)) {
            this.refreshStatus(level, pos);
            return false;
        }

        node.convertToEnergizedFromNatural(level);
        this.status = STATUS_REVERSE;
        this.progress = MAX_PROGRESS;
        this.playCompletionEffects(level, nodePos);
        return true;
    }

    private boolean convertReverse(Level level, BlockPos pos) {
        BlockPos nodePos = pos.below();
        if (!(level.getBlockEntity(nodePos) instanceof AuraNodeBlockEntity node) || !node.isEnergized()) {
            this.refreshStatus(level, pos);
            return false;
        }

        node.convertToDrainedNaturalFromEnergized();
        this.status = STATUS_IDLE;
        this.progress = 0;
        this.playCompletionEffects(level, nodePos);
        return true;
    }

    private boolean failUnstableEnergizedStack(Level level, BlockPos pos) {
        BlockPos nodePos = pos.below();
        if (level.getBlockEntity(nodePos) instanceof AuraNodeBlockEntity node && node.isEnergized()) {
            explodifyEnergizedNode(level, nodePos);
            this.status = STATUS_IDLE;
            this.progress = REVERSE_COMPLETE_PROGRESS;
            return true;
        }
        this.refreshStatus(level, pos);
        return false;
    }

    public static void explodifyEnergizedNode(Level level, BlockPos nodePos) {
        level.removeBlock(nodePos, false);
        level.explode(null, nodePos.getX() + 0.5D, nodePos.getY() + 0.5D, nodePos.getZ() + 0.5D, 3.0F,
                Level.ExplosionInteraction.NONE);

        for (int index = 0; index < 50; index++) {
            BlockPos fluxPos = nodePos.offset(level.random.nextInt(8) - level.random.nextInt(8),
                    level.random.nextInt(8) - level.random.nextInt(8),
                    level.random.nextInt(8) - level.random.nextInt(8));
            if (level.isEmptyBlock(fluxPos)) {
                level.setBlock(fluxPos, (fluxPos.getY() < nodePos.getY()
                        ? TCBlocks.FLUX_GOO.get()
                        : TCBlocks.FLUX_GAS.get()).defaultBlockState(), 3);
            }
        }
    }

    private boolean hasActiveStabilizer(Level level, BlockPos nodePos) {
        BlockPos stabilizerPos = nodePos.below();
        if (level.hasNeighborSignal(stabilizerPos)) {
            return false;
        }
        BlockState stabilizer = level.getBlockState(stabilizerPos);
        return stabilizer.is(TCBlocks.NODE_STABILIZER.get())
                || stabilizer.is(TCBlocks.ADVANCED_NODE_STABILIZER.get());
    }

    private void sendConversionBolts(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos nodePos = pos.below();
        double nodeX = nodePos.getX() + 0.5D;
        double nodeY = nodePos.getY() + 0.5D;
        double nodeZ = nodePos.getZ() + 0.5D;
        PacketDistributor.sendToPlayersNear(serverLevel, null, nodeX, nodeY, nodeZ, 32.0D,
                new BlockZapFxPayload(randomInside(level, pos.getX()), pos.getY() + 0.5D,
                        randomInside(level, pos.getZ()), nodeX, nodeY, nodeZ));
        if (this.hasActiveStabilizer(level, nodePos)) {
            BlockPos stabilizerPos = nodePos.below();
            PacketDistributor.sendToPlayersNear(serverLevel, null, nodeX, nodeY, nodeZ, 32.0D,
                    new BlockZapFxPayload(randomInside(level, stabilizerPos.getX()), stabilizerPos.getY() + 0.5D,
                            randomInside(level, stabilizerPos.getZ()), nodeX, nodeY, nodeZ));
        }
    }

    private boolean shouldShowConversionBolts(Level level, BlockPos pos, boolean powered) {
        if (this.progress <= REVERSE_COMPLETE_PROGRESS || this.status == STATUS_IDLE) {
            return false;
        }
        if (this.status == STATUS_FORWARD) {
            return powered && this.progress < MAX_PROGRESS
                    && level.getBlockEntity(pos.below()) instanceof AuraNodeBlockEntity node
                    && !node.isEnergized();
        }
        return !powered && level.getBlockEntity(pos.below()) instanceof AuraNodeBlockEntity node
                && node.isEnergized();
    }

    private void playCompletionEffects(Level level, BlockPos nodePos) {
        level.levelEvent(2001, nodePos, Block.getId(TCBlocks.AURA_NODE.get().defaultBlockState()));
        level.playSound(null, nodePos, TCSoundEvents.CRAFTFAIL.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    private void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void requestStatusCheck() {
        this.checkStatus = true;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getStatus() {
        return this.status;
    }

    public float getProgressScale() {
        return this.progress / (float) MAX_PROGRESS;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = Mth.clamp(tag.getInt("progress"), 0, MAX_PROGRESS);
        this.status = Mth.clamp(tag.getInt("status"), STATUS_IDLE, STATUS_REVERSE);
        this.tickCount = tag.getInt("tickCount");
        this.checkStatus = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("status", this.status);
        tag.putInt("tickCount", this.tickCount);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static double randomInside(Level level, int blockCoord) {
        return blockCoord + 0.25D + level.random.nextDouble() * 0.5D;
    }
}
