package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ArcanePedestalBlockEntity pedestal, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = pedestal.getStoredItem();
        if (stack.isEmpty()) {
            return;
        }

        Level level = pedestal.getLevel();
        float age = level != null ? level.getGameTime() + partialTick : partialTick;
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.15D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.0F));
        poseStack.scale(0.55F, 0.55F, 0.55F);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, level, 0);
        poseStack.popPose();
    }
}
