package thaumictinkerer.common.menus;


import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumictinkerer.common.registry.TTMenuTypes;

public class IchorPouchMenu extends AbstractContainerMenu {
    private final ItemStack pouchStack;
    private final int pouchSlotIndex;
    private final ComponentItemHandler itemHandler;

    public static final int POUCH_SIZE = 13 * 9;

    public IchorPouchMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, ItemStack.STREAM_CODEC.decode(buf), buf.readInt());
    }

    public IchorPouchMenu(int id, Inventory playerInventory, ItemStack pouchStack, int pouchSlotIndex) {
        super(TTMenuTypes.ICHOR_POUCH.get(), id);
        this.pouchStack = pouchStack;
        this.pouchSlotIndex = pouchSlotIndex;

        if (!pouchStack.has(DataComponents.CONTAINER)) {
            pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }

        this.itemHandler = new ComponentItemHandler(pouchStack, DataComponents.CONTAINER, POUCH_SIZE) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.isEmpty() || stack.getItem() instanceof ItemFocusBasic || stack.getTags().anyMatch(t -> t.location().getPath().contains("focus"));
            }
        };

        int startX = 11;
        int startY = 11;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 13; col++) {
                this.addSlot(new SlotItemHandler(this.itemHandler, col + row * 13, startX + col * 18, startY + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return super.mayPlace(stack) && (stack.isEmpty() || stack.getItem() instanceof ItemFocusBasic || stack.getTags().anyMatch(t -> t.location().getPath().contains("focus")));
                    }
                });
            }
        }

        int playerInvY = startY + 9 * 18 + 14;
        int playerInvX = startX + 2 * 18;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, playerInvX + col * 18, playerInvY + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < POUCH_SIZE) {
                if (!this.moveItemStackTo(itemstack1, POUCH_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() instanceof ItemFocusBasic) {
                if (!this.moveItemStackTo(itemstack1, 0, POUCH_SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.pouchSlotIndex >= 0 && this.pouchSlotIndex < player.getInventory().items.size()) {
            return player.getInventory().getItem(this.pouchSlotIndex) == this.pouchStack;
        }
        return player.getMainHandItem() == this.pouchStack || player.getOffhandItem() == this.pouchStack;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.getItem() == this.pouchStack || (clickType == ClickType.SWAP && button == this.pouchSlotIndex)) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }
}

