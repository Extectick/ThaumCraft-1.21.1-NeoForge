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
import thaumcraft.common.blocks.AlchemicalFurnaceBlock;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.menus.AlchemicalFurnaceMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;

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
        boolean wasBurning = furnace.isBurning();
        int oldVis = furnace.vis;
        boolean changed = false;
        furnace.count++;

        if (furnace.burnTime > 0) {
            furnace.burnTime--;
        }

        if (furnace.count % (furnace.speedBoost ? 20 : 40) == 0 && furnace.aspects.size() > 0) {
            changed |= furnace.transferEssentiaUp(level, pos);
        }

        if (furnace.burnTime == 0 && furnace.canSmelt()) {
            ItemStack fuel = furnace.items.get(FUEL_SLOT);
            furnace.currentBurnTime = furnace.burnTime = getBurnTime(fuel);
            if (furnace.burnTime > 0) {
                changed = true;
                furnace.speedBoost = fuel.is(TCItems.ALUMENTUM.get());
                furnace.consumeFuel();
            }
        }

        if (furnace.isBurning() && furnace.canSmelt()) {
            furnace.cookTime++;
            if (furnace.cookTime >= furnace.smeltTime) {
                furnace.cookTime = 0;
                furnace.smeltItem();
                changed = true;
            }
        } else {
            furnace.cookTime = 0;
        }

        if (wasBurning != furnace.isBurning()) {
            changed = true;
        }

        if (changed) {
            furnace.setChanged();
        }

        if (wasBurning != furnace.isBurning() || (oldVis > 0) != (furnace.vis > 0)) {
            level.setBlock(pos, state.setValue(AlchemicalFurnaceBlock.LIT, furnace.isBurning())
                    .setValue(AlchemicalFurnaceBlock.FILLED, furnace.vis > 0), 3);
        }
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

    private boolean canSmelt() {
        ItemStack input = this.items.get(INPUT_SLOT);
        if (input.isEmpty()) {
            return false;
        }

        AspectList inputAspects = ObjectAspectRegistry.getObjectTagsWithBonus(input);
        if (inputAspects.isEmpty()) {
            return false;
        }

        int aspectAmount = inputAspects.visSize();
        if (aspectAmount > MAX_VIS - this.vis) {
            return false;
        }

        this.smeltTime = Math.max(1, aspectAmount * 10);
        return true;
    }

    private void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }

        AspectList inputAspects = ObjectAspectRegistry.getObjectTagsWithBonus(this.items.get(INPUT_SLOT));
        for (Aspect aspect : inputAspects.getAspects()) {
            this.aspects.add(aspect, inputAspects.getAmount(aspect));
        }
        this.vis = this.aspects.visSize();
        this.items.get(INPUT_SLOT).shrink(1);
        if (this.items.get(INPUT_SLOT).isEmpty()) {
            this.items.set(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    private boolean transferEssentiaUp(Level level, BlockPos pos) {
        boolean changed = false;
        AspectList excluded = new AspectList();

        for (int depth = 1; depth <= 5; depth++) {
            if (!(level.getBlockEntity(pos.above(depth)) instanceof ArcaneAlembicBlockEntity alembic)) {
                break;
            }

            if (!alembic.getEssentia().isEmpty()
                    && alembic.getEssentia().amount() < alembic.getEssentiaCapacity()
                    && this.aspects.getAmount(alembic.getEssentia().aspect()) > 0) {
                Aspect aspect = alembic.getEssentia().aspect();
                this.takeFromContainer(aspect, 1);
                alembic.fillEssentia(aspect, 1, false);
                excluded.merge(aspect, 1);
                changed = true;
            }
        }

        for (int depth = 1; depth <= 5; depth++) {
            if (!(level.getBlockEntity(pos.above(depth)) instanceof ArcaneAlembicBlockEntity alembic)) {
                break;
            }

            if (alembic.getEssentia().isEmpty()) {
                Aspect aspect = null;
                if (alembic.getFilterAspect() == null) {
                    aspect = this.takeRandomAspect(level, excluded);
                } else if (this.takeFromContainer(alembic.getFilterAspect(), 1)) {
                    aspect = alembic.getFilterAspect();
                }

                if (aspect != null) {
                    alembic.fillEssentia(aspect, 1, false);
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            level.sendBlockUpdated(pos, this.getBlockState(), this.getBlockState(), 3);
        }
        return changed;
    }

    private Aspect takeRandomAspect(Level level, AspectList excluded) {
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

    private boolean takeFromContainer(Aspect aspect, int amount) {
        if (this.aspects.getAmount(aspect) < amount) {
            return false;
        }
        this.aspects.remove(aspect, amount);
        this.vis -= amount;
        return true;
    }

    private void consumeFuel() {
        ItemStack fuel = this.items.get(FUEL_SLOT);
        if (fuel.isEmpty()) {
            return;
        }

        ItemStack remainder = fuel.getCraftingRemainingItem();
        fuel.shrink(1);
        this.items.set(FUEL_SLOT, fuel.isEmpty() ? remainder : fuel);
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
