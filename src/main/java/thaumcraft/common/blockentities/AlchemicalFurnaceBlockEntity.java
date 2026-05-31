package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.menus.AlchemicalFurnaceMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.util.ServerEssentiaTransportHooks;

public class AlchemicalFurnaceBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int CONTAINER_SIZE = 2;
    public static final int MAX_VIS = 50;
    private static final int[] SLOTS_BOTTOM = new int[] { FUEL_SLOT };
    private static final int[] SLOTS_TOP = new int[0];
    private static final int[] SLOTS_SIDES = new int[] { INPUT_SLOT };

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final AspectList aspects = new AspectList();
    private int vis;
    private int smeltTime = 100;
    private int burnTime;
    private int currentBurnTime;
    private int cookTime;
    private int count;
    private boolean speedBoost;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AlchemicalFurnaceBlockEntity.this.cookTime;
                case 1 -> AlchemicalFurnaceBlockEntity.this.burnTime;
                case 2 -> AlchemicalFurnaceBlockEntity.this.currentBurnTime;
                case 3 -> AlchemicalFurnaceBlockEntity.this.vis;
                case 4 -> AlchemicalFurnaceBlockEntity.this.smeltTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AlchemicalFurnaceBlockEntity.this.cookTime = value;
                case 1 -> AlchemicalFurnaceBlockEntity.this.burnTime = value;
                case 2 -> AlchemicalFurnaceBlockEntity.this.currentBurnTime = value;
                case 3 -> AlchemicalFurnaceBlockEntity.this.vis = value;
                case 4 -> AlchemicalFurnaceBlockEntity.this.smeltTime = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public AlchemicalFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.ALCHEMICAL_FURNACE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemicalFurnaceBlockEntity furnace) {
        ServerEssentiaTransportHooks.tickAlchemicalFurnace(level, pos, state, furnace);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.aspects.readFromNBT(tag);
        this.vis = this.aspects.visSize();
        this.speedBoost = tag.getBoolean("speedBoost");
        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.smeltTime = Math.max(1, tag.getShort("SmeltTime"));
        this.currentBurnTime = getBurnTime(this.items.get(FUEL_SLOT));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        this.aspects.writeToNBT(tag);
        tag.putBoolean("speedBoost", this.speedBoost);
        tag.putShort("BurnTime", (short) this.burnTime);
        tag.putShort("CookTime", (short) this.cookTime);
        tag.putShort("SmeltTime", (short) this.smeltTime);
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public int getStoredVis() {
        return this.vis;
    }

    public int getSmeltTime() {
        return this.smeltTime;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    public int getBurnTimeRemaining() {
        return this.burnTime;
    }

    public AspectList getStoredAspects() {
        return this.aspects.copy();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.alchemical_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlchemicalFurnaceMenu(containerId, playerInventory, this, this.dataAccess);
    }

    public Aspect takeRandomAspect(Level level, AspectList excluded) {
        if (this.aspects.size() <= 0) {
            return null;
        }

        AspectList available = this.aspects.copy();
        for (Aspect aspect : excluded.getAspects()) {
            available.remove(aspect);
        }

        if (available.size() <= 0) {
            return null;
        }

        Aspect aspect = available.getAspects().get(level.random.nextInt(available.size()));
        this.aspects.remove(aspect, 1);
        this.vis--;
        return aspect;
    }

    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (this.aspects.getAmount(aspect) < amount) {
            return false;
        }
        this.aspects.remove(aspect, amount);
        this.vis -= amount;
        return true;
    }

    public void consumeFuel() {
        ItemStack fuel = this.items.get(FUEL_SLOT);
        if (fuel.isEmpty()) {
            return;
        }

        ItemStack remainder = fuel.getCraftingRemainingItem();
        fuel.shrink(1);
        this.items.set(FUEL_SLOT, fuel.isEmpty() ? remainder : fuel);
    }

    public int incrementCount() {
        return ++this.count;
    }

    public boolean hasStoredAspects() {
        return this.aspects.size() > 0;
    }

    public boolean hasStoredVis() {
        return this.vis > 0;
    }

    public boolean isSpeedBoosted() {
        return this.speedBoost;
    }

    public void setSpeedBoost(boolean speedBoost) {
        this.speedBoost = speedBoost;
    }

    public void decrementBurnTime() {
        if (this.burnTime > 0) {
            this.burnTime--;
        }
    }

    public void setBurnFromFuel(int burnTime) {
        this.currentBurnTime = burnTime;
        this.burnTime = burnTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = Math.max(0, cookTime);
    }

    public void setSmeltTime(int smeltTime) {
        this.smeltTime = Math.max(1, smeltTime);
    }

    public int getRemainingVisCapacity() {
        return MAX_VIS - this.vis;
    }

    public void addAspects(AspectList inputAspects) {
        for (Aspect aspect : inputAspects.getAspects()) {
            this.aspects.add(aspect, inputAspects.getAmount(aspect));
        }
        this.vis = this.aspects.visSize();
    }

    public void shrinkInput() {
        this.items.get(INPUT_SLOT).shrink(1);
        if (this.items.get(INPUT_SLOT).isEmpty()) {
            this.items.set(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    public int getAspectAmount(Aspect aspect) {
        return this.aspects.getAmount(aspect);
    }

    public void markStorageChanged(Level level, BlockPos pos) {
        this.setChanged();
        level.sendBlockUpdated(pos, this.getBlockState(), this.getBlockState(), 3);
    }

    public static boolean isItemFuel(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    private static int getBurnTime(ItemStack stack) {
        return stack.isEmpty() ? 0 : stack.getBurnTime(RecipeType.SMELTING);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return this.items.get(INPUT_SLOT).isEmpty() && this.items.get(FUEL_SLOT).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize(stack)) {
            stack.setCount(this.getMaxStackSize(stack));
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            return !ObjectAspectRegistry.getObjectTagsWithBonus(stack).isEmpty();
        }
        return slot == FUEL_SLOT && isItemFuel(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction != Direction.UP && this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction != Direction.DOWN || slot != FUEL_SLOT || stack.is(Items.BUCKET);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
}
