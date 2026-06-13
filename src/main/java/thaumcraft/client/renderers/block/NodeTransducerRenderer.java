package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.NodeTransducerBlockEntity;

public class NodeTransducerRenderer implements BlockEntityRenderer<NodeTransducerBlockEntity> {
    private static final ResourceLocation BASE_MODEL = Thaumcraft.id("models/block/node_converter.obj");
    private static final ResourceLocation IDLE_OVERLAY_MODEL =
            Thaumcraft.id("models/block/node_converter_overlay_idle.obj");
    private static final ResourceLocation FORWARD_OVERLAY_MODEL =
            Thaumcraft.id("models/block/node_converter_overlay_forward.obj");
    private static final ResourceLocation REVERSE_OVERLAY_MODEL =
            Thaumcraft.id("models/block/node_converter_overlay_reverse.obj");
    private static final ITextureRenderTypeLookup CUTOUT = RenderType::entityCutoutNoCull;
    private static final Matrix4f HIDDEN = new Matrix4f().scale(0.0F);
    private static final CompositeRenderable.Transforms LOCK_ONLY =
            CompositeRenderable.Transforms.of(ImmutableMap.of("piston", HIDDEN));
    private static CompositeRenderable baseModel;
    private static CompositeRenderable idleOverlayModel;
    private static CompositeRenderable forwardOverlayModel;
    private static CompositeRenderable reverseOverlayModel;

    public NodeTransducerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NodeTransducerBlockEntity transducer, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        renderTransducer(poseStack, buffers, transducer.getStatus(), transducer.getProgress(), partialTick,
                packedLight);
    }

    public static void renderTransducer(PoseStack poseStack, MultiBufferSource buffers, int status, int progress,
            float partialTick, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.0D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;
        getBaseModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                LOCK_ONLY);
        getOverlayModel(status).render(poseStack, buffers, CUTOUT,
                overlayLight(progress, ticks), OverlayTexture.NO_OVERLAY, partialTick, LOCK_ONLY);

        int count = Math.min(50, progress);
        float extension = count / 137.0F;
        for (int index = 0; index < 4; index++) {
            CompositeRenderable.Transforms piston = pistonTransforms(index, extension);
            getBaseModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                    piston);

            float pulse = Mth.sin((ticks + index * 5.0F) / 3.0F) * 0.1F + 0.9F;
            int light = 3 + Math.round(12.0F * Mth.clamp(extension * 2.5F * pulse, 0.0F, 1.0F));
            getOverlayModel(status).render(poseStack, buffers, CUTOUT,
                    LightTexture.pack(light, light), OverlayTexture.NO_OVERLAY, partialTick, piston);
        }
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(NodeTransducerBlockEntity transducer) {
        return new AABB(
                transducer.getBlockPos().getX(),
                transducer.getBlockPos().getY() - 1.0D,
                transducer.getBlockPos().getZ(),
                transducer.getBlockPos().getX() + 1.0D,
                transducer.getBlockPos().getY() + 1.0D,
                transducer.getBlockPos().getZ() + 1.0D);
    }

    private static CompositeRenderable.Transforms pistonTransforms(int index, float extension) {
        Matrix4f transform = new Matrix4f()
                .rotateZ((float) Math.toRadians(90.0F * index))
                .rotateY((float) Math.toRadians(45.0F))
                .translate(0.0F, 0.0F, extension);
        return CompositeRenderable.Transforms.of(ImmutableMap.of("lock", HIDDEN, "piston", transform));
    }

    private static int overlayLight(int progress, float ticks) {
        float extension = Math.min(50, progress) / 137.0F;
        float pulse = Mth.sin(ticks / 3.0F) * 0.1F + 0.9F;
        int light = 3 + Math.round(12.0F * Mth.clamp(extension * 2.5F * pulse, 0.0F, 1.0F));
        return LightTexture.pack(light, light);
    }

    private static CompositeRenderable getBaseModel() {
        if (baseModel == null) {
            baseModel = load(BASE_MODEL);
        }
        return baseModel;
    }

    private static CompositeRenderable getOverlayModel(int status) {
        if (status == NodeTransducerBlockEntity.STATUS_FORWARD) {
            if (forwardOverlayModel == null) {
                forwardOverlayModel = load(FORWARD_OVERLAY_MODEL);
            }
            return forwardOverlayModel;
        }
        if (status == NodeTransducerBlockEntity.STATUS_REVERSE) {
            if (reverseOverlayModel == null) {
                reverseOverlayModel = load(REVERSE_OVERLAY_MODEL);
            }
            return reverseOverlayModel;
        }
        if (idleOverlayModel == null) {
            idleOverlayModel = load(IDLE_OVERLAY_MODEL);
        }
        return idleOverlayModel;
    }

    private static CompositeRenderable load(ResourceLocation location) {
        ObjModel.ModelSettings settings = new ObjModel.ModelSettings(location, false, true, true, true, null);
        return ObjLoader.INSTANCE.loadModel(settings)
                .bakeRenderable(StandaloneGeometryBakingContext.create(location));
    }
}
