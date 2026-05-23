package thaumcraft.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.common.blocks.EssentiaTubeBlock;
import thaumcraft.common.blocks.EssentiaTubeBlock.TubeMode;
import thaumcraft.common.registry.TCBlockEntities;

public class EssentiaTubeBlockEntity extends BlockEntity implements IEssentiaContainer {
    private static final int CAPACITY = 8;
    private static final int BUFFER_CAPACITY = 32;
    private static final int TRANSFER_INTERVAL = 5;

    private EssentiaStorage storage = EssentiaStorage.EMPTY;
    @Nullable
    private Aspect filterAspect;
    @Nullable
    private Direction lastSourceDirection;
    private boolean enabled = true;

    public EssentiaTubeBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.ESSENTIA_TUBE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        if (level.getGameTime() % TRANSFER_INTERVAL != 0) {
            return;
        }

        if (tube.storage.isEmpty()) {
            tube.pullFromNeighbor(level, pos);
        } else {
            tube.pushToNeighbor(level, pos);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int amount = tag.getInt("amount");
        Aspect aspect = Aspect.byTag(tag.getString("aspect")).orElse(Aspect.AIR);
        this.storage = amount > 0 ? new EssentiaStorage(aspect, amount).withAmount(amount, this.getEssentiaCapacity())
                : EssentiaStorage.EMPTY;
        this.enabled = !tag.contains("enabled") || tag.getBoolean("enabled");
        this.filterAspect = Aspect.byTag(tag.getString("filter")).orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storage.isEmpty()) {
            tag.putString("aspect", this.storage.aspect().getTag());
            tag.putInt("amount", this.storage.amount());
        }
        tag.putBoolean("enabled", this.enabled);
        if (this.filterAspect != null) {
            tag.putString("filter", this.filterAspect.getTag());
        }
    }

    @Override
    public EssentiaStorage getEssentia() {
        return this.storage;
    }

    @Override
    public int getEssentiaCapacity() {
        return this.getMode() == TubeMode.BUFFER ? BUFFER_CAPACITY : CAPACITY;
    }

    @Override
    public int fillEssentia(Aspect aspect, int amount, boolean simulate) {
        if (amount <= 0 || !this.enabled || !this.canAccept(aspect)) {
            return 0;
        }

        int accepted = Math.min(amount, this.getEssentiaCapacity() - this.storage.amount());
        if (accepted > 0 && !simulate) {
            this.storage = new EssentiaStorage(aspect, this.storage.amount() + accepted);
            this.markStorageChanged();
        }
        return accepted;
    }

    @Override
    public int drainEssentia(Aspect aspect, int amount, boolean simulate) {
        if (amount <= 0 || !this.enabled || this.storage.isEmpty() || this.storage.aspect() != aspect) {
            return 0;
        }

        int drained = Math.min(amount, this.storage.amount());
        if (drained > 0 && !simulate) {
            int remaining = this.storage.amount() - drained;
            this.storage = remaining > 0 ? this.storage.withAmount(remaining, this.getEssentiaCapacity())
                    : EssentiaStorage.EMPTY;
            this.markStorageChanged();
        }
        return drained;
    }

    @Override
    public boolean canAccept(Aspect aspect) {
        return IEssentiaContainer.super.canAccept(aspect) && (this.filterAspect == null || this.filterAspect == aspect);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
        this.markStorageChanged();
    }

    public void setFilterAspect(Aspect aspect) {
        this.filterAspect = aspect;
        this.markStorageChanged();
    }

    public void clearFilterAspect() {
        this.filterAspect = null;
        this.markStorageChanged();
    }

    private void pullFromNeighbor(Level level, BlockPos pos) {
        if (!this.enabled) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (!this.canPullFrom(direction)) {
                continue;
            }

            IEssentiaContainer neighbor = getEssentiaContainer(level, pos.relative(direction));
            if (neighbor == null || neighbor.getEssentia().isEmpty()) {
                continue;
            }

            EssentiaStorage neighborStorage = neighbor.getEssentia();
            int drained = neighbor.drainEssentia(neighborStorage.aspect(), 1, true);
            if (drained > 0 && this.fillEssentia(neighborStorage.aspect(), drained, true) == drained) {
                neighbor.drainEssentia(neighborStorage.aspect(), drained, false);
                this.fillEssentia(neighborStorage.aspect(), drained, false);
                this.lastSourceDirection = direction;
                return;
            }
        }
    }

    private void pushToNeighbor(Level level, BlockPos pos) {
        if (!this.enabled) {
            return;
        }

        Direction onlyOutput = this.getDirectionalOutput();
        if (onlyOutput != null) {
            this.tryPush(level, pos, onlyOutput, true);
            return;
        }

        if (this.tryPushToPreferredNeighbor(level, pos, false)) {
            return;
        }

        this.tryPushToPreferredNeighbor(level, pos, true);
    }

    private boolean tryPushToPreferredNeighbor(Level level, BlockPos pos, boolean includeRestricted) {
        for (Direction direction : Direction.values()) {
            if (direction != this.lastSourceDirection && this.tryPush(level, pos, direction, includeRestricted)) {
                return true;
            }
        }

        return this.lastSourceDirection != null && this.tryPush(level, pos, this.lastSourceDirection, includeRestricted);
    }

    private boolean tryPush(Level level, BlockPos pos, Direction direction, boolean includeRestricted) {
        if (!this.canPushTo(direction)) {
            return false;
        }

        IEssentiaContainer neighbor = getEssentiaContainer(level, pos.relative(direction));
        if (neighbor == null || this.storage.isEmpty()) {
            return false;
        }
        if (!includeRestricted && getMode(neighbor) == TubeMode.RESTRICTED) {
            return false;
        }

        Aspect aspect = this.storage.aspect();
        int accepted = neighbor.fillEssentia(aspect, 1, true);
        if (accepted <= 0) {
            return false;
        }

        this.drainEssentia(aspect, accepted, false);
        neighbor.fillEssentia(aspect, accepted, false);
        if (this.storage.isEmpty()) {
            this.lastSourceDirection = null;
        }
        return true;
    }

    private boolean canPullFrom(Direction direction) {
        return this.getMode() != TubeMode.DIRECTIONAL || this.lastSourceDirection == null
                || direction == this.lastSourceDirection;
    }

    private boolean canPushTo(Direction direction) {
        return this.getMode() != TubeMode.DIRECTIONAL || this.lastSourceDirection == null
                || direction == this.lastSourceDirection.getOpposite();
    }

    @Nullable
    private Direction getDirectionalOutput() {
        return this.getMode() == TubeMode.DIRECTIONAL && this.lastSourceDirection != null
                ? this.lastSourceDirection.getOpposite()
                : null;
    }

    private TubeMode getMode() {
        return this.getBlockState().getBlock() instanceof EssentiaTubeBlock tube ? tube.getMode() : TubeMode.NORMAL;
    }

    private static TubeMode getMode(IEssentiaContainer container) {
        return container instanceof EssentiaTubeBlockEntity tube ? tube.getMode() : TubeMode.NORMAL;
    }

    @Nullable
    private static IEssentiaContainer getEssentiaContainer(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof IEssentiaContainer container ? container : null;
    }

    private void markStorageChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
