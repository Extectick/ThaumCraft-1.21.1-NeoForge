package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.renderers.block.NodeStabilizerRenderer;
import thaumcraft.common.registry.TCItems;

public class NodeStabilizerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public NodeStabilizerItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        NodeStabilizerRenderer.renderStabilizer(poseStack, buffers,
                stack.is(TCItems.ADVANCED_NODE_STABILIZER.get()), 0, 0.0F, packedLight);
    }

    public static NodeStabilizerItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new NodeStabilizerItemRenderer(
                minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
