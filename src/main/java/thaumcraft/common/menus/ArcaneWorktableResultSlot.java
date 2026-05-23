package thaumcraft.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArcaneWorktableResultSlot extends Slot {
    private final ArcaneWorktableMenu menu;

    public ArcaneWorktableResultSlot(ArcaneWorktableMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.menu.canTakeResult();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.menu.onTakeResult(player, stack);
        super.onTake(player, stack);
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
