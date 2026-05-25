package thaumcraft.client.hud;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.PlayerNotifications;

public final class ResearchNotificationOverlay {
    private static final int LINE_STEP = 8;
    private static final ResourceLocation THAUMONOMICON = Thaumcraft.id("textures/item/thaumonomicon.png");
    private static final ResourceLocation PARTICLES = Thaumcraft.id("textures/misc/particles.png");

    private ResearchNotificationOverlay() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        long time = PlayerNotifications.nowMillis();
        List<PlayerNotifications.Notification> notifications = PlayerNotifications.getListAndUpdate(time);
        List<PlayerNotifications.AspectNotification> aspects = PlayerNotifications.getAspectListAndUpdate(time);
        if (notifications.isEmpty() && aspects.isEmpty()) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 500.0F);
        renderNotifyHud(guiGraphics, minecraft, notifications, time);
        renderAspectHud(guiGraphics, minecraft, aspects, time);
        guiGraphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    private static void renderNotifyHud(GuiGraphics guiGraphics, Minecraft minecraft,
            List<PlayerNotifications.Notification> notifications, long time) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int entry = 0;
        float shift = -8.0F;

        while (entry < notifications.size() && entry < PlayerNotifications.notificationMax()) {
            PlayerNotifications.Notification notification = notifications.get(entry);
            int textWidth = minecraft.font.width(notification.text());
            int alpha = 255;
            if (entry == notifications.size() - 1 && notification.created() > time) {
                alpha = 255 - (int) ((notification.created() - time)
                        / (float) PlayerNotifications.notificationFadeInMs() * 240.0F);
            }
            if (notification.expire() < time + PlayerNotifications.notificationDelayMs()) {
                alpha = (int) (255.0F - (time + PlayerNotifications.notificationDelayMs() - notification.expire())
                        / (float) PlayerNotifications.notificationDelayMs() * 240.0F);
                shift = -8.0F * (alpha / 255.0F);
            }

            alpha = Mth.clamp(alpha, 0, 255);
            float fade = alpha / 255.0F;
            int x = width - textWidth / 2 - 10;
            float y = height - entry * LINE_STEP + shift;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y, 0.0F);
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            guiGraphics.drawString(minecraft.font, notification.text(), -4, -8,
                    withAlpha(0xFFFFFF, Math.round(fade * 170.0F)), false);
            guiGraphics.pose().popPose();

            if (notification.image() != null) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(width - 9.0F, y - 6.0F, 0.0F);
                guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
                drawTexturedQuad(guiGraphics, notification.image(), 0, 0, 16, 16, 0.0F, 0.0F, 1.0F, 1.0F,
                        notification.color(), alpha / 511.0F);
                guiGraphics.pose().popPose();
            }

            if (entry == notifications.size() - 1 && notification.created() > time) {
                renderEntryFlash(guiGraphics, minecraft, width, y, textWidth, notifications.size(), entry, time,
                        notification.created(), alpha);
            }

            entry++;
        }
    }

    private static void renderEntryFlash(GuiGraphics guiGraphics, Minecraft minecraft, int width, float y,
            int textWidth, int notificationCount, int entry, long time, long created, int alpha) {
        float scale = (created - time) / (float) PlayerNotifications.notificationFadeInMs();
        scale = Mth.clamp(scale, 0.0F, 1.0F);
        int particleX = 16 * ((minecraft.player.tickCount + entry * 3) % 16);
        float x = width - 5.0F - 8.0F * scale - (1.0F - scale) * (1.0F - scale) * (1.0F - scale) * textWidth * 3.0F;
        float flashY = y - 2.0F - 8.0F * scale;

        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, flashY, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.5F - alpha / 511.0F);
        guiGraphics.blit(PARTICLES, 0, 0, particleX, 80, 16, 16, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
        RenderSystem.defaultBlendFunc();
    }

    private static void renderAspectHud(GuiGraphics guiGraphics, Minecraft minecraft,
            List<PlayerNotifications.AspectNotification> notifications, long time) {
        if (notifications.isEmpty()) {
            return;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        float mainAlpha = 0.0F;

        for (PlayerNotifications.AspectNotification notification : notifications) {
            if (notification.created() > time) {
                continue;
            }
            Aspect aspect = notification.aspect();
            ResourceLocation image = Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png");
            int startX = (int) (width * notification.startX());
            int startY = (int) (height * notification.startY());
            int endX = width;
            int endY = -8;
            int bezierX = (int) (width * (0.25F + notification.startX()));
            int bezierY = (int) (height * notification.startY());
            double t = (double) (time - notification.created()) / (notification.expire() - notification.created());
            double x = (1.0D - t) * (1.0D - t) * startX + 2.0D * (1.0D - t) * t * bezierX + t * t * endX;
            double y = (1.0D - t) * (1.0D - t) * startY + 2.0D * (1.0D - t) * t * bezierY + t * t * endY;
            float alpha = 1.0F;
            if (t < 0.3D) {
                alpha = (float) (t / 0.3D);
            } else if (t > 0.66D) {
                alpha = (float) (1.0D - (t - 0.66D) / 0.34D);
            }
            alpha = Mth.clamp(alpha, 0.0F, 1.0F);
            mainAlpha = Math.max(mainAlpha, alpha);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) x, (float) y, 0.0F);
            guiGraphics.pose().scale(0.6F * alpha, 0.6F * alpha, 1.0F);
            drawTexturedQuad(guiGraphics, image, 0, 0, 16, 16, 0.0F, 0.0F, 1.0F, 1.0F, aspect.getColor(),
                    alpha * 0.66F);
            guiGraphics.pose().popPose();
        }

        if (mainAlpha > 0.0F) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(width - 16.0F, 0.0F, 0.0F);
            drawTexturedQuad(guiGraphics, THAUMONOMICON, 0, 0, 16, 16, 0.0F, 0.0F, 1.0F, 1.0F, 0xFFFFFF,
                    mainAlpha);
            guiGraphics.pose().popPose();
        }
    }

    private static void drawTexturedQuad(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width,
            int height, float minU, float minV, float maxU, float maxV, int color, float alpha) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float clampedAlpha = Mth.clamp(alpha, 0.0F, 1.0F);

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix = guiGraphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, x, y, 0.0F).setUv(minU, minV).setColor(red, green, blue, clampedAlpha);
        buffer.addVertex(matrix, x, y + height, 0.0F).setUv(minU, maxV).setColor(red, green, blue, clampedAlpha);
        buffer.addVertex(matrix, x + width, y + height, 0.0F).setUv(maxU, maxV)
                .setColor(red, green, blue, clampedAlpha);
        buffer.addVertex(matrix, x + width, y, 0.0F).setUv(maxU, minV).setColor(red, green, blue, clampedAlpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static int withAlpha(int color, int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (color & 0xFFFFFF);
    }
}
