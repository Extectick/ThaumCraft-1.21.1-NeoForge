package thaumcraft.client.compat.jei;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;

final class JeiAspectRenderer {
    private static final DecimalFormat VIS_FORMAT = new DecimalFormat("#######.##",
            DecimalFormatSymbols.getInstance(java.util.Locale.ROOT));
    private static final int ASPECT_TEXTURE_SIZE = 32;

    private JeiAspectRenderer() {
    }

    static void renderAspect(GuiGraphics guiGraphics, Aspect aspect, int amount, int x, int y, int size) {
        int color = aspect.getColor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, 1.0F);
        ResourceLocation texture = Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png");
        guiGraphics.blit(texture, x, y, size, size, 0.0F, 0.0F, ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE,
                ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderAmount(guiGraphics, String.valueOf(amount), x, y, size);
        RenderSystem.disableBlend();
    }

    static void renderVis(GuiGraphics guiGraphics, Aspect aspect, int centiVis, int x, int y) {
        renderAspect(guiGraphics, aspect, 0, x, y, 14);
        String amount = VIS_FORMAT.format(centiVis / 100.0F);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, amount, x + 17, y + 4, 0xFF404040, false);
    }

    static void renderVisTag(GuiGraphics guiGraphics, Aspect aspect, int centiVis, int x, int y) {
        int color = aspect.getColor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, 1.0F);
        guiGraphics.blit(Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png"), x, y, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderAmount(guiGraphics, VIS_FORMAT.format(centiVis / 100.0F), x, y, 16);
        RenderSystem.disableBlend();
    }

    static Component aspectName(Aspect aspect) {
        return Component.translatable("tc.aspect." + aspect.getTag());
    }

    private static void renderAmount(GuiGraphics guiGraphics, String amount, int x, int y, int size) {
        if (amount == null || amount.isEmpty() || "0".equals(amount)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int scaledX = (x + size) * 2 - font.width(amount);
        int scaledY = (y + size) * 2 - font.lineHeight;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawString(font, amount, scaledX - 1, scaledY, 0xFF000000, false);
        guiGraphics.drawString(font, amount, scaledX + 1, scaledY, 0xFF000000, false);
        guiGraphics.drawString(font, amount, scaledX, scaledY - 1, 0xFF000000, false);
        guiGraphics.drawString(font, amount, scaledX, scaledY + 1, 0xFF000000, false);
        guiGraphics.drawString(font, amount, scaledX, scaledY, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();
    }
}
