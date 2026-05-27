package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.renderers.block.ArcaneAlembicRenderer;

public class ArcaneAlembicItemRenderer extends BlockEntityWithoutLevelRenderer {
    public ArcaneAlembicItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        poseStack.translate(0.0F, 0.0F, -0.4F);

        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0.0F, 0.0F, -0.08F);
            poseStack.scale(0.82F, 0.82F, 0.82F);
        } else if (displayContext == ItemDisplayContext.GROUND) {
            poseStack.scale(0.55F, 0.55F, 0.55F);
        }

        ArcaneAlembicRenderer.getModel().render(poseStack, buffer, RenderType::entityCutoutNoCull, packedLight,
                OverlayTexture.NO_OVERLAY, 0.0F, ArcaneAlembicRenderer.itemTransforms());
        poseStack.popPose();
    }

    public static ArcaneAlembicItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new ArcaneAlembicItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
