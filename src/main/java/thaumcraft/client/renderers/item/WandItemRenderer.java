package thaumcraft.client.renderers.item;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.WandFocusHelper;

public class WandItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation ROD_WOOD = Thaumcraft.id("textures/item/wand_rod_wood.png");
    private static final ResourceLocation CAP_IRON = Thaumcraft.id("textures/item/wand_cap_iron_model.png");
    private static final ResourceLocation FOCUS = Thaumcraft.id("textures/models/wand.png");
    private static final ResourceLocation SCRIPT = Thaumcraft.id("textures/misc/script.png");
    private static final float MODEL_SCALE = 0.0625F;
    private static final int FULL_BRIGHT = 0x00F000F0;

    private final ModelPart rod;
    private final ModelPart capTop;
    private final ModelPart capBottom;
    private final ModelPart focus;

    public WandItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        this.rod = part(cube(0, 8, -1.0F, -1.0F, -1.0F, 2.0F, 18.0F, 2.0F, 32.0F, 32.0F), 0.0F, 2.0F,
                0.0F);
        this.capTop = part(cube(0, 0, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 32.0F, 32.0F), 0.0F, 0.0F,
                0.0F);
        this.capBottom = part(cube(0, 0, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 32.0F, 32.0F), 0.0F, 20.0F,
                0.0F);
        this.focus = part(cube(0, 0, -3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, 32.0F, 32.0F), 0.0F, 0.0F,
                0.0F);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        applyTc4Transform(displayContext, poseStack);

        renderPart(this.rod, ROD_WOOD, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        renderPart(this.capTop, CAP_IRON, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        renderPart(this.capBottom, CAP_IRON, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);

        ItemFocusBasic focusItem = WandFocusHelper.getFocus(stack);
        if (focusItem != null) {
            ItemStack focusStack = WandFocusHelper.getFocusItem(stack);
            renderFocus(focusItem, focusStack, poseStack, buffer, packedOverlay);
        }

        renderRunes(poseStack, buffer, packedOverlay);

        poseStack.popPose();
    }

    private void renderFocus(ItemFocusBasic focusItem, ItemStack focusStack, PoseStack poseStack, MultiBufferSource buffer,
            int packedOverlay) {
        ResourceLocation ornament = focusItem.getOrnamentTexture(focusStack);
        if (ornament != null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.translate(-0.25F, -0.10F, 0.0275F);
            poseStack.scale(0.50F, 0.50F, 0.50F);
            renderTexturedPlane(ornament, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.translate(-0.25F, -0.10F, 0.0275F);
            poseStack.scale(0.50F, 0.50F, 0.50F);
            renderTexturedPlane(ornament, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
        }

        float alpha = 0.95F;
        ResourceLocation depth = focusItem.getFocusDepthLayerTexture(focusStack);
        if (depth != null) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -0.09F, 0.0F);
            poseStack.scale(0.16F, 0.16F, 0.16F);
            renderPart(this.focus, depth, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
            alpha = 0.60F;
        }

        int color = (((int) (alpha * 255.0F)) << 24) | (focusItem.getFocusColor(focusStack) & 0x00FFFFFF);
        poseStack.pushPose();
        poseStack.scale(0.50F, 0.50F, 0.50F);
        renderPart(this.focus, FOCUS, poseStack, buffer, FULL_BRIGHT, packedOverlay, color);
        poseStack.popPose();
    }

    private static void renderRunes(PoseStack poseStack, MultiBufferSource buffer, int packedOverlay) {
        int ticks = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount;
        for (int side = 0; side < 4; side++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * side));
            for (int rune = 0; rune < 14; rune++) {
                int frame = (rune + side * 3) & 15;
                float alpha = 0.45F + 0.20F * (float) Math.sin((ticks + frame * 5) / 10.0F);
                int color = (((int) (alpha * 255.0F)) << 24) | 0xE0A033;
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                poseStack.translate(0.36F + rune * 0.14F, -0.011F, -0.081F);
                poseStack.scale(0.12F, 0.12F, 0.12F);
                renderScriptRune(frame, poseStack, buffer, FULL_BRIGHT, packedOverlay, color);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }

    private static void applyTc4Transform(ItemDisplayContext displayContext, PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(66.0F));
            poseStack.translate(0.0F, 0.60F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.scale(0.95F, 0.95F, 0.95F);
        } else if (displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED) {
            poseStack.translate(0.0F, 0.55F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.scale(0.75F, 0.75F, 0.75F);
        } else if (displayContext.firstPerson()) {
            poseStack.translate(0.0F, 0.85F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-35.0F));
            poseStack.scale(0.95F, 1.05F, 0.95F);
        } else {
            poseStack.translate(0.0F, 0.85F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-25.0F));
        }
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }

    private static void renderPart(ModelPart part, ResourceLocation texture, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, int color) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        part.render(poseStack, consumer, packedLight, packedOverlay, color);
    }

    private static void renderTexturedPlane(ResourceLocation texture, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, int color) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        addPlaneVertex(consumer, poseStack, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, packedLight, packedOverlay, color);
    }

    private static void renderScriptRune(int rune, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            int packedOverlay, int color) {
        float minU = rune / 16.0F;
        float maxU = minU + 1.0F / 16.0F;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(SCRIPT));
        addPlaneVertex(consumer, poseStack, -0.50F, 0.50F, 0.0F, maxU, 1.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.50F, 0.50F, 0.0F, maxU, 0.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.50F, -0.50F, 0.0F, minU, 0.0F, packedLight, packedOverlay, color);
        addPlaneVertex(consumer, poseStack, -0.50F, -0.50F, 0.0F, minU, 1.0F, packedLight, packedOverlay, color);
    }

    private static void addPlaneVertex(VertexConsumer consumer, PoseStack poseStack, float x, float y, float z, float u,
            float v, int packedLight, int packedOverlay, int color) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private static ModelPart part(ModelPart.Cube cube, float x, float y, float z) {
        ModelPart part = new ModelPart(List.of(cube), Map.of());
        part.setPos(x, y, z);
        return part;
    }

    private static ModelPart.Cube cube(int textureU, int textureV, float x, float y, float z, float sizeX, float sizeY,
            float sizeZ, float textureWidth, float textureHeight) {
        return new ModelPart.Cube(textureU, textureV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, true,
                textureWidth, textureHeight, Set.of(Direction.values()));
    }

    public static WandItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new WandItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
