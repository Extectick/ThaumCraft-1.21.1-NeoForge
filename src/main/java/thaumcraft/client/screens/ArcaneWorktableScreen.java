package thaumcraft.client.screens;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.lib.crafting.ArcaneWorktableRecipes;
import thaumcraft.common.menus.ArcaneWorktableMenu;

public class ArcaneWorktableScreen extends AbstractContainerScreen<ArcaneWorktableMenu> {
    private static final ResourceLocation BACKGROUND = Thaumcraft.id("textures/gui/gui_arcaneworkbench.png");
    private static final ResourceLocation ASPECT_BACK = Thaumcraft.id("textures/aspects/_back.png");
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
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderAspectCosts(guiGraphics);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.menu.isArcaneResultBlockedByVis()) {
            guiGraphics.fill(this.leftPos + 160, this.topPos + 64, this.leftPos + 176, this.topPos + 80, 0xAA333333);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
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
            int cost = recipe.getVisCost().get(aspect);
            if (cost > 0) {
                boolean hasEnough = ArcaneWorktableRecipes.hasPrimalCost(wand, recipe);
                float pulse = Mth.sin((Minecraft.getInstance().gui.getGuiTicks() + count * 10) / 2.0F) * 0.2F - 0.2F;
                float alpha = hasEnough || WandVisHelper.hasEnoughVis(wand, aspect, cost) ? 1.0F : 0.5F + pulse;
                this.renderAspectTag(guiGraphics, aspect, cost, this.leftPos + ASPECT_LOCS[count][0] - 8,
                        this.topPos + ASPECT_LOCS[count][1] - 8, alpha);
            }
            count++;
        }

        if (!ArcaneWorktableRecipes.hasPrimalCost(wand, recipe)) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            String text = Component.translatable("container.thaumcraft.arcane_worktable.insufficient_vis").getString();
            int x = (this.leftPos + 168) * 2 - this.font.width(text) / 2;
            int y = (this.topPos + 46) * 2;
            guiGraphics.drawString(this.font, text, x, y, 0xEE6DEE, false);
            guiGraphics.pose().popPose();
        }
    }

    private void renderAspectTag(GuiGraphics guiGraphics, Aspect aspect, int centiVis, int x, int y, float alpha) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(ASPECT_BACK, x, y, 0, 0, 16, 16, 16, 16);
        guiGraphics.blit(ASPECT_TEXTURES.get(aspect), x, y, 0, 0, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        String amount = formatVis(centiVis);
        guiGraphics.drawString(this.font, amount, x + 8 - this.font.width(amount) / 2, y + 17, 0xFFFFFF, true);
    }

    private static String formatVis(int centiVis) {
        if (centiVis % 100 == 0) {
            return Integer.toString(centiVis / 100);
        }
        return String.format(java.util.Locale.ROOT, "%.2f", centiVis / 100.0F);
    }

    private static ResourceLocation aspectTexture(String name) {
        return Thaumcraft.id("textures/aspects/" + name + ".png");
    }
}
