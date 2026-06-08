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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.renderers.block.AuraNodeRenderer;

public class AuraNodeItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final AspectList ITEM_ASPECTS = new AspectList()
            .add(Aspect.AIR, 40)
            .add(Aspect.FIRE, 40)
            .add(Aspect.EARTH, 40)
            .add(Aspect.WATER, 40);

    public AuraNodeItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float scale = displayContext == ItemDisplayContext.GUI ? 0.78F
                : displayContext == ItemDisplayContext.GROUND ? 0.45F : 0.65F;
        poseStack.scale(scale, scale, scale);
        renderPlane(poseStack, buffers);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderPlane(poseStack, buffers);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        renderPlane(poseStack, buffers);
        poseStack.popPose();
    }

    private static void renderPlane(PoseStack poseStack, MultiBufferSource buffers) {
        AuraNodeRenderer.renderNodeLayers(poseStack, buffers, ITEM_ASPECTS, NodeType.NORMAL, null, 0.5F, 1.0F);
    }

    public static AuraNodeItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new AuraNodeItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
