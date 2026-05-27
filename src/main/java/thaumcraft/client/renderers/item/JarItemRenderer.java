package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCItems;

public class JarItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation JAR = Thaumcraft.id("textures/block/jar.png");
    private static final ResourceLocation JAR_VOID = Thaumcraft.id("textures/block/jar_void.png");
    private static final ResourceLocation BRINE = Thaumcraft.id("textures/block/jarbrine.png");
    private static final ResourceLocation BRAIN = Thaumcraft.id("textures/block/brain2.png");

    private final ModelPart jarCore = part(cube(0, 0, -5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F, 64.0F, 32.0F));
    private final ModelPart jarLid = part(cube(0, 24, -3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F, 64.0F, 32.0F),
            0.0F, -14.0F, 0.0F);
    private final ModelPart brine = part(cube(0, 0, -4.0F, -11.0F, -4.0F, 8.0F, 10.0F, 8.0F, 64.0F, 32.0F));
    private final ModelPart brainCore = part(cube(0, 0, 0.0F, 0.0F, 0.0F, 12.0F, 10.0F, 16.0F, 128.0F, 64.0F),
            -6.0F, 8.0F, -8.0F);
    private final ModelPart brainLower = part(cube(64, 0, 0.0F, 0.0F, 0.0F, 8.0F, 3.0F, 7.0F, 128.0F, 64.0F),
            -4.0F, 18.0F, 0.0F);
    private final ModelPart brainStem = part(cube(0, 32, 0.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, 128.0F, 64.0F),
            -1.0F, 18.0F, -2.0F);

    public JarItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.12F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0.0F, 0.03F, 0.0F);
            poseStack.scale(0.86F, 0.86F, 0.86F);
        }

        if (stack.is(TCItems.BRAIN_IN_A_JAR.get())) {
            renderBrine(poseStack, buffer, packedLight);
            renderBrain(poseStack, buffer, packedLight);
        } else if (stack.is(TCItems.NODE_IN_A_JAR.get())) {
            renderBrine(poseStack, buffer, packedLight);
        }

        renderJar(stack.is(TCItems.VOID_JAR.get()) ? JAR_VOID : JAR, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    private void renderJar(ResourceLocation texture, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        this.jarCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.jarLid.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    private void renderBrine(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(BRINE));
        this.brine.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    private void renderBrain(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.78F, 0.0F);
        poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
        poseStack.scale(0.4F, 0.4F, 0.4F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BRAIN));
        this.brainCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.brainLower.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.brainStem.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
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
            float width, float height, float depth, float textureWidth, float textureHeight) {
        return new ModelPart.Cube(textureU, textureV, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F,
                false, textureWidth, textureHeight, Set.of(Direction.values()));
    }

    public static JarItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new JarItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
