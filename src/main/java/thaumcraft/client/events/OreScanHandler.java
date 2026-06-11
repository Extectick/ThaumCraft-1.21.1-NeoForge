package thaumcraft.client.events;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT)
public class OreScanHandler {

    public static final TagKey<Block> ORES_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:ores"));

    private static @Nullable BlockPos scanCenter;
    private static long scanExpireTime;
    private static int scanRange;
    private static final int[][][] scannedBlocks = new int[17][17][17];

    public static void startScan(Player player, BlockPos center, int range) {
        scanCenter = center;
        scanRange = Math.min(range, 8);
        scanExpireTime = System.currentTimeMillis() + 5000L;

        Level level = player.level();
        for (int xx = -scanRange; xx <= scanRange; xx++) {
            for (int yy = -scanRange; yy <= scanRange; yy++) {
                for (int zz = -scanRange; zz <= scanRange; zz++) {
                    int value = -1;
                    BlockPos pos = center.offset(xx, yy, zz);
                    BlockState state = level.getBlockState(pos);
                    FluidState fluid = state.getFluidState();

                    if (!state.isAir() && !state.is(Blocks.BEDROCK)) {
                        if (!fluid.isEmpty() && fluid.isSource()) {
                            if (fluid.is(FluidTags.LAVA)) {
                                value = -10;
                            } else if (fluid.is(FluidTags.WATER)) {
                                value = -5;
                            }
                        } else if (state.is(ORES_TAG)) {
                            value = 0; // ore
                        }
                    }
                    scannedBlocks[xx + 8][yy + 8][zz + 8] = value;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (scanCenter == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now > scanExpireTime) {
            scanCenter = null;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        long elapsed = now - (scanExpireTime - 5000L);

        Vec3 cam = event.getCamera().getPosition();
        var poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var mat = poseStack.last();

        for (int xx = -8; xx <= 8; xx++) {
            for (int yy = -8; yy <= 8; yy++) {
                for (int zz = -8; zz <= 8; zz++) {
                    int value = scannedBlocks[xx + 8][yy + 8][zz + 8];
                    if (value == -1) {
                        continue;
                    }

                    float alpha;
                    if (elapsed > 4750L) {
                        alpha = 1.0F - (elapsed - 4750L) / 250.0F;
                    } else if (elapsed < 1500L) {
                        alpha = elapsed / 1500.0F;
                    } else {
                        alpha = 1.0F;
                    }
                    float dist = 1.0F - (xx * xx + yy * yy + zz * zz) / 128.0F;
                    alpha *= Mth.clamp(dist, 0.0F, 1.0F);
                    if (alpha <= 0.0F) {
                        continue;
                    }

                    float bx = scanCenter.getX() + xx;
                    float by = scanCenter.getY() + yy;
                    float bz = scanCenter.getZ() + zz;

                    if (value == -5) {
                        drawSpecialOverlay(buf, mat, bx, by, bz, 0.3F, 0.5F, 0.9F, alpha);
                    } else if (value == -10) {
                        drawSpecialOverlay(buf, mat, bx, by, bz, 1.0F, 0.4F, 0.1F, alpha);
                    } else if (value >= 0) {
                        float pulse = Mth.sin(elapsed / 200.0F + xx + yy + zz) * 0.3F + 0.7F;
                        float r = 0.2F * pulse;
                        float g = 0.9F * pulse;
                        float b = 0.3F * pulse;
                        drawOreOverlay(buf, mat, bx, by, bz, r, g, b, alpha);
                    }
                }
            }
        }

        MeshData mesh = buf.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();

        poseStack.popPose();
    }

    private static void drawSpecialOverlay(BufferBuilder buf, com.mojang.blaze3d.vertex.PoseStack.Pose mat, float x, float y, float z, float r, float g,
                                           float b, float a) {
        float x1 = x - 0.001F, y1 = y - 0.001F, z1 = z - 0.001F;
        float x2 = x + 1.001F, y2 = y + 1.001F, z2 = z + 1.001F;
        addQuad(buf, mat, r, g, b, a * 0.5F, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        addQuad(buf, mat, r, g, b, a * 0.5F, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1);
        addQuad(buf, mat, r, g, b, a * 0.5F, x1, y2, z1, x2, y2, z1, x2, y1, z1, x1, y1, z1);
        addQuad(buf, mat, r, g, b, a * 0.5F, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        addQuad(buf, mat, r, g, b, a * 0.5F, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1);
        addQuad(buf, mat, r, g, b, a * 0.5F, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
    }

    private static void drawOreOverlay(BufferBuilder buf, com.mojang.blaze3d.vertex.PoseStack.Pose mat, float x, float y, float z, float r, float g, float b,
                                       float a) {
        float inset = 0.05F;
        float x1 = x + inset, y1 = y + inset, z1 = z + inset;
        float x2 = x + 1.0F - inset, y2 = y + 1.0F - inset, z2 = z + 1.0F - inset;

        float fa = a * 0.4F;
        addQuad(buf, mat, r, g, b, fa, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        addQuad(buf, mat, r, g, b, fa, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1);
        addQuad(buf, mat, r, g, b, fa, x1, y2, z1, x2, y2, z1, x2, y1, z1, x1, y1, z1);
        addQuad(buf, mat, r, g, b, fa, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        addQuad(buf, mat, r, g, b, fa, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1);
        addQuad(buf, mat, r, g, b, fa, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
    }

    private static void addQuad(BufferBuilder buf, com.mojang.blaze3d.vertex.PoseStack.Pose mat, float r, float g, float b, float a, float x1, float y1,
                                float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        buf.addVertex(mat.pose(), x1, y1, z1).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x2, y2, z2).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x3, y3, z3).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x4, y4, z4).setColor(r, g, b, a);
    }
}
