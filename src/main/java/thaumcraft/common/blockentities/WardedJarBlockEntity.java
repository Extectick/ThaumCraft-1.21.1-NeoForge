package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.common.registry.TCBlockEntities;

public class WardedJarBlockEntity extends BlockEntity implements IEssentiaContainer {
    public static final int CAPACITY = 64;

    private EssentiaStorage storage = EssentiaStorage.EMPTY;

    public WardedJarBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.WARDED_JAR.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int amount = tag.getInt("amount");
        Aspect aspect = Aspect.byTag(tag.getString("aspect")).orElse(Aspect.AIR);
        this.storage = amount > 0 ? new EssentiaStorage(aspect, amount).withAmount(amount, this.getEssentiaCapacity())
                : EssentiaStorage.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storage.isEmpty()) {
            tag.putString("aspect", this.storage.aspect().getTag());
            tag.putInt("amount", this.storage.amount());
        }
    }

    @Override
    public EssentiaStorage getEssentia() {
        return this.storage;
    }

    @Override
    public int getEssentiaCapacity() {
        return CAPACITY;
    }

    @Override
    public int fillEssentia(Aspect aspect, int amount, boolean simulate) {
        if (amount <= 0 || !this.canAccept(aspect)) {
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
        if (amount <= 0 || this.storage.isEmpty() || this.storage.aspect() != aspect) {
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

    private void markStorageChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
