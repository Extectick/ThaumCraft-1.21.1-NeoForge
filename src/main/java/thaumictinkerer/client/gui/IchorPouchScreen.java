package thaumictinkerer.client.gui;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.menus.IchorPouchMenu;

public class IchorPouchScreen extends AbstractContainerScreen<IchorPouchMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "textures/gui/ichor_focus_pouch.png");

    public IchorPouchScreen(IchorPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}

