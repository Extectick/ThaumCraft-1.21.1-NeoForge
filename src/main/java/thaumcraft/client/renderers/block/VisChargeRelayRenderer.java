package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.VisChargeRelayBlockEntity;
import thaumcraft.common.visnet.VisNetHandler;

public class VisChargeRelayRenderer extends VisRelayRenderer<VisChargeRelayBlockEntity> {
    private static final ResourceLocation MODEL = Thaumcraft.id("models/block/vis_relay.obj");
    private static final ITextureRenderTypeLookup CUTOUT = RenderType::entityCutoutNoCull;
    private static final Matrix4f HIDDEN = new Matrix4f().scale(0.0F);
    private static final CompositeRenderable.Transforms RING_BASE =
            CompositeRenderable.Transforms.of(ImmutableMap.of("Crystal", HIDDEN, "RingFloat", HIDDEN,
                    "Support", HIDDEN));
    private static final CompositeRenderable.Transforms RING_FLOAT =
            CompositeRenderable.Transforms.of(ImmutableMap.of("Crystal", HIDDEN, "RingBase", HIDDEN,
                    "Support", HIDDEN));
    private static final CompositeRenderable.Transforms CRYSTAL =
            CompositeRenderable.Transforms.of(ImmutableMap.of("RingFloat", HIDDEN, "RingBase", HIDDEN,
                    "Support", HIDDEN));
    private static CompositeRenderable model;

    public VisChargeRelayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(VisChargeRelayBlockEntity relay, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        renderChargerModel(poseStack, buffers, partialTick, packedLight,
                VisNetHandler.isNodeValid(relay.getLevel(), relay.getParentPos()));
        super.render(relay, partialTick, poseStack, buffers, packedLight, packedOverlay);
    }

    public static void renderChargerModel(PoseStack poseStack, MultiBufferSource buffers, float partialTick,
            int packedLight, boolean connected) {
        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));

        getModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                RING_FLOAT);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(0.0D, 0.0D, 0.5D);
        for (int index = 0; index < 4; index++) {
            CompositeRenderable.Transforms support = supportTransforms(index);
            getModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                    support);
        }
        poseStack.popPose();

        int light = (connected ? 50 : 0)
                + (int) (150.0F * (Mth.sin(ticks / 2.0F) * 0.05F + 0.95F));
        getModel().render(poseStack, buffers, CUTOUT, LightTexture.pack(light % 16, light / 16),
                OverlayTexture.NO_OVERLAY, partialTick, CRYSTAL);
        poseStack.popPose();
    }

    public static void renderRelayModel(PoseStack poseStack, MultiBufferSource buffers, float partialTick,
            int packedLight, Direction orientation, boolean connected) {
        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        applyRelayOrientation(poseStack, orientation);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));

        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(0.0D, 0.0D, -0.16D);
        getModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                RING_BASE);
        poseStack.popPose();

        getModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                RING_FLOAT);

        int light = (connected ? 50 : 0)
                + (int) (150.0F * (Mth.sin(ticks / 2.0F) * 0.05F + 0.95F));
        getModel().render(poseStack, buffers, CUTOUT, LightTexture.pack(light % 16, light / 16),
                OverlayTexture.NO_OVERLAY, partialTick, CRYSTAL);
        poseStack.popPose();
    }

    private static void applyRelayOrientation(PoseStack poseStack, Direction orientation) {
        switch (orientation) {
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case NORTH -> {
            }
        }
    }

    private static CompositeRenderable.Transforms supportTransforms(int index) {
        Matrix4f transform = new Matrix4f().rotateZ((float) Math.toRadians(90.0F * index));
        return CompositeRenderable.Transforms.of(ImmutableMap.of("Crystal", HIDDEN, "RingFloat", HIDDEN,
                "RingBase", HIDDEN, "Support", transform));
    }

    private static CompositeRenderable getModel() {
        if (model == null) {
            ObjModel.ModelSettings settings = new ObjModel.ModelSettings(MODEL, false, true, true, true, null);
            model = ObjLoader.INSTANCE.loadModel(settings)
                    .bakeRenderable(StandaloneGeometryBakingContext.create(MODEL));
        }
        return model;
    }
}
