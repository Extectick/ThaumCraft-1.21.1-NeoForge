package thaumcraft.client.hud;

import com.mojang.math.Axis;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
    private static final int DIAL_SIZE = 64;
    private static final int BAR_OFFSET = 32;
    private static final float HUD_SCALE = 0.5F;
    private static final float DIAL_ALPHA = 1.0F;
    private static final float TUBE_FRAME_ALPHA = 0.82F;

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
        int centerY = dialBottom ? guiGraphics.guiHeight() - 16 : 16;
        renderDial(guiGraphics, wand, 16, centerY, Screen.hasShiftDown(), dialBottom);
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
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX - DIAL_SIZE * HUD_SCALE / 2.0F,
                centerY - DIAL_SIZE * HUD_SCALE / 2.0F, 0.0F);
        guiGraphics.pose().scale(HUD_SCALE, HUD_SCALE, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, DIAL_ALPHA);
        guiGraphics.blit(HUD, 0, 0, 0, 0, DIAL_SIZE, DIAL_SIZE);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX - 8.0F, centerY - 8.0F, 0.0F);
            guiGraphics.renderItem(focusStack, 0, 0);
            guiGraphics.pose().popPose();
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
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
        guiGraphics.pose().translate(0.0F, -BAR_OFFSET, 0.0F);
        guiGraphics.pose().scale(HUD_SCALE, HUD_SCALE, 1.0F);

        if (fill > 0) {
            guiGraphics.setColor(red, green, blue, 0.8F);
            guiGraphics.blit(HUD, -4, 35 - fill, 104, 0, 8, fill);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, TUBE_FRAME_ALPHA);
        guiGraphics.blit(HUD, -8, -3, 72, 0, 16, 42);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
