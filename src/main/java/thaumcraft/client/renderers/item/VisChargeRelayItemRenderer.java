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
import thaumcraft.client.renderers.block.VisChargeRelayRenderer;

public class VisChargeRelayItemRenderer extends BlockEntityWithoutLevelRenderer {
    public VisChargeRelayItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        applyItemTransform(displayContext, poseStack);
        VisChargeRelayRenderer.renderChargerModel(poseStack, buffers, 0.0F, packedLight, false);
        poseStack.popPose();
    }

    private static void applyItemTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        switch (displayContext) {
            case GUI -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.9F, 0.9F, 0.9F);
            }
            case GROUND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.48F, 0.48F, 0.48F);
            }
            case FIXED -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-25.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.7F, 0.7F, 0.7F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-50.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(35.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(25.0F));
                poseStack.scale(0.55F, 0.55F, 0.55F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(35.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(30.0F));
                poseStack.scale(0.45F, 0.45F, 0.45F);
            }
            default -> poseStack.scale(0.7F, 0.7F, 0.7F);
        }
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static VisChargeRelayItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new VisChargeRelayItemRenderer(
                minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
