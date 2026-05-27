package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.blockentities.WardedJarBlockEntity;

public class WardedJarRenderer implements BlockEntityRenderer<WardedJarBlockEntity> {
    public WardedJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WardedJarBlockEntity jar, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        EssentiaStorage storage = jar.getEssentia();
        if (!storage.isEmpty()) {
            float y1 = 0.0625F;
            float y2 = y1 + 0.625F * storage.amount() / jar.getEssentiaCapacity();
            int color = 0xBB000000 | storage.aspect().getColor();
            int light = Math.max(packedLight, LightTexture.pack(12, 12));
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                    .apply(Thaumcraft.id("block/animatedglow"));

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
            renderCuboid(poseStack, consumer, sprite, 0.25F, y1, 0.25F, 0.75F, y2, 0.75F, light, color);
        }

        Aspect filter = jar.getFilterAspect();
        if (filter != null) {
            renderLabel(poseStack, bufferSource, jar.getFacing(), filter, packedLight);
        }
    }

    private static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing,
            Aspect aspect, int packedLight) {
        int light = Math.max(packedLight, LightTexture.FULL_BRIGHT);
        VertexConsumer label = bufferSource.getBuffer(RenderType.entityTranslucent(Thaumcraft.id("textures/models/label.png")));
        renderSideQuad(poseStack, label, facing, 0.5F, 0.375F, 0.5F, 0.42F, 0.42F, 0.002F, light, 0xFFFFFFFF);

        VertexConsumer icon = bufferSource.getBuffer(RenderType.entityTranslucent(Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png")));
        renderSideQuad(poseStack, icon, facing, 0.5F, 0.375F, 0.5F, 0.18F, 0.18F, 0.004F, light,
                0xFF000000 | aspect.getColor());
    }

    private static void renderSideQuad(PoseStack poseStack, VertexConsumer consumer, Direction facing, float centerX,
            float centerY, float centerZ, float width, float height, float offset, int light, int color) {
        float halfW = width / 2.0F;
        float halfH = height / 2.0F;
        float x1 = centerX - halfW;
        float x2 = centerX + halfW;
        float y1 = centerY - halfH;
        float y2 = centerY + halfH;
        float z1 = centerZ - halfW;
        float z2 = centerZ + halfW;

        switch (facing) {
            case SOUTH -> addTexturedQuad(poseStack, consumer, x1, y1, 0.8125F + offset, x2, y1, 0.8125F + offset,
                    x2, y2, 0.8125F + offset, x1, y2, 0.8125F + offset, 0.0F, 0.0F, 1.0F, light, color);
            case WEST -> addTexturedQuad(poseStack, consumer, 0.1875F - offset, y1, z2, 0.1875F - offset, y1, z1,
                    0.1875F - offset, y2, z1, 0.1875F - offset, y2, z2, -1.0F, 0.0F, 0.0F, light, color);
            case EAST -> addTexturedQuad(poseStack, consumer, 0.8125F + offset, y1, z1, 0.8125F + offset, y1, z2,
                    0.8125F + offset, y2, z2, 0.8125F + offset, y2, z1, 1.0F, 0.0F, 0.0F, light, color);
            case NORTH -> addTexturedQuad(poseStack, consumer, x2, y1, 0.1875F - offset, x1, y1, 0.1875F - offset,
                    x1, y2, 0.1875F - offset, x2, y2, 0.1875F - offset, 0.0F, 0.0F, -1.0F, light, color);
            default -> {
            }
        }
    }

    private static void renderCuboid(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float x1,
            float y1, float z1, float x2, float y2, float z2, int light, int color) {
        addQuad(poseStack, consumer, sprite, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0.0F, 1.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, 0.0F, -1.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, -1.0F, light, color);
        addQuad(poseStack, consumer, sprite, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, -1.0F, 0.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 1.0F, 0.0F, 0.0F, light, color);
    }

    private static void addQuad(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float x1, float y1, float z1,
            float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4,
            float normalX, float normalY, float normalZ, int light, int color) {
        addVertex(poseStack, consumer, x1, y1, z1, sprite.getU0(), sprite.getV0(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x2, y2, z2, sprite.getU1(), sprite.getV0(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x3, y3, z3, sprite.getU1(), sprite.getV1(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x4, y4, z4, sprite.getU0(), sprite.getV1(), normalX, normalY, normalZ, light, color);
    }

    private static void addTexturedQuad(PoseStack poseStack, VertexConsumer consumer, float x1, float y1, float z1,
            float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4,
            float normalX, float normalY, float normalZ, int light, int color) {
        addVertex(poseStack, consumer, x1, y1, z1, 0.0F, 1.0F, normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x2, y2, z2, 1.0F, 1.0F, normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x3, y3, z3, 1.0F, 0.0F, normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x4, y4, z4, 0.0F, 0.0F, normalX, normalY, normalZ, light, color);
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float z,
            float u, float v, float normalX, float normalY, float normalZ, int light, int color) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalX, normalY, normalZ);
    }
}
