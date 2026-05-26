package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.InfusionPillarBlockEntity;
import thaumcraft.common.blocks.InfusionPillarBlock;
import thaumcraft.common.blocks.InfusionPillarBlock.Corner;

public class InfusionPillarRenderer implements BlockEntityRenderer<InfusionPillarBlockEntity> {
    private static final ResourceLocation MODEL = Thaumcraft.id("models/block/pillar.obj");
    private static final ITextureRenderTypeLookup RENDER_TYPE = RenderType::entityCutoutNoCull;
    private static CompositeRenderable model;

    public InfusionPillarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(InfusionPillarBlockEntity pillar, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = pillar.getBlockState();
        if (state.getValue(InfusionPillarBlock.TOP)) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation(state.getValue(InfusionPillarBlock.CORNER))));
        getModel().render(poseStack, bufferSource, RENDER_TYPE, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                CompositeRenderable.Transforms.EMPTY);
        poseStack.popPose();
    }

    private static CompositeRenderable getModel() {
        if (model == null) {
            ObjModel.ModelSettings settings = new ObjModel.ModelSettings(MODEL, false, true, true, true, null);
            model = ObjLoader.INSTANCE.loadModel(settings).bakeRenderable(StandaloneGeometryBakingContext.create(MODEL));
        }
        return model;
    }

    private static float rotation(Corner corner) {
        return switch (corner) {
            case NORTH_WEST -> 0.0F;
            case SOUTH_WEST -> 90.0F;
            case SOUTH_EAST -> 180.0F;
            case NORTH_EAST -> 270.0F;
        };
    }
}
