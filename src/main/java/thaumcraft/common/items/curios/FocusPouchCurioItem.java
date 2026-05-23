package thaumcraft.common.items.curios;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.common.curios.TCSlots;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class FocusPouchCurioItem extends ThaumcraftCurioItem {
    public FocusPouchCurioItem(Properties properties) {
        super(TCSlots.BELT, properties);
    }

    public FocusPouchContents getContents(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.FOCUS_POUCH_CONTENTS, FocusPouchContents.EMPTY);
    }

    public boolean addFocus(ItemStack pouch, ItemStack focus) {
        return this.getContents(pouch).addFocus(focus).map(contents -> {
            pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS, contents);
            return true;
        }).orElse(false);
    }

    public static boolean addFocusToEquipped(Player player, ItemStack focus) {
        return ThaumcraftCuriosCompat.findFocusPouch(player)
                .map(slot -> slot.stack())
                .filter(stack -> stack.is(TCItems.FOCUS_POUCH.get()))
                .map(stack -> ((FocusPouchCurioItem) stack.getItem()).addFocus(stack, focus))
                .orElse(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.thaumcraft.focus_pouch.contents",
                this.getContents(stack).focusCount(), FocusPouchContents.SIZE).withStyle(ChatFormatting.GRAY));
    }
}
