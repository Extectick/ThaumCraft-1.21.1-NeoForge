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
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.common.blockentities.NodeJarBlockEntity;

public class NodeJarRenderer implements BlockEntityRenderer<NodeJarBlockEntity> {
    private static final ResourceLocation JAR = Thaumcraft.id("textures/block/jar.png");

    private final ModelPart jarCore = part(cube(0, 0, -5.0F, -12.0F, -5.0F,
            10.0F, 12.0F, 10.0F));
    private final ModelPart jarLid = part(cube(0, 24, -3.0F, 0.0F, -3.0F,
            6.0F, 2.0F, 6.0F), 0.0F, -14.0F, 0.0F);

    public NodeJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NodeJarBlockEntity jar, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        NodeJarData data = jar.getNodeData();
        if (data.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.43D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.58F, 0.58F, 0.58F);
        AuraNodeRenderer.renderNodeLayers(poseStack, bufferSource, data.aspects(), data.nodeType(),
                data.nodeModifier(), 0.8F, 1.0F);
        poseStack.popPose();

        float remaining = Math.max(0.0F, jar.getCaptureAnimationTicks() - partialTick);
        float captureScale = 1.0F + 2.0F * remaining / 20.0F;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(captureScale, captureScale, captureScale);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(JAR));
        this.jarCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.jarLid.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(NodeJarBlockEntity blockEntity) {
        return blockEntity.getCaptureAnimationTicks() > 0;
    }

    private static ModelPart part(ModelPart.Cube cube) {
        return new ModelPart(List.of(cube), Map.of());
    }

    private static ModelPart part(ModelPart.Cube cube, float x, float y, float z) {
        ModelPart part = part(cube);
        part.setPos(x, y, z);
        return part;
    }

    private static ModelPart.Cube cube(int textureU, int textureV, float x, float y, float z,
            float width, float height, float depth) {
        return new ModelPart.Cube(textureU, textureV, x, y, z, width, height, depth,
                0.0F, 0.0F, 0.0F, false, 64.0F, 32.0F, Set.of(Direction.values()));
    }
}
