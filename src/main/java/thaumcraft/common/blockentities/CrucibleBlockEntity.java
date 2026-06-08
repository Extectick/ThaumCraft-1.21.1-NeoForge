package thaumcraft.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.services.ClientServices;
import thaumcraft.common.util.ServerCrucibleHooks;

public class CrucibleBlockEntity extends BlockEntity implements IWandable {
    public static final int MAX_WATER = 1000;
    public static final int MAX_TAGS = 100;

    private final AspectList aspects = new AspectList();
    private int waterAmount;
    private int heat;
    private long counter = -100L;
    private int entityDamageDelay;

    public CrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.CRUCIBLE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        ServerCrucibleHooks.tickCrucible(level, pos, state, crucible);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        ClientServices.get().tickCrucible(crucible);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.heat = tag.getShort("Heat");
        this.waterAmount = tag.contains("Water") ? tag.getInt("Water") : tag.getInt("WaterAmount");
        this.counter = tag.contains("Counter") ? tag.getLong("Counter") : -100L;
        this.aspects.readFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putShort("Heat", (short) this.heat);
        tag.putInt("Water", this.waterAmount);
        tag.putLong("Counter", this.counter);
        this.aspects.writeToNBT(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int getHeat() {
        return this.heat;
    }

    public void setHeat(int heat) {
        this.heat = Math.clamp(heat, 0, 200);
    }

    public int getWaterAmount() {
        return this.waterAmount;
    }

    public boolean hasWater() {
        return this.waterAmount > 0;
    }

    public boolean isBoiling() {
        return this.waterAmount > 0 && this.heat > 150;
    }

    public boolean addWater(int amount) {
        if (amount <= 0 || this.waterAmount >= MAX_WATER) {
            return false;
        }
        this.waterAmount = Math.min(MAX_WATER, this.waterAmount + amount);
        this.markChanged();
        return true;
    }

    public void drainWater(int amount) {
        if (amount <= 0 || this.waterAmount <= 0) {
            return;
        }
        this.waterAmount = Math.max(0, this.waterAmount - amount);
        this.markChanged();
    }

    public AspectList getAspects() {
        return this.aspects.copy();
    }

    public void setAspects(AspectList aspects) {
        this.aspects.getAspects().forEach(this.aspects::remove);
        this.aspects.add(aspects);
        this.markChanged();
    }

    public void addAspects(AspectList added) {
        this.aspects.add(added);
        this.markChanged();
    }

    public void removeAspect(Aspect aspect, int amount) {
        this.aspects.remove(aspect, amount);
        this.markChanged();
    }

    public int tagAmount() {
        return this.aspects.visSize();
    }

    public long incrementCounter() {
        return ++this.counter;
    }

    public long getCounter() {
        return this.counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public int getEntityDamageDelay() {
        return this.entityDamageDelay;
    }

    public void setEntityDamageDelay(int entityDamageDelay) {
        this.entityDamageDelay = entityDamageDelay;
    }

    public double getFluidHeight() {
        double base = 0.3D + 0.5D * this.waterAmount / MAX_WATER;
        double out = base + (double) this.tagAmount() / MAX_TAGS * (1.0D - base);
        if (out > 1.0D) {
            return 1.001D;
        }
        return out == 1.0D ? 0.9999D : out;
    }

    public void clearContents() {
        this.waterAmount = 0;
        this.aspects.getAspects().forEach(this.aspects::remove);
        this.markChanged();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (this.level != null && this.level.isClientSide) {
            ClientServices.get().crucibleBlockEvent(this, id, type);
        }
        return id == 1 || id == 2 || super.triggerEvent(id, type);
    }

    public void markChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ServerCrucibleHooks.spillCrucibleRemnants(level, pos, this);
            level.playSound(null, pos, TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.45F, 1.0F);
        }
        player.swing(player.getUsedItemHand(), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
