package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.blockentities.VisRelayBlockEntity;
import thaumcraft.common.util.RevealerHelper;
import thaumcraft.common.visnet.IVisNetNode;
import thaumcraft.common.visnet.VisNetHandler;

public class VisRelayRenderer<T extends VisRelayBlockEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation BEAM = Thaumcraft.id("textures/misc/beam1.png");

    public VisRelayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T relay, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
            int packedLight, int packedOverlay) {
        Level level = relay.getLevel();
        Direction orientation = relay.getOrientation().getOpposite();
        if (level == null) {
            return;
        }
        BlockPos parentPos = relay.getParentPos();
        if (parentPos == null || !level.isLoaded(parentPos)) {
            parentPos = VisNetHandler.findParent(level, relay);
        }
        if (parentPos == null || !level.isLoaded(parentPos)) {
            return;
        }
        BlockEntity parentEntity = level.getBlockEntity(parentPos);
        IVisNetNode parent = VisNetHandler.asNode(parentEntity);
        if (parent == null && !(parentEntity instanceof AuraNodeBlockEntity)) {
            return;
        }
        if (parent != null && !VisNetHandler.isNodeValid(level, parentPos)) {
            return;
        }

        Vec3 from = relayEndpoint(relay.getBlockPos(), orientation);
        Vec3 to = parent == null ? Vec3.atCenterOf(parentPos) : parentEndpoint(parent, level);
        Vec3 localTo = to.subtract(Vec3.atLowerCornerOf(relay.getBlockPos()));
        Vec3 localFrom = from.subtract(Vec3.atLowerCornerOf(relay.getBlockPos()));
        float red = relay.isPulsing() ? relay.getPulseRed() : 0.9F;
        float green = relay.isPulsing() ? relay.getPulseGreen() : 0.9F;
        float blue = relay.isPulsing() ? relay.getPulseBlue() : 1.0F;
        float alpha = relay.isPulsing() ? 0.8F : 0.3F;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !RevealerHelper.showsNodes(player)) {
            alpha *= 0.1F;
        }

        VertexConsumer consumer = buffers.getBuffer(RenderType.EYES.apply(BEAM,
                RenderStateShard.LIGHTNING_TRANSPARENCY));
        addBeam(poseStack, consumer, localFrom, localTo, red, green, blue, alpha, partialTick);
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(T relay) {
        BlockPos pos = relay.getBlockPos();
        BlockPos parent = relay.getParentPos();
        if (parent == null) {
            return new AABB(pos).inflate(1.0D);
        }
        return new AABB(pos).minmax(new AABB(parent)).inflate(1.0D);
    }

    private static Vec3 parentEndpoint(IVisNetNode parent, Level level) {
        if (parent instanceof VisRelayBlockEntity relay) {
            return relayEndpoint(relay.getBlockPos(), relay.getOrientation().getOpposite());
        }
        return Vec3.atCenterOf(parent.getVisNetPos());
    }

    private static Vec3 relayEndpoint(BlockPos pos, Direction orientation) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 offset = Vec3.atLowerCornerOf(orientation.getNormal()).scale(0.05D);
        return center.subtract(offset);
    }

    private static void addBeam(PoseStack poseStack, VertexConsumer consumer, Vec3 from, Vec3 to,
            float red, float green, float blue, float alpha, float partialTick) {
        Vec3 direction = to.subtract(from).normalize();
        if (!Double.isFinite(direction.lengthSqr())) {
            return;
        }
        Vec3 up = Math.abs(direction.y) > 0.9D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 side = direction.cross(up).normalize().scale(0.055D);
        Vec3 vertical = direction.cross(side).normalize().scale(0.055D);
        float length = (float) to.distanceTo(from);
        float ticks = Minecraft.getInstance().player == null
                ? System.nanoTime() / 50_000_000.0F
                : Minecraft.getInstance().player.tickCount + partialTick;
        float v0 = -ticks * 0.2F - Mth.floor(-ticks * 0.1F);
        float v1 = length + v0;
        addBeamQuad(poseStack, consumer, from, to, side, red, green, blue, alpha, v0, v1);
        addBeamQuad(poseStack, consumer, from, to, vertical, red, green, blue, alpha, v0 + 0.33F, v1 + 0.33F);
    }

    private static void addBeamQuad(PoseStack poseStack, VertexConsumer consumer, Vec3 from, Vec3 to, Vec3 offset,
            float red, float green, float blue, float alpha, float v0, float v1) {
        Vec3 a = to.subtract(offset);
        Vec3 b = from.subtract(offset);
        Vec3 c = from.add(offset);
        Vec3 d = to.add(offset);
        addBeamVertex(poseStack, consumer, a, 1.0F, v1, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, b, 1.0F, v0, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, c, 0.0F, v0, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, d, 0.0F, v1, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, d, 0.0F, v1, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, c, 0.0F, v0, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, b, 1.0F, v0, red, green, blue, alpha);
        addBeamVertex(poseStack, consumer, a, 1.0F, v1, red, green, blue, alpha);
    }

    private static void addBeamVertex(PoseStack poseStack, VertexConsumer consumer, Vec3 point, float u, float v,
            float red, float green, float blue, float alpha) {
        consumer.addVertex(poseStack.last(), (float) point.x, (float) point.y, (float) point.z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
