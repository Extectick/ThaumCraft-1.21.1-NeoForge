package thaumcraft.common.items.wands;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.registry.TCDataComponents;

public final class WandFocusHelper {
    private WandFocusHelper() {
    }

    public static boolean isFocus(ItemStack stack) {
        return stack.getItem() instanceof ItemFocusBasic;
    }

    public static ItemStack getFocusItem(ItemStack wand) {
        ItemStack focus = wand.getOrDefault(TCDataComponents.WAND_FOCUS, ItemStack.EMPTY);
        return focus.isEmpty() ? ItemStack.EMPTY : focus.copy();
    }

    @Nullable
    public static ItemFocusBasic getFocus(ItemStack wand) {
        ItemStack focus = getFocusItem(wand);
        return focus.getItem() instanceof ItemFocusBasic itemFocus ? itemFocus : null;
    }

    public static boolean hasFocus(ItemStack wand) {
        return getFocus(wand) != null;
    }

    public static void setFocus(ItemStack wand, ItemStack focus) {
        if (focus.isEmpty()) {
            wand.remove(TCDataComponents.WAND_FOCUS);
            return;
        }

        if (!isFocus(focus)) {
            throw new IllegalArgumentException("ItemStack is not a wand focus: " + focus);
        }

        ItemStack stored = focus.copy();
        stored.setCount(1);
        wand.set(TCDataComponents.WAND_FOCUS, stored);
    }

    public static ItemStack removeFocus(ItemStack wand) {
        ItemStack focus = getFocusItem(wand);
        wand.remove(TCDataComponents.WAND_FOCUS);
        return focus;
    }
}
