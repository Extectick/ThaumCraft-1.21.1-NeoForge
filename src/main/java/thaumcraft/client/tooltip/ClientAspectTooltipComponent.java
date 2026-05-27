package thaumcraft.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public class ClientAspectTooltipComponent implements ClientTooltipComponent {
    private static final int ICON_SIZE = 16;
    private static final int BACK_SIZE = 20;
    private static final int ASPECT_TEXTURE_SIZE = 32;
    private static final int BACK_TEXTURE_SIZE = 64;
    private static final float BACK_ALPHA = 0.35F;
    private static final int ENTRY_SIZE = 18;
    private static final int ROW_HEIGHT = 20;
    private static final int MAX_PER_ROW = 8;

    private final AspectTooltipComponent component;

    public ClientAspectTooltipComponent(AspectTooltipComponent component) {
        this.component = component;
    }

    @Override
    public int getHeight() {
        return Math.max(1, rows()) * ROW_HEIGHT - 2;
    }

    @Override
    public int getWidth(Font font) {
        return Math.min(this.component.entries().size(), MAX_PER_ROW) * ENTRY_SIZE + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int index = 0; index < this.component.entries().size(); index++) {
            AspectTooltipComponent.Entry entry = this.component.entries().get(index);
            int row = index / MAX_PER_ROW;
            int column = index % MAX_PER_ROW;
            int entryX = x + column * ENTRY_SIZE;
            int entryY = y + row * ROW_HEIGHT;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, BACK_ALPHA);
            guiGraphics.blit(Thaumcraft.id("textures/aspects/_back.png"), entryX - 2, entryY - 2,
                    BACK_SIZE, BACK_SIZE, 0.0F, 0.0F, BACK_TEXTURE_SIZE, BACK_TEXTURE_SIZE,
                    BACK_TEXTURE_SIZE, BACK_TEXTURE_SIZE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int color = entry.aspect().getColor();
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            ResourceLocation texture = Thaumcraft.id("textures/aspects/" + entry.aspect().getTag() + ".png");
            RenderSystem.setShaderColor(red, green, blue, 1.0F);
            guiGraphics.blit(texture, entryX, entryY, ICON_SIZE, ICON_SIZE, 0.0F, 0.0F,
                    ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderAmount(font, guiGraphics, entry.amount(), entryX, entryY);
        }
    }

    private int rows() {
        return Math.max(1, (this.component.entries().size() + MAX_PER_ROW - 1) / MAX_PER_ROW);
    }

    private static void renderAmount(Font font, GuiGraphics guiGraphics, int amount, int x, int y) {
        if (amount <= 0) {
            return;
        }
        String text = String.valueOf(amount);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        int textX = (x + ICON_SIZE) * 2 - font.width(text);
        int textY = (y + ICON_SIZE) * 2 - font.lineHeight;
        guiGraphics.drawString(font, text, textX - 1, textY, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX + 1, textY, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY - 1, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY + 1, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();
    }
}
