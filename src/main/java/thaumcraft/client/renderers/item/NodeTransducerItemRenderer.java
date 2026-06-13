package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.renderers.block.NodeTransducerRenderer;
import thaumcraft.common.blockentities.NodeTransducerBlockEntity;

public class NodeTransducerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public NodeTransducerItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        applyItemTransform(displayContext, poseStack);
        NodeTransducerRenderer.renderTransducer(poseStack, buffers, NodeTransducerBlockEntity.STATUS_IDLE, 0, 0.0F,
                packedLight);
        poseStack.popPose();
    }

    private static void applyItemTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        switch (displayContext) {
            case GUI -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-35.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(12.0F));
                poseStack.scale(0.72F, 0.72F, 0.72F);
            }
            case GROUND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.42F, 0.42F, 0.42F);
            }
            case FIXED -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-25.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.62F, 0.62F, 0.62F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(35.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(28.0F));
                poseStack.scale(0.52F, 0.52F, 0.52F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-60.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(35.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(35.0F));
                poseStack.scale(0.42F, 0.42F, 0.42F);
            }
            default -> poseStack.scale(0.65F, 0.65F, 0.65F);
        }
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static NodeTransducerItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new NodeTransducerItemRenderer(
                minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
