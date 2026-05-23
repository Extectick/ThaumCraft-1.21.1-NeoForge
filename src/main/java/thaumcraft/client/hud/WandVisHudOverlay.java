package thaumcraft.client.hud;

import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.registry.TCItems;

public final class WandVisHudOverlay {
    private static final ResourceLocation HUD = Thaumcraft.id("textures/gui/hud.png");
    private static final int BAR_HEIGHT = 30;

    private WandVisHudOverlay() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui || minecraft.screen != null) {
            return;
        }

        ItemStack wand = getHeldWand(player);
        if (wand.isEmpty()) {
            return;
        }

        boolean dialBottom = ThaumcraftConfig.WAND_DIAL_BOTTOM.get();
        int top = dialBottom ? guiGraphics.guiHeight() - 32 : 0;
        renderDial(guiGraphics, wand, 16, top + 16, player.isShiftKeyDown(), dialBottom);
    }

    private static ItemStack getHeldWand(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(TCItems.WAND_CASTING.get())) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        return offHand.is(TCItems.WAND_CASTING.get()) ? offHand : ItemStack.EMPTY;
    }

    private static void renderDial(GuiGraphics guiGraphics, ItemStack wand, int centerX, int centerY,
            boolean showNumbers, boolean dialBottom) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX - 16.0F, centerY - 16.0F, 0.0F);
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.blit(HUD, 0, 0, 0, 0, 64, 64);
        guiGraphics.pose().popPose();

        int max = Math.max(1, WandVisHelper.getMaxVis(wand));
        ItemStack focusStack = WandFocusHelper.getFocusItem(wand);
        ItemFocusBasic focus = WandFocusHelper.getFocus(wand);
        PrimalVisStorage focusCost = focus != null ? focus.getVisCost(focusStack) : PrimalVisStorage.EMPTY;
        int count = 0;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int amount = WandVisHelper.getVis(wand, aspect);
            renderBar(guiGraphics, aspect, amount, max, centerX, centerY, -15 + count * 24, focusCost.get(aspect),
                    showNumbers, dialBottom);
            count++;
        }
        if (!focusStack.isEmpty()) {
            guiGraphics.renderItem(focusStack, centerX - 8, centerY - 8);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderBar(GuiGraphics guiGraphics, Aspect aspect, int amount, int max, int centerX, int centerY,
            float angle, int focusCost, boolean showNumbers, boolean dialBottom) {
        int fill = Mth.clamp(Math.round(BAR_HEIGHT * amount / (float) max), 0, BAR_HEIGHT);
        float red = ((aspect.getColor() >> 16) & 0xFF) / 255.0F;
        float green = ((aspect.getColor() >> 8) & 0xFF) / 255.0F;
        float blue = (aspect.getColor() & 0xFF) / 255.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        if (!dialBottom) {
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
        guiGraphics.pose().translate(0.0F, -32.0F, 0.0F);
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);

        if (fill > 0) {
            guiGraphics.setColor(red, green, blue, 0.8F);
            guiGraphics.blit(HUD, -4, 35 - fill, 104, 0, 8, fill);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        guiGraphics.blit(HUD, -8, -3, 72, 0, 16, 42);
        if (focusCost > 0) {
            guiGraphics.blit(HUD, -4, -8, 136, 0, 8, 8);
        }
        if (showNumbers) {
            renderNumber(guiGraphics, amount);
        }
        guiGraphics.pose().popPose();
    }

    private static void renderNumber(GuiGraphics guiGraphics, int amount) {
        Minecraft minecraft = Minecraft.getInstance();
        String value = Integer.toString(amount / 100);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0F));
        guiGraphics.drawString(minecraft.font, value, -32, -4, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
    }
}
