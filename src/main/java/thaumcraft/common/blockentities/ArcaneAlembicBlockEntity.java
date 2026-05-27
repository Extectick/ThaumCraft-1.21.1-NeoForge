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
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;

public class ArcaneAlembicBlockEntity extends BlockEntity implements IEssentiaContainer, IEssentiaTransport, IWandable {
    public static final int CAPACITY = 32;

    private EssentiaStorage storage = EssentiaStorage.EMPTY;
    @Nullable
    private Aspect filterAspect;
    private Direction facing = Direction.NORTH;

    public ArcaneAlembicBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.ARCANE_ALEMBIC.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int amount = tag.getInt("amount");
        Aspect aspect = Aspect.byTag(tag.getString("aspect")).orElse(Aspect.AIR);
        this.storage = amount > 0 ? new EssentiaStorage(aspect, amount).withAmount(amount, this.getEssentiaCapacity())
                : EssentiaStorage.EMPTY;
        this.filterAspect = Aspect.byTag(tag.getString("AspectFilter")).orElse(null);
        this.facing = Direction.from3DDataValue(tag.getByte("facing"));
        if (this.facing.getAxis().isVertical()) {
            this.facing = Direction.NORTH;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storage.isEmpty()) {
            tag.putString("aspect", this.storage.aspect().getTag());
            tag.putInt("amount", this.storage.amount());
        }
        if (this.filterAspect != null) {
            tag.putString("AspectFilter", this.filterAspect.getTag());
        }
        tag.putByte("facing", (byte) this.facing.get3DDataValue());
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
    public boolean canAccept(Aspect aspect) {
        return this.storage.isEmpty() || this.storage.aspect() == aspect;
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

    @Nullable
    public Aspect getFilterAspect() {
        return this.filterAspect;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public void setFacing(Direction facing) {
        if (!facing.getAxis().isVertical()) {
            this.facing = facing;
            this.markStorageChanged();
        }
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
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                this.clearEssentia();
                level.playSound(null, pos, TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
            }
            player.swing(player.getUsedItemHand(), true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        Direction side = hitResult.getDirection();
        if (side.getAxis().isVertical()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            this.setFacing(side);
        }
        player.swing(player.getUsedItemHand(), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean canOutputTo(Direction face) {
        return face != Direction.DOWN && face != this.facing;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != Direction.DOWN && face != this.facing;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
    }

    @Nullable
    @Override
    public Aspect getSuctionType(@Nullable Direction face) {
        return null;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (this.canOutputTo(face) && !this.storage.isEmpty() && this.storage.aspect() == aspect && amount > 0) {
            return this.drainEssentia(aspect, amount, false);
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
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
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    private void markStorageChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
