package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;

public class RunicMatrixRenderer implements BlockEntityRenderer<RunicMatrixBlockEntity> {
    private static final ResourceLocation INFUSER_SPRITE = Thaumcraft.id("block/infuser_block");
    private static final float CUBE_SIZE = 0.45F;
    private static final float CUBE_OFFSET = 0.25F;

    public RunicMatrixRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RunicMatrixBlockEntity matrix, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float ticks = renderTicks(partialTick);
        float startUp = matrix.isActive() ? 1.0F : 0.0F;
        float instability = Math.min(6.0F,
                1.0F + matrix.getInstability() * 0.66F * (Math.min(matrix.getCraftCount(), 50) / 50.0F));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees((ticks % 360.0F) * startUp));
        poseStack.mulPose(Axis.XP.rotationDegrees(35.0F * startUp));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * startUp));

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(INFUSER_SPRITE);
        VertexConsumer base = sprite.wrap(bufferSource.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)));
        VertexConsumer overlay = sprite.wrap(bufferSource.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS)));

        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    renderSubCube(matrix, poseStack, base, packedLight, packedOverlay, ticks, instability, startUp,
                            a, b, c, false);
                }
            }
        }

        if (matrix.isActive()) {
            for (int a = 0; a < 2; a++) {
                for (int b = 0; b < 2; b++) {
                    for (int c = 0; c < 2; c++) {
                        renderSubCube(matrix, poseStack, overlay, LightTexture.FULL_BRIGHT, packedOverlay, ticks,
                                instability, startUp, a, b, c, true);
                    }
                }
            }
        }

        poseStack.popPose();
    }

    private static void renderSubCube(RunicMatrixBlockEntity matrix, PoseStack poseStack, VertexConsumer consumer,
            int packedLight, int packedOverlay, float ticks, float instability, float startUp, int a, int b, int c,
            boolean glow) {
        float xJitter = 0.0F;
        float yJitter = 0.0F;
        float zJitter = 0.0F;
        if (matrix.isActive()) {
            xJitter = Mth.sin((ticks + a * 10.0F) / (15.0F - instability / 2.0F)) * 0.01F * startUp * instability;
            yJitter = Mth.sin((ticks + b * 10.0F) / (14.0F - instability / 2.0F)) * 0.01F * startUp * instability;
            zJitter = Mth.sin((ticks + c * 10.0F) / (13.0F - instability / 2.0F)) * 0.01F * startUp * instability;
        }

        int xSign = a == 0 ? -1 : 1;
        int ySign = b == 0 ? -1 : 1;
        int zSign = c == 0 ? -1 : 1;
        float alpha = glow ? (Mth.sin((ticks + a * 2.0F + b * 3.0F + c * 4.0F) / 4.0F) * 0.1F + 0.2F) * startUp : 1.0F;
        float minV = glow ? 0.5F : 0.0F;
        float maxV = glow ? 1.0F : 0.5F;

        poseStack.pushPose();
        poseStack.translate(xJitter + xSign * CUBE_OFFSET, yJitter + ySign * CUBE_OFFSET,
                zJitter + zSign * CUBE_OFFSET);
        if (a > 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
        if (b > 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        if (c > 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
        drawCube(poseStack, consumer, CUBE_SIZE, glow ? 0.8F : 1.0F, glow ? 0.1F : 1.0F, 1.0F, alpha,
                packedLight, packedOverlay, minV, maxV);
        poseStack.popPose();
    }

    private static void drawCube(PoseStack poseStack, VertexConsumer consumer, float size, float red, float green,
            float blue, float alpha, int packedLight, int packedOverlay, float minV, float maxV) {
        float h = size / 2.0F;
        PoseStack.Pose pose = poseStack.last();
        quad(pose, consumer, -h, -h, h, h, -h, h, h, h, h, -h, h, h, 0, 0, 1, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
        quad(pose, consumer, h, -h, -h, -h, -h, -h, -h, h, -h, h, h, -h, 0, 0, -1, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
        quad(pose, consumer, -h, h, h, h, h, h, h, h, -h, -h, h, -h, 0, 1, 0, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
        quad(pose, consumer, -h, -h, -h, h, -h, -h, h, -h, h, -h, -h, h, 0, -1, 0, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
        quad(pose, consumer, h, -h, h, h, -h, -h, h, h, -h, h, h, h, 1, 0, 0, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
        quad(pose, consumer, -h, -h, -h, -h, -h, h, -h, h, h, -h, h, -h, -1, 0, 0, red, green, blue, alpha,
                packedLight, packedOverlay, minV, maxV);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float x1, float y1, float z1, float x2,
            float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float normalX,
            float normalY, float normalZ, float red, float green, float blue, float alpha, int packedLight,
            int packedOverlay, float minV, float maxV) {
        vertex(pose, consumer, x1, y1, z1, 0.0F, minV, normalX, normalY, normalZ, red, green, blue, alpha, packedLight,
                packedOverlay);
        vertex(pose, consumer, x2, y2, z2, 1.0F, minV, normalX, normalY, normalZ, red, green, blue, alpha, packedLight,
                packedOverlay);
        vertex(pose, consumer, x3, y3, z3, 1.0F, maxV, normalX, normalY, normalZ, red, green, blue, alpha, packedLight,
                packedOverlay);
        vertex(pose, consumer, x4, y4, z4, 0.0F, maxV, normalX, normalY, normalZ, red, green, blue, alpha, packedLight,
                packedOverlay);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u,
            float v, float normalX, float normalY, float normalZ, float red, float green, float blue, float alpha,
            int packedLight, int packedOverlay) {
        consumer.addVertex(pose, x, y, z).setColor(red, green, blue, alpha).setUv(u, v).setOverlay(packedOverlay)
                .setLight(packedLight).setNormal(pose, normalX, normalY, normalZ);
    }

    private static float renderTicks(float partialTick) {
        return Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount + partialTick : partialTick;
    }
}
