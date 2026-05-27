package thaumcraft.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menus.AlchemicalFurnaceMenu;

public class AlchemicalFurnaceScreen extends AbstractContainerScreen<AlchemicalFurnaceMenu> {
    private static final ResourceLocation BACKGROUND = Thaumcraft.id("textures/gui/gui_alchemyfurnace.png");

    public AlchemicalFurnaceScreen(AlchemicalFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isBurning()) {
            int burn = this.menu.getBurnProgress();
            guiGraphics.blit(BACKGROUND, this.leftPos + 80, this.topPos + 26 + 20 - burn, 176, 20 - burn, 16, burn);
        }

        int cook = this.menu.getCookProgress();
        guiGraphics.blit(BACKGROUND, this.leftPos + 106, this.topPos + 13 + 46 - cook, 216, 46 - cook, 9, cook);

        int contents = this.menu.getContentsProgress();
        guiGraphics.blit(BACKGROUND, this.leftPos + 61, this.topPos + 12 + 48 - contents, 200, 48 - contents, 8,
                contents);
        guiGraphics.blit(BACKGROUND, this.leftPos + 60, this.topPos + 8, 232, 0, 10, 55);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
