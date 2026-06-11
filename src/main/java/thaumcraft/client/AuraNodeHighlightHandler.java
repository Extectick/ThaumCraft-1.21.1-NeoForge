package thaumcraft.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class AuraNodeHighlightHandler {
    private AuraNodeHighlightHandler() {}

    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        BlockPos pos = event.getTarget().getBlockPos();
        if (mc.level.getBlockState(pos).is(TCBlocks.AURA_NODE.get())) {
            event.setCanceled(true);
        }
        
        boolean hasGoggles = mc.player.getItemBySlot(EquipmentSlot.HEAD).is(TCItems.GOGGLES.get());
        boolean hasThaumometer = mc.player.getMainHandItem().is(TCItems.THAUMOMETER.get()) || mc.player.getOffhandItem().is(TCItems.THAUMOMETER.get());
        
        if (hasGoggles || hasThaumometer) {
            BlockEntity be = mc.level.getBlockEntity(pos);
            AspectList aspects = null;
            float renderYOffset = 0.0F;

            if (be instanceof AuraNodeBlockEntity node) {
                aspects = node.getAspects();
            } else if (be instanceof IEssentiaContainer container) {
                EssentiaStorage storage = container.getEssentia();
                if (storage != null && !storage.isEmpty()) {
                    aspects = new AspectList().add(storage.aspect(), storage.amount());
                    renderYOffset = 0.5F;
                }
            }

            if (aspects != null && !aspects.isEmpty()) {
                renderAspects(mc.player, event.getPoseStack(), event.getMultiBufferSource(), event.getCamera(), pos, aspects, event.getTarget().getDirection(), renderYOffset);
            }
        }
    }

    private static void renderAspects(Player player, PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, BlockPos pos, AspectList aspectList, Direction dir, float renderYOffset) {
        float tagscale = 0.3F;
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;
        
        boolean spaceAbove = player.level().isEmptyBlock(pos.above());
        Direction renderDir = spaceAbove ? Direction.UP : dir;

        double x = pos.getX();
        double y = pos.getY() + (spaceAbove ? 0.4F : 0.0F) + renderYOffset;
        double z = pos.getZ();

        int rowsize = 5;
        int current = 0;
        float shifty = 0.0F;
        int left = aspectList.size();

        for (Aspect aspect : aspectList.getAspectsSorted()) {
            int aspectColor = aspect.getColor();
            ResourceLocation aspectTex = Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png");
            
            int div = Math.min(left, rowsize);
            if (current >= rowsize) {
                current = 0;
                shifty -= tagscale * 1.05F;
                left -= rowsize;
                if (left < rowsize) {
                    div = left % rowsize;
                }
            }

            float shift = ((float) current - (float) div / 2.0F + 0.5F) * tagscale * 4.0F;
            shift *= tagscale;

            poseStack.pushPose();
            double renderX = x - camX + 0.5D + (tagscale * 2.0F * (float) renderDir.getStepX());
            double renderY = y - camY - shifty + 0.5D + (tagscale * 2.0F * (float) renderDir.getStepY());
            double renderZ = z - camZ + 0.5D + (tagscale * 2.0F * (float) renderDir.getStepZ());
            poseStack.translate(renderX, renderY, renderZ);
            
            float xd = (float) (camX - (x + 0.5D));
            float zd = (float) (camZ - (z + 0.5D));
            float rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
            
            poseStack.mulPose(Axis.YP.rotationDegrees(rotYaw + 180.0F));
            poseStack.translate(shift, 0.0F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(tagscale, tagscale, tagscale);

            VertexConsumer iconConsumer = bufferSource.getBuffer(getOverlayRenderType(aspectTex));
            float r = ((aspectColor >> 16) & 0xFF) / 255.0F;
            float g = ((aspectColor >> 8) & 0xFF) / 255.0F;
            float b = (aspectColor & 0xFF) / 255.0F;
            renderCenteredQuad(poseStack, iconConsumer, r, g, b, 0.75F, 15728880);

            int amount = aspectList.getAmount(aspect);
            if (amount >= 0) {
                String am = String.valueOf(amount);
                Font font = Minecraft.getInstance().font;
                int sw = font.width(am);

                poseStack.pushPose();
                poseStack.scale(0.04F, 0.04F, 0.04F);
                poseStack.translate(0.0F, 6.0F, 0.0F);
                font.drawInBatch(am, 14.0F - sw, 1.0F, 0x111111, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
                poseStack.translate(0.0F, 0.0F, -0.5F);
                font.drawInBatch(am, 13.0F - sw, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
                poseStack.popPose();
            }

            poseStack.popPose();
            current++;
        }
    }

    private static void renderCenteredQuad(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, int packedLight) {
        Matrix4f matrix = poseStack.last().pose();
        consumer.addVertex(matrix, -0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setLight(packedLight);
        consumer.addVertex(matrix, 0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setLight(packedLight);
        consumer.addVertex(matrix, 0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setLight(packedLight);
        consumer.addVertex(matrix, -0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setLight(packedLight);
    }

    private static RenderType getOverlayRenderType(ResourceLocation texture) {
        return RenderType.create("aspect_overlay_icon", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false));
    }
}
