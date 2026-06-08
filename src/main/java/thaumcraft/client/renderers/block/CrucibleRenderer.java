package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import thaumcraft.common.blockentities.CrucibleBlockEntity;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrucibleBlockEntity crucible, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (crucible.getWaterAmount() <= 0) {
            return;
        }

        float fill = Math.min(1.0F, crucible.tagAmount() / (float) CrucibleBlockEntity.MAX_TAGS);
        int waterColor = BiomeColors.getAverageWaterColor(crucible.getLevel(), crucible.getBlockPos());
        int red = Math.round(255.0F * lerp(((waterColor >> 16) & 0xFF) / 255.0F, 0.72F, fill));
        int green = Math.round(255.0F * lerp(((waterColor >> 8) & 0xFF) / 255.0F, 0.18F, fill));
        int blue = Math.round(255.0F * lerp((waterColor & 0xFF) / 255.0F, 0.95F, fill));
        int alpha = Math.round(255.0F * lerp(1.0F, 0.55F, fill));
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still"));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        float y = (float) crucible.getFluidHeight();
        renderTopQuad(poseStack, consumer, sprite, y, packedLight, color);
    }

    private static void renderTopQuad(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float y,
            int light, int color) {
        float min = 0.125F;
        float max = 0.875F;
        addVertex(poseStack, consumer, min, y, min, sprite.getU0(), sprite.getV0(), light, color);
        addVertex(poseStack, consumer, max, y, min, sprite.getU1(), sprite.getV0(), light, color);
        addVertex(poseStack, consumer, max, y, max, sprite.getU1(), sprite.getV1(), light, color);
        addVertex(poseStack, consumer, min, y, max, sprite.getU0(), sprite.getV1(), light, color);
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float z,
            float u, float v, int light, int color) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static float lerp(float from, float to, float amount) {
        return from + (to - from) * amount;
    }

}
