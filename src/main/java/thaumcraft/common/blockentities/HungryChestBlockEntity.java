package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

public class HungryChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

    public float lidAngle;
    public float prevLidAngle;
    private int numUsingPlayers;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {

            level.blockEvent(pos, state.getBlock(), 1, openCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu menu) {
                Container container = menu.getContainer();
                return container == HungryChestBlockEntity.this;
            }
            return false;
        }
    };

    public HungryChestBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.HUNGRY_CHEST.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HungryChestBlockEntity be) {
        be.prevLidAngle = be.lidAngle;
        float step = 0.1F;

        if ((be.numUsingPlayers == 0 && be.lidAngle > 0.0F) || (be.numUsingPlayers > 0 && be.lidAngle < 1.0F)) {
            float prev = be.lidAngle;

            if (be.numUsingPlayers > 0) {
                be.lidAngle += step;
            } else {
                be.lidAngle -= step;
            }

            be.lidAngle = Math.min(be.lidAngle, 1.0F);

            if (be.lidAngle < 0.5F && prev >= 0.5F) {
                level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);
            }

            be.lidAngle = Math.max(be.lidAngle, 0.0F);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {

            this.numUsingPlayers = type;
            if (type > 0 && this.lidAngle == 0.0F) {
                level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, SoundEvents.CHEST_OPEN,
                        SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        if (id == 2) {

            if (this.lidAngle < 0.2F) {
                this.lidAngle = 0.2F;
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    public void onItemEaten() {
        if (level != null && !level.isClientSide) {

            level.blockEvent(worldPosition, getBlockState().getBlock(), 2, 2);
        }
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTicks;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.thaumicrecords.hungry_chest");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return ChestMenu.threeRows(containerId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public void startOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}



