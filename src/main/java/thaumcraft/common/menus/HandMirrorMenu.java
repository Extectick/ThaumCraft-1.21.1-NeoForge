package thaumcraft.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.HandMirrorItem;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenuTypes;

public class HandMirrorMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOTS = 1;
    private final ItemStack mirror;
    private final Container input;
    private final Player player;

    public HandMirrorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, findLinkedMirror(playerInventory));
    }

    public HandMirrorMenu(int containerId, Inventory playerInventory, ItemStack mirror) {
        super(TCMenuTypes.HAND_MIRROR.get(), containerId);
        this.mirror = mirror;
        this.player = playerInventory.player;
        this.input = new SimpleContainer(INPUT_SLOTS) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return !stack.is(TCItems.HAND_MIRROR.get());
            }
        };

        this.addSlot(new Slot(this.input, 0, 80, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.is(TCItems.HAND_MIRROR.get());
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        ItemStack stack = this.input.getItem(0);
        if (!this.player.level().isClientSide()
                && !stack.isEmpty()
                && !stack.is(TCItems.HAND_MIRROR.get())
                && HandMirrorItem.transport(this.mirror, stack, this.player, this.player.level())) {
            this.input.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            ItemStack stack = this.input.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.mirror.isEmpty() && this.mirror.is(TCItems.HAND_MIRROR.get()) && HandMirrorItem.hasLink(this.mirror);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!this.moveItemStackTo(stack, INPUT_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.is(TCItems.HAND_MIRROR.get()) || !this.moveItemStackTo(stack, 0, INPUT_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    private static ItemStack findLinkedMirror(Inventory inventory) {
        ItemStack main = inventory.player.getMainHandItem();
        if (main.is(TCItems.HAND_MIRROR.get()) && HandMirrorItem.hasLink(main)) {
            return main;
        }
        ItemStack offhand = inventory.player.getOffhandItem();
        if (offhand.is(TCItems.HAND_MIRROR.get()) && HandMirrorItem.hasLink(offhand)) {
            return offhand;
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(TCItems.HAND_MIRROR.get()) && HandMirrorItem.hasLink(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
