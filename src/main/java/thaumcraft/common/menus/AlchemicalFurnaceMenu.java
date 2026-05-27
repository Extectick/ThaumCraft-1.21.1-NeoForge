package thaumcraft.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.registry.TCMenuTypes;

public class AlchemicalFurnaceMenu extends AbstractContainerMenu {
    public static final int INPUT_MENU_SLOT = 0;
    public static final int FUEL_MENU_SLOT = 1;
    public static final int PLAYER_INV_SLOT_START = 2;
    public static final int PLAYER_INV_SLOT_END = 29;
    public static final int HOTBAR_SLOT_START = 29;
    public static final int HOTBAR_SLOT_END = 38;

    private static final int DATA_COOK_TIME = 0;
    private static final int DATA_BURN_TIME = 1;
    private static final int DATA_CURRENT_BURN_TIME = 2;
    private static final int DATA_VIS = 3;
    private static final int DATA_SMELT_TIME = 4;

    private final Container furnace;
    private final ContainerData data;

    public AlchemicalFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(AlchemicalFurnaceBlockEntity.CONTAINER_SIZE),
                new SimpleContainerData(5));
    }

    public AlchemicalFurnaceMenu(int containerId, Inventory playerInventory, Container furnace, ContainerData data) {
        super(TCMenuTypes.ALCHEMICAL_FURNACE.get(), containerId);
        checkContainerSize(furnace, AlchemicalFurnaceBlockEntity.CONTAINER_SIZE);
        checkContainerDataCount(data, 5);
        this.furnace = furnace;
        this.data = data;
        furnace.startOpen(playerInventory.player);
        this.addDataSlots(data);

        this.addSlot(new Slot(furnace, AlchemicalFurnaceBlockEntity.INPUT_SLOT, 80, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !ObjectAspectRegistry.getObjectTagsWithBonus(stack).isEmpty();
            }
        });
        this.addSlot(new Slot(furnace, AlchemicalFurnaceBlockEntity.FUEL_SLOT, 80, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return AlchemicalFurnaceBlockEntity.isItemFuel(stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.furnace.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            moved = stack.copy();

            if (index == INPUT_MENU_SLOT || index == FUEL_MENU_SLOT) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_SLOT_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (AlchemicalFurnaceBlockEntity.isItemFuel(stack)) {
                if (!this.moveItemStackTo(stack, FUEL_MENU_SLOT, FUEL_MENU_SLOT + 1, false)
                        && !this.moveItemStackTo(stack, INPUT_MENU_SLOT, INPUT_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!ObjectAspectRegistry.getObjectTagsWithBonus(stack).isEmpty()) {
                if (!this.moveItemStackTo(stack, INPUT_MENU_SLOT, INPUT_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= PLAYER_INV_SLOT_START && index < PLAYER_INV_SLOT_END) {
                if (!this.moveItemStackTo(stack, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= HOTBAR_SLOT_START && index < HOTBAR_SLOT_END
                    && !this.moveItemStackTo(stack, PLAYER_INV_SLOT_START, PLAYER_INV_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == moved.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return moved;
    }

    public boolean isBurning() {
        return this.data.get(DATA_BURN_TIME) > 0;
    }

    public int getBurnProgress() {
        int currentBurnTime = this.data.get(DATA_CURRENT_BURN_TIME);
        if (currentBurnTime == 0) {
            currentBurnTime = 200;
        }
        return this.data.get(DATA_BURN_TIME) * 20 / currentBurnTime;
    }

    public int getCookProgress() {
        int smeltTime = this.data.get(DATA_SMELT_TIME);
        if (smeltTime <= 0) {
            smeltTime = 1;
        }
        return this.data.get(DATA_COOK_TIME) * 46 / smeltTime;
    }

    public int getContentsProgress() {
        return this.data.get(DATA_VIS) * 48 / AlchemicalFurnaceBlockEntity.MAX_VIS;
    }
}
