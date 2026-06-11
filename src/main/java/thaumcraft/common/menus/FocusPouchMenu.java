package thaumcraft.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCMenuTypes;

import java.util.ArrayList;
import java.util.List;

public class FocusPouchMenu extends AbstractContainerMenu {

    public static final int POUCH_SLOTS = 18;
    public static final int POUCH_COLS  = 6;

    private final ItemStack pouch;
    private final int       pouchInventorySlot;
    private final Container focusContainer;

    public FocusPouchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,
                findPouch(playerInventory),
                findPouchSlot(playerInventory));
    }

    public FocusPouchMenu(int containerId, Inventory playerInventory,
                          ItemStack pouch, int pouchInventorySlot) {
        super(TCMenuTypes.FOCUS_POUCH.get(), containerId);
        this.pouch              = pouch;
        this.pouchInventorySlot = pouchInventorySlot;

        this.focusContainer = new SimpleContainer(POUCH_SLOTS) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return WandFocusHelper.isFocus(stack);
            }
        };

        loadPouchIntoContainer();

        for (int i = 0; i < POUCH_SLOTS; i++) {
            this.addSlot(new Slot(focusContainer, i,
                    37 + (i % POUCH_COLS) * 18,
                    51 + (i / POUCH_COLS) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return WandFocusHelper.isFocus(stack);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18, 151 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 209));
        }
    }

    private void loadPouchIntoContainer() {
        FocusPouchContents contents = pouch.getOrDefault(
                TCDataComponents.FOCUS_POUCH_CONTENTS,
                FocusPouchContents.EMPTY);
        for (int i = 0; i < POUCH_SLOTS; i++) {
            focusContainer.setItem(i, contents.get(i));
        }
    }

    private void saveContainerToPouch() {
        List<ItemStack> stacks = new ArrayList<>(POUCH_SLOTS);
        for (int i = 0; i < POUCH_SLOTS; i++) {
            stacks.add(focusContainer.getItem(i).copy());
        }
        pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS,
                new FocusPouchContents(stacks));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            saveContainerToPouch();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (pouchInventorySlot >= 0
                && pouchInventorySlot < player.getInventory().getContainerSize()) {
            return ItemStack.isSameItem(
                    player.getInventory().getItem(pouchInventorySlot), pouch);
        }
        return thaumcraft.common.curios.ThaumcraftCuriosCompat
                .findFocusPouch(player).isPresent();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy  = stack.copy();
        final int hotbarStart = POUCH_SLOTS + 27;

        if (index < POUCH_SLOTS) {
            if (!this.moveItemStackTo(stack, POUCH_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (WandFocusHelper.isFocus(stack)) {
                if (!this.moveItemStackTo(stack, 0, POUCH_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < hotbarStart) {
                if (!this.moveItemStackTo(stack, hotbarStart, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, POUCH_SLOTS, hotbarStart, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    public ItemStack getPouch() { return pouch; }

    private static ItemStack findPouch(Inventory inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof FocusPouchCurioItem) {
                return inv.getItem(i);
            }
        }
        return thaumcraft.common.curios.ThaumcraftCuriosCompat
                .findFocusPouch(inv.player)
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private static int findPouchSlot(Inventory inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof FocusPouchCurioItem) return i;
        }
        return -1;
    }
}
