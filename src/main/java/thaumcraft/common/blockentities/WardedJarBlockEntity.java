package thaumcraft.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.util.ServerEssentiaTransportHooks;

public class WardedJarBlockEntity extends BlockEntity implements IEssentiaContainer, IAspectSource, IEssentiaTransport, IWandable {
    public static final int CAPACITY = 64;

    private EssentiaStorage storage = EssentiaStorage.EMPTY;
    @Nullable
    private Aspect filterAspect;
    private Direction facing = Direction.NORTH;
    private int count;

    public WardedJarBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.WARDED_JAR.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
        ServerEssentiaTransportHooks.tickWardedJar(level, pos, state, jar);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int amount = tag.getInt("amount");
        if (amount <= 0 && tag.contains("Amount")) {
            amount = tag.getInt("Amount");
        }
        Aspect aspect = Aspect.byTag(tag.getString("aspect")).orElseGet(
                () -> Aspect.byTag(tag.getString("Aspect")).orElse(Aspect.AIR));
        this.storage = amount > 0 ? new EssentiaStorage(aspect, amount).withAmount(amount, this.getEssentiaCapacity())
                : EssentiaStorage.EMPTY;
        this.filterAspect = Aspect.byTag(tag.getString("AspectFilter")).orElse(null);
        this.facing = Direction.byName(tag.getString("facing"));
        if (this.facing == null || this.facing.getAxis().isVertical()) {
            this.facing = Direction.NORTH;
        }
        this.count = tag.getInt("count");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storage.isEmpty()) {
            tag.putString("aspect", this.storage.aspect().getTag());
            tag.putInt("amount", this.storage.amount());
            tag.putString("Aspect", this.storage.aspect().getTag());
            tag.putInt("Amount", this.storage.amount());
        }
        if (this.filterAspect != null) {
            tag.putString("AspectFilter", this.filterAspect.getTag());
        }
        tag.putString("facing", this.facing.getName());
        tag.putInt("count", this.count);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public EssentiaStorage getEssentia() {
        return this.storage;
    }

    public void setEssentia(EssentiaStorage storage) {
        this.storage = storage.isEmpty() ? EssentiaStorage.EMPTY : storage.withAmount(storage.amount(), this.getEssentiaCapacity());
        this.markStorageChanged();
    }

    @Nullable
    public Aspect getFilterAspect() {
        return this.filterAspect;
    }

    public void setFilterAspect(@Nullable Aspect filterAspect) {
        this.filterAspect = filterAspect;
        this.markStorageChanged();
    }

    public Direction getFacing() {
        return this.facing;
    }

    public void setFacing(Direction facing) {
        if (facing.getAxis().isHorizontal()) {
            this.facing = facing;
            this.markStorageChanged();
        }
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

        if (this.isVoidJar()) {
            if (!simulate) {
                int storedAmount = Math.min(this.getEssentiaCapacity(),
                        (this.storage.isEmpty() ? 0 : this.storage.amount()) + amount);
                this.storage = new EssentiaStorage(aspect, storedAmount);
                this.markStorageChanged();
            }
            return amount;
        }

        int accepted = Math.min(amount, this.getEssentiaCapacity() - this.storage.amount());
        if (accepted > 0 && !simulate) {
            this.storage = new EssentiaStorage(aspect, this.storage.amount() + accepted);
            this.markStorageChanged();
        }
        return accepted;
    }

    @Override
    public boolean canAccept(Aspect aspect) {
        return (this.filterAspect == null || this.filterAspect == aspect)
                && (this.storage.isEmpty() || this.storage.aspect() == aspect);
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

    public void clearEssentia() {
        if (!this.storage.isEmpty()) {
            this.storage = EssentiaStorage.EMPTY;
            this.markStorageChanged();
        }
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            this.clearEssentia();
            level.playSound(null, pos, TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 0.4F, 1.0F);
            level.playSound(null, pos, TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.3F, 1.0F);
        }
        player.swing(player.getUsedItemHand(), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
    }

    @Nullable
    @Override
    public Aspect getSuctionType(@Nullable Direction face) {
        return this.filterAspect != null ? this.filterAspect : this.storage.isEmpty() ? null : this.storage.aspect();
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        if (this.isVoidJar()) {
            return this.filterAspect != null && this.storage.amount() < this.getEssentiaCapacity() ? 48 : 32;
        }
        return this.storage.amount() < this.getEssentiaCapacity() ? this.getMinimumSuction() : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return this.canOutputTo(face) ? this.drainEssentia(aspect, amount, false) : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return this.canInputFrom(face) ? this.fillEssentia(aspect, amount, false) : 0;
    }

    @Nullable
    @Override
    public Aspect getEssentiaType(@Nullable Direction face) {
        return this.storage.isEmpty() ? null : this.storage.aspect();
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        return this.storage.amount();
    }

    @Override
    public int getMinimumSuction() {
        if (this.isVoidJar()) {
            return this.filterAspect != null ? 48 : 32;
        }
        return this.filterAspect != null ? 64 : 32;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    public int incrementCount() {
        return ++this.count;
    }

    public boolean isVoidJar() {
        return this.getBlockState().is(TCBlocks.VOID_JAR.get());
    }

    private void markStorageChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
