package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.BrainInAJarBlockEntity;

public class BrainInAJarRenderer implements BlockEntityRenderer<BrainInAJarBlockEntity> {
    private static final ResourceLocation BRAIN_TEXTURE = Thaumcraft.id("textures/block/brain2.png");
    private static final ResourceLocation BRINE_TEXTURE = Thaumcraft.id("textures/block/jarbrine.png");
    private final ModelPart brainCore = cube(0, 0, 0.0F, 0.0F, 0.0F, 12.0F, 10.0F, 16.0F, 128.0F, 64.0F);
    private final ModelPart brainLower = cube(64, 0, 0.0F, 0.0F, 0.0F, 8.0F, 3.0F, 7.0F, 128.0F, 64.0F);
    private final ModelPart brainStem = cube(0, 32, 0.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, 128.0F, 64.0F);
    private final ModelPart brine = cube(0, 0, -4.0F, -11.0F, -4.0F, 8.0F, 10.0F, 8.0F, 64.0F, 32.0F);

    public BrainInAJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BrainInAJarBlockEntity brain, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.01D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        renderBrain(brain, partialTick, poseStack, bufferSource, packedLight);
        VertexConsumer brineConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BRINE_TEXTURE));
        this.brine.render(poseStack, brineConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
    }

    private void renderBrain(BrainInAJarBlockEntity brain, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight) {
        float ticks = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount + partialTick : partialTick;
        float bob = (float) Math.sin(ticks / 14.0F) * 0.03F + 0.03F;

        poseStack.pushPose();
        poseStack.translate(0.0F, -0.8F + bob, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(brain.getRenderedRotation(partialTick)));
        poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
        poseStack.scale(0.4F, 0.4F, 0.4F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BRAIN_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(-6.0F / 16.0F, 8.0F / 16.0F, -8.0F / 16.0F);
        this.brainCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-4.0F / 16.0F, 18.0F / 16.0F, 0.0F);
        this.brainLower.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-1.0F / 16.0F, 18.0F / 16.0F, -2.0F / 16.0F);
        poseStack.mulPose(Axis.XP.rotation(0.4089647F));
        this.brainStem.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        poseStack.popPose();
    }

    private static ModelPart cube(int textureU, int textureV, float x, float y, float z, float width, float height,
            float depth, float textureWidth, float textureHeight) {
        return new ModelPart(List.of(new ModelPart.Cube(textureU, textureV, x, y, z, width, height, depth,
                0.0F, 0.0F, 0.0F, false, textureWidth, textureHeight, Set.of(Direction.values()))), Map.of());
    }
}
