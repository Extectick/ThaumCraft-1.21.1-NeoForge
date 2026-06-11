package thaumcraft.client.renderers.block;

import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.registry.TCItems;

public class AuraNodeRenderer implements BlockEntityRenderer<AuraNodeBlockEntity> {
    private static final ResourceLocation NODE_TEXTURE = Thaumcraft.id("textures/misc/nodes.png");
    private static final ResourceLocation WISPY_TEXTURE = Thaumcraft.id("textures/misc/wispy.png");
    private static final int FRAMES = 32;
    private static final float LINK_QUALITY = 16.0F;
    private static final Map<AuraNodeBlockEntity, SmoothedDrainColor> DRAIN_COLORS = new WeakHashMap<>();
    private static final RenderType NODE_ADDITIVE_SEE_THROUGH = createSeeThroughType(
            "thaumcraft_node_additive_see_through", RenderStateShard.LIGHTNING_TRANSPARENCY);
    private static final RenderType NODE_TRANSLUCENT_SEE_THROUGH = createSeeThroughType(
            "thaumcraft_node_translucent_see_through", RenderStateShard.TRANSLUCENT_TRANSPARENCY);

    public AuraNodeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AuraNodeBlockEntity node, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean holdingThaumometer = player != null && (player.getMainHandItem().is(TCItems.THAUMOMETER.get())
                || player.getOffhandItem().is(TCItems.THAUMOMETER.get()));
        boolean hasGoggles = player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(TCItems.GOGGLES.get());
        boolean revealed = hasGoggles || (holdingThaumometer && isVisibleThroughThaumometer(player, node, partialTick));
        double distance = player == null ? 0.0D
                : Math.sqrt(player.distanceToSqr(node.getBlockPos().getCenter()));
        if (distance > (holdingThaumometer ? 48.0D : 64.0D)) {
            return;
        }

        float alpha = revealed ? (float) ((48.0D - distance) / 48.0D) : 0.1F;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        if (revealed) {
            renderNodeLayers(poseStack, bufferSource, node.getAspects(), node.getNodeType(), node.getNodeModifier(),
                    alpha, 1.0F, true);
        } else {
            renderStrip(poseStack, additive(bufferSource), frame(), 1, 0.5F, alpha, 0xFFFFFF, 0.0F, 0.001F);
        }
        poseStack.popPose();
        renderDrainBeam(node, partialTick, poseStack, bufferSource);
    }

    private static boolean isVisibleThroughThaumometer(LocalPlayer player, AuraNodeBlockEntity node,
            float partialTick) {
        Vec3 target = node.getBlockPos().getCenter();
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 direction = target.subtract(eye);
        double distance = direction.length();
        if (distance < 2.0D) {
            return true;
        }

        Vec3 view = player.getViewVector(partialTick);
        double dot = Mth.clamp(direction.scale(1.0D / distance).dot(view), -1.0D, 1.0D);

        // TC4 used UtilsFX.isVisibleTo(0.44F, ...): roughly the central
        // 25-degree cone covered by the thaumometer glass at the default FOV.
        double fovAdjustment = Mth.clamp(
                (Minecraft.getInstance().options.fov().get() - 70.0D) / 40.0D,
                0.0D, 1.0D);
        return Math.acos(dot) < 0.44D + fovAdjustment * 0.5D;
    }

    public static void renderNodeLayers(PoseStack poseStack, MultiBufferSource buffers, AspectList aspects,
            NodeType type, NodeModifier modifier, float alpha, float sizeMultiplier) {
        renderNodeLayers(poseStack, buffers, aspects, type, modifier, alpha, sizeMultiplier, false);
    }

    private static void renderNodeLayers(PoseStack poseStack, MultiBufferSource buffers, AspectList aspects,
            NodeType type, NodeModifier modifier, float alpha, float sizeMultiplier, boolean seeThrough) {
        if (aspects.isEmpty()) {
            return;
        }

        alpha *= modifierAlpha(modifier);
        int currentFrame = frame();
        float average = (float) aspects.visSize() / aspects.size();
        float lastAngle = 0.0F;
        LocalPlayer player = Minecraft.getInstance().player;
        float ticks = player == null ? System.nanoTime() / 50_000_000.0F : player.tickCount;
        int index = 0;
        for (Aspect aspect : aspects.getAspects()) {
            float layerAlpha = alpha;
            if (usesAlphaBlend(aspect)) {
                layerAlpha *= 1.5F;
            }
            float pulse = (float) Math.sin(ticks / (14.0F - Math.min(index, 10)));
            float scale = (0.2F + (pulse * 0.25F + 0.5F) * aspects.getAmount(aspect) / 50.0F) * sizeMultiplier;
            lastAngle = (float) (System.nanoTime() / 5_000_000L % (5000 + 500L * index))
                    / (5000.0F + 500.0F * index) * 360.0F;
            VertexConsumer consumer = usesAlphaBlend(aspect)
                    ? translucent(buffers, seeThrough)
                    : additive(buffers, seeThrough);
            renderStrip(poseStack, consumer, currentFrame, 0, scale,
                    layerAlpha / Math.max(1.0F, aspects.size() / 2.0F), aspect.getColor(), lastAngle,
                    0.001F * (index + 1));
            index++;
        }

        float coreScale = (0.1F + average / 150.0F) * sizeMultiplier;
        if (type == NodeType.HUNGRY) {
            coreScale *= 0.75F;
        }
        float coreAngle = type == NodeType.UNSTABLE ? 0.0F : lastAngle;
        VertexConsumer core = type == NodeType.DARK || type == NodeType.TAINTED
                ? translucent(buffers, seeThrough)
                : additive(buffers, seeThrough);
        renderStrip(poseStack, core, currentFrame, typeStrip(type), coreScale, alpha, 0xFFFFFF, coreAngle,
                0.001F * (aspects.size() + 1));
    }

    private static void renderStrip(PoseStack poseStack, VertexConsumer consumer, int frame, int strip, float size,
            float alpha, int color, float angle, float depthOffset) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, depthOffset);
        if (angle != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        }
        float half = Math.max(0.05F, size);
        float u1 = frame / (float) FRAMES;
        float u2 = (frame + 1) / (float) FRAMES;
        float v1 = strip / (float) FRAMES;
        float v2 = (strip + 1) / (float) FRAMES;
        int a = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        addVertex(poseStack, consumer, -half, -half, u2, v2, r, g, b, a);
        addVertex(poseStack, consumer, half, -half, u1, v2, r, g, b, a);
        addVertex(poseStack, consumer, half, half, u1, v1, r, g, b, a);
        addVertex(poseStack, consumer, -half, half, u2, v1, r, g, b, a);
        poseStack.popPose();
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float u, float v,
            int red, int green, int blue, int alpha) {
        consumer.addVertex(poseStack.last(), x, y, 0.0F)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private static VertexConsumer additive(MultiBufferSource buffers) {
        return additive(buffers, false);
    }

    private static VertexConsumer additive(MultiBufferSource buffers, boolean seeThrough) {
        // The original node renderer used SRC_ALPHA, ONE. RenderType.eyes uses
        // ONE, ONE, which ignores the node alpha and burns faint nodes to white.
        return buffers.getBuffer(seeThrough
                ? NODE_ADDITIVE_SEE_THROUGH
                : RenderType.EYES.apply(NODE_TEXTURE, RenderStateShard.LIGHTNING_TRANSPARENCY));
    }

    private static VertexConsumer translucent(MultiBufferSource buffers) {
        return translucent(buffers, false);
    }

    private static VertexConsumer translucent(MultiBufferSource buffers, boolean seeThrough) {
        return buffers.getBuffer(seeThrough
                ? NODE_TRANSLUCENT_SEE_THROUGH
                : RenderType.entityTranslucentEmissive(NODE_TEXTURE, false));
    }

    private static RenderType createSeeThroughType(String name,
            RenderStateShard.TransparencyStateShard transparency) {
        return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(NODE_TEXTURE, false, false))
                        .setTransparencyState(transparency)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false));
    }

    private static boolean usesAlphaBlend(Aspect aspect) {
        return aspect == Aspect.ENTROPY || aspect == Aspect.VOID;
    }

    private static int frame() {
        return (int) (System.nanoTime() / 40_000_000L % FRAMES);
    }

    private static int typeStrip(NodeType type) {
        return switch (type) {
            case NORMAL -> 1;
            case DARK -> 2;
            case HUNGRY -> 3;
            case PURE -> 4;
            case TAINTED -> 5;
            case UNSTABLE -> 6;
        };
    }

    private static float modifierAlpha(NodeModifier modifier) {
        if (modifier == null) {
            return 1.0F;
        }
        return switch (modifier) {
            case BRIGHT -> 1.5F;
            case PALE -> 0.66F;
            case FADING -> {
                LocalPlayer player = Minecraft.getInstance().player;
                float ticks = player == null ? System.nanoTime() / 50_000_000.0F : player.tickCount;
                yield (float) Math.sin(ticks / 3.0F) * 0.25F + 0.33F;
            }
        };
    }

    private static void renderDrainBeam(AuraNodeBlockEntity node, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers) {
        if (node.getLevel() == null || node.getDrainPlayerId() < 0) {
            DRAIN_COLORS.remove(node);
            return;
        }
        Entity entity = node.getLevel().getEntity(node.getDrainPlayerId());
        if (!(entity instanceof LivingEntity drainer) || !drainer.isUsingItem()) {
            return;
        }

        float swing = Mth.sin(drainer.getTicksUsingItem() / 10.0F) * 10.0F;
        Vec3 handOffset = new Vec3(-0.1D, -0.1D, 0.5D)
                .xRot(-Mth.lerp(partialTick, drainer.xRotO, drainer.getXRot()) * Mth.DEG_TO_RAD)
                .yRot(-Mth.lerp(partialTick, drainer.yRotO, drainer.getYRot()) * Mth.DEG_TO_RAD)
                .yRot(-swing * 0.01F)
                .xRot(-swing * 0.015F);
        Vec3 playerPos = new Vec3(
                Mth.lerp(partialTick, drainer.xOld, drainer.getX()),
                Mth.lerp(partialTick, drainer.yOld, drainer.getY()) + drainer.getEyeHeight(),
                Mth.lerp(partialTick, drainer.zOld, drainer.getZ())).add(handOffset);
        Vec3 nodeCenter = node.getBlockPos().getCenter();
        Vec3 localStart = playerPos.subtract(nodeCenter);
        int color = smoothDrainColor(node);
        float growth = Math.min(drainer.getTicksUsingItem(), 10) / 10.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        drawFloatyLine(poseStack, buffers.getBuffer(
                RenderType.EYES.apply(WISPY_TEXTURE, RenderStateShard.LIGHTNING_TRANSPARENCY)),
                localStart, Vec3.ZERO, color, -0.02F, growth, 0.15F);
        poseStack.popPose();
    }

    private static int smoothDrainColor(AuraNodeBlockEntity node) {
        int target = node.getDrainColor();
        SmoothedDrainColor state = DRAIN_COLORS.computeIfAbsent(node,
                ignored -> new SmoothedDrainColor(target, node.getTickCount()));
        if (state.tick != node.getTickCount()) {
            int current = state.color;
            int red = (((current >> 16) & 255) * 4 + ((target >> 16) & 255)) / 5;
            int green = (((current >> 8) & 255) * 4 + ((target >> 8) & 255)) / 5;
            int blue = ((current & 255) * 4 + (target & 255)) / 5;
            state.color = red << 16 | green << 8 | blue;
            state.tick = node.getTickCount();
        }
        return state.color;
    }

    private static void drawFloatyLine(PoseStack poseStack, VertexConsumer consumer, Vec3 start, Vec3 end,
            int color, float speed, float growth, float width) {
        Vec3 delta = start.subtract(end);
        float distance = (float) delta.length();
        if (!Float.isFinite(distance) || distance > 32.0F) {
            return;
        }
        int length = Mth.clamp(Math.round(distance) * 8, 1, 96);
        int visibleLength = Math.max(1, Mth.floor(length * growth));
        float time = System.nanoTime() / 30_000_000.0F;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;

        for (int i = 0; i < visibleLength; i++) {
            BeamPoint first = beamPoint(start, end, delta, distance, length, i, time, speed);
            BeamPoint second = beamPoint(start, end, delta, distance, length, i + 1, time, speed);
            addBeamQuad(poseStack, consumer, first, second, width, true, red, green, blue);
            addBeamQuad(poseStack, consumer, first, second, width, false, red, green, blue);
        }
    }

    private static BeamPoint beamPoint(Vec3 start, Vec3 end, Vec3 delta, float distance, int length, int index,
            float time, float speed) {
        float progress = index / (float) length;
        float alpha = 1.0F - Math.abs(index - length / 2.0F) / (length / 2.0F);
        double dx = delta.x + Mth.sin((float) ((start.z % 16.0D
                + distance * (1.0F - progress) * LINK_QUALITY / 2.0F - time % 32767.0F / 5.0F) / 4.0D))
                * 0.5F * alpha;
        double dy = delta.y + Mth.sin((float) ((start.x % 16.0D
                + distance * (1.0F - progress) * LINK_QUALITY / 2.0F - time % 32767.0F / 5.0F) / 3.0D))
                * 0.5F * alpha;
        double dz = delta.z + Mth.sin((float) ((start.y % 16.0D
                + distance * (1.0F - progress) * LINK_QUALITY / 2.0F - time % 32767.0F / 5.0F) / 2.0D))
                * 0.5F * alpha;
        Vec3 position = end.add(dx * progress, dy * progress, dz * progress);
        float u = (1.0F - progress) * distance - time * speed;
        return new BeamPoint(position, u, Mth.clamp(alpha, 0.0F, 1.0F));
    }

    private static void addBeamQuad(PoseStack poseStack, VertexConsumer consumer, BeamPoint first, BeamPoint second,
            float width, boolean vertical, int red, int green, int blue) {
        Vec3 offset = vertical ? new Vec3(0.0D, width, 0.0D) : new Vec3(width, 0.0D, 0.0D);
        addBeamVertex(poseStack, consumer, first.position.subtract(offset), first.u, 1.0F,
                red, green, blue, first.alpha);
        addBeamVertex(poseStack, consumer, second.position.subtract(offset), second.u, 1.0F,
                red, green, blue, second.alpha);
        addBeamVertex(poseStack, consumer, second.position.add(offset), second.u, 0.0F,
                red, green, blue, second.alpha);
        addBeamVertex(poseStack, consumer, first.position.add(offset), first.u, 0.0F,
                red, green, blue, first.alpha);
    }

    private static void addBeamVertex(PoseStack poseStack, VertexConsumer consumer, Vec3 point, float u, float v,
            int red, int green, int blue, float alpha) {
        consumer.addVertex(poseStack.last(), (float) point.x, (float) point.y, (float) point.z)
                .setColor(red, green, blue, Math.round(alpha * 255.0F))
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private record BeamPoint(Vec3 position, float u, float alpha) {
    }

    private static final class SmoothedDrainColor {
        private int color;
        private int tick;

        private SmoothedDrainColor(int color, int tick) {
            this.color = color;
            this.tick = tick;
        }
    }

    @Override
    public boolean shouldRenderOffScreen(AuraNodeBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
