package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
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
import thaumcraft.common.blockentities.NodeStabilizerBlockEntity;

public class NodeStabilizerRenderer implements BlockEntityRenderer<NodeStabilizerBlockEntity> {
    private static final ResourceLocation BASE_MODEL = Thaumcraft.id("models/block/node_stabilizer.obj");
    private static final ResourceLocation OVERLAY_MODEL = Thaumcraft.id("models/block/node_stabilizer_overlay.obj");
    private static final ResourceLocation ADVANCED_OVERLAY_MODEL =
            Thaumcraft.id("models/block/node_stabilizer_overlay_advanced.obj");
    private static final ResourceLocation BUBBLE = Thaumcraft.id("textures/misc/node_bubble.png");
    private static final ITextureRenderTypeLookup CUTOUT = RenderType::entityCutoutNoCull;
    private static final Matrix4f HIDDEN = new Matrix4f().scale(0.0F);
    private static final CompositeRenderable.Transforms LOCK_ONLY =
            CompositeRenderable.Transforms.of(ImmutableMap.of("piston", HIDDEN));
    private static final RenderType BUBBLE_RENDER_TYPE = RenderType.create(
            "thaumcraft_node_stabilizer_bubble",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(BUBBLE, false, false))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));
    private static CompositeRenderable baseModel;
    private static CompositeRenderable overlayModel;
    private static CompositeRenderable advancedOverlayModel;

    public NodeStabilizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NodeStabilizerBlockEntity stabilizer, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        renderStabilizer(poseStack, buffers, stabilizer.isAdvanced(), stabilizer.getAnimationCount(),
                partialTick, packedLight);
        if (stabilizer.getAnimationCount() > 0) {
            renderBubble(poseStack, buffers, stabilizer.isAdvanced(), stabilizer.getAnimationCount(), partialTick);
        }
    }

    @Override
    public AABB getRenderBoundingBox(NodeStabilizerBlockEntity stabilizer) {
        return new AABB(
                stabilizer.getBlockPos().getX(),
                stabilizer.getBlockPos().getY(),
                stabilizer.getBlockPos().getZ(),
                stabilizer.getBlockPos().getX() + 1.0D,
                stabilizer.getBlockPos().getY() + 2.0D,
                stabilizer.getBlockPos().getZ() + 1.0D);
    }

    public static void renderStabilizer(PoseStack poseStack, MultiBufferSource buffers, boolean advanced, int count,
            float partialTick, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        getBaseModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                LOCK_ONLY);

        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;
        for (int index = 0; index < 4; index++) {
            CompositeRenderable.Transforms piston = pistonTransforms(index, count);
            getBaseModel().render(poseStack, buffers, CUTOUT, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                    piston);
            float pulse = Mth.sin((ticks + index * 5.0F) / 3.0F) * 0.1F + 0.9F;
            int light = Mth.clamp(3 + Math.round(12.0F * count / 37.0F * pulse), 0, 15);
            getOverlayModel(advanced).render(poseStack, buffers, CUTOUT, LightTexture.pack(light, light),
                    OverlayTexture.NO_OVERLAY, partialTick, piston);
        }
        poseStack.popPose();
    }

    private static CompositeRenderable.Transforms pistonTransforms(int index, int count) {
        Matrix4f transform = new Matrix4f()
                .rotateZ((float) Math.toRadians(90.0F * index))
                .rotateY((float) Math.toRadians(45.0F))
                .translate(0.0F, 0.0F, count / 100.0F);
        return CompositeRenderable.Transforms.of(ImmutableMap.of("lock", HIDDEN, "piston", transform));
    }

    private static void renderBubble(PoseStack poseStack, MultiBufferSource buffers, boolean advanced, int count,
            float partialTick) {
        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;
        float alpha = count / 37.0F * (Mth.sin(ticks / 8.0F) * 0.1F + 0.5F);
        int color = advanced ? 0xFF4444 : 0xFFFFFF;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int opacity = Mth.clamp(Math.round(alpha * 255.0F), 0, 255);

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        VertexConsumer consumer = buffers.getBuffer(BUBBLE_RENDER_TYPE);
        addBubbleVertex(poseStack, consumer, -0.9F, -0.9F, 0.0F, 1.0F, red, green, blue, opacity);
        addBubbleVertex(poseStack, consumer, 0.9F, -0.9F, 1.0F, 1.0F, red, green, blue, opacity);
        addBubbleVertex(poseStack, consumer, 0.9F, 0.9F, 1.0F, 0.0F, red, green, blue, opacity);
        addBubbleVertex(poseStack, consumer, -0.9F, 0.9F, 0.0F, 0.0F, red, green, blue, opacity);
        poseStack.popPose();
    }

    private static void addBubbleVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float u,
            float v, int red, int green, int blue, int alpha) {
        consumer.addVertex(poseStack.last(), x, y, 0.0F)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private static CompositeRenderable getBaseModel() {
        if (baseModel == null) {
            baseModel = load(BASE_MODEL);
        }
        return baseModel;
    }

    private static CompositeRenderable getOverlayModel(boolean advanced) {
        if (advanced) {
            if (advancedOverlayModel == null) {
                advancedOverlayModel = load(ADVANCED_OVERLAY_MODEL);
            }
            return advancedOverlayModel;
        }
        if (overlayModel == null) {
            overlayModel = load(OVERLAY_MODEL);
        }
        return overlayModel;
    }

    private static CompositeRenderable load(ResourceLocation location) {
        ObjModel.ModelSettings settings = new ObjModel.ModelSettings(location, false, true, true, true, null);
        return ObjLoader.INSTANCE.loadModel(settings)
                .bakeRenderable(StandaloneGeometryBakingContext.create(location));
    }
}
