package thaumcraft.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.client.input.TCKeyMappings;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public final class FocusSelectorOverlay {
    private static final ResourceLocation FOCUS_POUCH = Thaumcraft.id("textures/gui/gui_focuspouch.png");
    private static final int WIDTH = 175;
    private static final int HEIGHT = 232;
    private static final int FOCUS_COLUMNS = 6;
    private static final int FOCUS_SLOT_X = 37;
    private static final int FOCUS_SLOT_Y = 51;
    private static final int SLOT_SIZE = 18;

    private FocusSelectorOverlay() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui || minecraft.screen != null || !TCKeyMappings.CHANGE_FOCUS.isDown()) {
            return;
        }

        ItemStack wand = getHeldWand(player);
        if (wand.isEmpty()) {
            return;
        }

        ThaumcraftCuriosCompat.findFocusPouch(player)
                .map(slot -> slot.stack())
                .ifPresent(pouch -> renderSelector(guiGraphics, minecraft, wand, pouch));
    }

    private static ItemStack getHeldWand(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(TCItems.WAND_CASTING.get())) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        return offHand.is(TCItems.WAND_CASTING.get()) ? offHand : ItemStack.EMPTY;
    }

    private static void renderSelector(GuiGraphics guiGraphics, Minecraft minecraft, ItemStack wand, ItemStack pouch) {
        int left = (guiGraphics.guiWidth() - WIDTH) / 2;
        int top = (guiGraphics.guiHeight() - HEIGHT) / 2;
        FocusPouchContents contents = pouch.getOrDefault(TCDataComponents.FOCUS_POUCH_CONTENTS, FocusPouchContents.EMPTY);
        ItemStack currentFocus = WandFocusHelper.getFocusItem(wand);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        guiGraphics.blit(FOCUS_POUCH, left, top, 0, 0, WIDTH, HEIGHT);

        for (int slot = 0; slot < FocusPouchContents.SIZE; slot++) {
            int slotX = left + FOCUS_SLOT_X + slot % FOCUS_COLUMNS * SLOT_SIZE;
            int slotY = top + FOCUS_SLOT_Y + slot / FOCUS_COLUMNS * SLOT_SIZE;
            ItemStack focus = contents.get(slot);
            if (!focus.isEmpty()) {
                if (!currentFocus.isEmpty() && ItemStack.isSameItemSameComponents(focus, currentFocus)) {
                    guiGraphics.blit(FOCUS_POUCH, slotX, slotY, 240, 0, 16, 16);
                }
                guiGraphics.renderItem(focus, slotX, slotY);
            }
        }

        if (!currentFocus.isEmpty()) {
            int centerX = left + WIDTH / 2;
            int centerY = top + 24;
            guiGraphics.fill(centerX - 11, centerY - 11, centerX + 11, centerY + 11, 0x88000000);
            guiGraphics.renderItem(currentFocus, centerX - 8, centerY - 8);
            Component name = currentFocus.getHoverName();
            int textWidth = minecraft.font.width(name);
            guiGraphics.drawString(minecraft.font, name, centerX - textWidth / 2, centerY + 14, 0xFFFFFF, true);
        }
        guiGraphics.pose().popPose();
    }
}
