package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;

public class RunicMatrixRenderer implements BlockEntityRenderer<RunicMatrixBlockEntity> {
    private static final ResourceLocation TEXTURE = Thaumcraft.id("textures/block/infuser.png");
    private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;
    private final ModelPart baseCube = cube(0);
    private final ModelPart runeCube = cube(32);

    public RunicMatrixRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RunicMatrixBlockEntity matrix, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float ticks = renderTicks(matrix, partialTick);
        float startUp = matrix.getStartUp();
        float craftStart = matrix.isCrafting() ? Math.max(0.0F, 1.0F - Math.min(matrix.getCraftCount(), 50) / 50.0F) : 0.0F;
        float instability = Math.min(6.0F,
                1.0F + matrix.getInstability() * 0.66F * (Math.min(matrix.getCraftCount(), 50) / 50.0F));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees((ticks % 360.0F) * startUp));
        poseStack.mulPose(Axis.XP.rotationDegrees(35.0F * startUp));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * startUp));
        if (craftStart > 0.0F) {
            float pulse = (float) Math.sin((1.0F - craftStart) * Math.PI);
            poseStack.mulPose(Axis.XP.rotationDegrees(pulse * 7.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pulse * -7.0F));
            float scale = 1.0F + pulse * 0.09F;
            poseStack.scale(scale, scale, scale);
        }

        VertexConsumer baseConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderCubes(matrix, ticks, instability, poseStack, baseConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1,
                this.baseCube);

        if (matrix.isActive()) {
            VertexConsumer runeConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
            renderGlowingRunes(matrix, ticks, instability, startUp, poseStack, runeConsumer);
        }
        poseStack.popPose();

        if (matrix.isCrafting()) {
            renderCraftingHalo(matrix, poseStack, bufferSource);
        }
    }

    private static void renderCraftingHalo(RunicMatrixBlockEntity matrix, PoseStack poseStack,
            MultiBufferSource bufferSource) {
        int count = matrix.getCraftCount();
        int quality = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FAST ? 10 : 20;
        float rotationProgress = count / 500.0F;
        float ramp = Math.min(count, 50) / 50.0F;
        Random random = new Random(245L);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.dragonRays());

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        for (int i = 0; i < quality; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + rotationProgress * 360.0F));

            float length = random.nextFloat() * 20.0F + 5.0F;
            float width = random.nextFloat() * 2.0F + 1.0F;
            length /= 20.0F / ramp;
            width /= 20.0F / ramp;

            addHaloTriangle(consumer, poseStack, -0.866F * width, length, -0.5F * width,
                    0.866F * width, length, -0.5F * width);
            addHaloTriangle(consumer, poseStack, 0.866F * width, length, -0.5F * width,
                    0.0F, length, width);
            addHaloTriangle(consumer, poseStack, 0.0F, length, width,
                    -0.866F * width, length, -0.5F * width);
        }
        poseStack.popPose();
    }

    private static void addHaloTriangle(VertexConsumer consumer, PoseStack poseStack, float x1, float y1, float z1,
            float x2, float y2, float z2) {
        addHaloVertex(consumer, poseStack, 0.0F, 0.0F, 0.0F, 255, 255, 255, 255);
        addHaloVertex(consumer, poseStack, x1, y1, z1, 203, 0, 255, 0);
        addHaloVertex(consumer, poseStack, x2, y2, z2, 203, 0, 255, 0);
    }

    private static void addHaloVertex(VertexConsumer consumer, PoseStack poseStack, float x, float y, float z,
            int red, int green, int blue, int alpha) {
        consumer.addVertex(poseStack.last(), x, y, z).setColor(red, green, blue, alpha);
    }

    private void renderGlowingRunes(RunicMatrixBlockEntity matrix, float ticks, float instability, float startUp,
            PoseStack poseStack, VertexConsumer consumer) {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    float alpha = ((float) Math.sin((ticks + a * 2 + b * 3 + c * 4) / 4.0F) * 0.1F + 0.2F) * startUp;
                    int color = color(0.8F, 0.1F, 1.0F, alpha);
                    renderCube(matrix, ticks, instability, a, b, c, poseStack, consumer, FULL_BRIGHT,
                            OverlayTexture.NO_OVERLAY, color, this.runeCube);
                }
            }
        }
    }

    private void renderCubes(RunicMatrixBlockEntity matrix, float ticks, float instability, PoseStack poseStack,
            VertexConsumer consumer, int packedLight, int packedOverlay, int color, ModelPart cube) {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    renderCube(matrix, ticks, instability, a, b, c, poseStack, consumer, packedLight, packedOverlay,
                            color, cube);
                }
            }
        }
    }

    private static void renderCube(RunicMatrixBlockEntity matrix, float ticks, float instability, int a, int b, int c,
            PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color,
            ModelPart cube) {
        float b1 = 0.0F;
        float b2 = 0.0F;
        float b3 = 0.0F;
        if (matrix.isActive()) {
            float startUp = matrix.getStartUp();
            b1 = (float) Math.sin((ticks + a * 10) / (15.0F - instability / 2.0F)) * 0.01F * startUp * instability;
            b2 = (float) Math.sin((ticks + b * 10) / (14.0F - instability / 2.0F)) * 0.01F * startUp * instability;
            b3 = (float) Math.sin((ticks + c * 10) / (13.0F - instability / 2.0F)) * 0.01F * startUp * instability;
        }

        int aa = a == 0 ? -1 : 1;
        int bb = b == 0 ? -1 : 1;
        int cc = c == 0 ? -1 : 1;

        poseStack.pushPose();
        poseStack.translate(b1 + aa * 0.25F, b2 + bb * 0.25F, b3 + cc * 0.25F);
        if (a > 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
        if (b > 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        if (c > 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
        poseStack.scale(0.45F, 0.45F, 0.45F);
        cube.render(poseStack, consumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    private static float renderTicks(RunicMatrixBlockEntity matrix, float partialTick) {
        Level level = matrix.getLevel();
        return level != null ? level.getGameTime() + partialTick : partialTick;
    }

    private static ModelPart cube(int textureV) {
        return new ModelPart(List.of(new ModelPart.Cube(0, textureV, -8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F,
                0.0F, 0.0F, 0.0F, true, 64.0F, 64.0F, Set.of(Direction.values()))), Map.of());
    }

    private static int color(float red, float green, float blue, float alpha) {
        int a = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));
        int r = Math.max(0, Math.min(255, (int) (red * 255.0F)));
        int g = Math.max(0, Math.min(255, (int) (green * 255.0F)));
        int b = Math.max(0, Math.min(255, (int) (blue * 255.0F)));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
