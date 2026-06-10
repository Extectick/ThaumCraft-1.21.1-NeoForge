package thaumcraft.common.items.curios;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.curios.TCSlots;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.menus.FocusPouchMenu;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class FocusPouchCurioItem extends ThaumcraftCurioItem {

    public FocusPouchCurioItem(Properties properties) {
        super(TCSlots.BELT, properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            final ItemStack pouchRef = stack;
            final int slot = findSlotInInventory(player, stack);
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("item.thaumcraft.focus_pouch");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player p) {
                    return new FocusPouchMenu(containerId, playerInventory, pouchRef, slot);
                }
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static int findSlotInInventory(Player player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i) == stack) return i;
        }
        return -1;
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
