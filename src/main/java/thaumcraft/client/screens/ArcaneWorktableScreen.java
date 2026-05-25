package thaumcraft.client.screens;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.lib.crafting.ArcaneWorktableRecipes;
import thaumcraft.common.menus.ArcaneWorktableMenu;

public class ArcaneWorktableScreen extends AbstractContainerScreen<ArcaneWorktableMenu> {
    private static final ResourceLocation BACKGROUND = Thaumcraft.id("textures/gui/gui_arcaneworkbench.png");
    private static final DecimalFormat VIS_FORMAT = new DecimalFormat("#######.##",
            DecimalFormatSymbols.getInstance(java.util.Locale.ROOT));
    private static final Map<Aspect, ResourceLocation> ASPECT_TEXTURES = Map.of(
            Aspect.AIR, aspectTexture("aer"),
            Aspect.FIRE, aspectTexture("ignis"),
            Aspect.WATER, aspectTexture("aqua"),
            Aspect.EARTH, aspectTexture("terra"),
            Aspect.ORDER, aspectTexture("ordo"),
            Aspect.ENTROPY, aspectTexture("perditio"));
    private static final int[][] ASPECT_LOCS = {
            { 72, 21 },
            { 24, 43 },
            { 24, 102 },
            { 72, 124 },
            { 120, 102 },
            { 120, 43 }
    };

    public ArcaneWorktableScreen(ArcaneWorktableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 190;
        this.imageHeight = 234;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
        this.renderAspectCosts(guiGraphics);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (slot == this.menu.slots.get(ArcaneWorktableMenu.RESULT_SLOT) && this.menu.isArcaneResultBlockedByVis()) {
            this.renderBlockedResultSlot(guiGraphics, slot);
            return;
        }
        super.renderSlot(guiGraphics, slot);
    }

    private void renderAspectCosts(GuiGraphics guiGraphics) {
        ArcaneWorktableRecipe recipe = this.menu.getSelectedRecipeForDisplay().map(holder -> holder.value())
                .orElse(null);
        if (recipe == null) {
            return;
        }

        ItemStack wand = this.menu.getWandStack();
        int count = 0;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int cost = ArcaneWorktableRecipes.effectivePrimalCost(wand, this.minecraft.player, recipe, aspect);
            if (cost > 0) {
                boolean hasEnough = ArcaneWorktableRecipes.hasPrimalCost(wand, this.minecraft.player, recipe);
                float pulse = Mth.sin((Minecraft.getInstance().gui.getGuiTicks() + count * 10) / 2.0F) * 0.2F - 0.2F;
                float alpha = hasEnough || WandVisHelper.hasEnoughVis(wand, aspect, cost) ? 1.0F : 0.5F + pulse;
                this.renderAspectTag(guiGraphics, aspect, cost, this.leftPos + ASPECT_LOCS[count][0] - 8,
                        this.topPos + ASPECT_LOCS[count][1] - 8, alpha);
            }
            count++;
        }

        if (!ArcaneWorktableRecipes.hasPrimalCost(wand, this.minecraft.player, recipe)) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            String text = "Insufficient vis";
            int x = (this.leftPos + 168) * 2 - this.font.width(text) / 2;
            int y = (this.topPos + 46) * 2;
            guiGraphics.drawString(this.font, text, x, y, 0xEE6DEE, false);
            guiGraphics.pose().popPose();
        }
    }

    private void renderAspectTag(GuiGraphics guiGraphics, Aspect aspect, int centiVis, int x, int y, float alpha) {
        int color = aspect.getColor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, alpha);
        guiGraphics.blit(ASPECT_TEXTURES.get(aspect), x, y, 0, 0, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        this.renderAspectAmount(guiGraphics, formatVis(centiVis), x, y);
    }

    private void renderAspectAmount(GuiGraphics guiGraphics, String amount, int x, int y) {
        int scaledX = x * 2 + 32 - this.font.width(amount);
        int scaledY = y * 2 + 32 - this.font.lineHeight;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawString(this.font, amount, scaledX - 1, scaledY, 0x000000, false);
        guiGraphics.drawString(this.font, amount, scaledX + 1, scaledY, 0x000000, false);
        guiGraphics.drawString(this.font, amount, scaledX, scaledY - 1, 0x000000, false);
        guiGraphics.drawString(this.font, amount, scaledX, scaledY + 1, 0x000000, false);
        guiGraphics.drawString(this.font, amount, scaledX, scaledY, 0xFFFFFF, false);
        guiGraphics.pose().popPose();
    }

    private void renderBlockedResultSlot(GuiGraphics guiGraphics, Slot slot) {
        ItemStack result = slot.getItem();
        if (result.isEmpty()) {
            return;
        }
        float alpha = 0.5F + (Mth.sin(Minecraft.getInstance().gui.getGuiTicks() / 2.0F) * 0.2F - 0.2F);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(0.33F, 0.33F, 0.33F, alpha);
        guiGraphics.renderFakeItem(result, slot.x, slot.y);
        guiGraphics.renderItemDecorations(this.font, result, slot.x, slot.y);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    private static String formatVis(int centiVis) {
        return VIS_FORMAT.format(centiVis / 100.0F);
    }

    private static ResourceLocation aspectTexture(String name) {
        return Thaumcraft.id("textures/aspects/" + name + ".png");
    }
}
