package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

public class RunicMatrixBlockEntity extends BlockEntity {
    private boolean active;
    private boolean crafting;
    private int instability;
    private int craftCount;

    public RunicMatrixBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.RUNIC_MATRIX.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.active = tag.getBoolean("active");
        this.crafting = tag.getBoolean("crafting");
        this.instability = tag.getInt("instability");
        this.craftCount = tag.getInt("craftCount");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", this.active);
        tag.putBoolean("crafting", this.crafting);
        tag.putInt("instability", this.instability);
        tag.putInt("craftCount", this.craftCount);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isCrafting() {
        return this.crafting;
    }

    public int getInstability() {
        return this.instability;
    }

    public int getCraftCount() {
        return this.craftCount;
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.markChangedAndSync();
        }
    }

    public void setCrafting(boolean crafting) {
        if (this.crafting != crafting) {
            this.crafting = crafting;
            this.markChangedAndSync();
        }
    }

    public void setInstability(int instability) {
        this.instability = Math.max(0, instability);
        this.markChangedAndSync();
    }

    private void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
