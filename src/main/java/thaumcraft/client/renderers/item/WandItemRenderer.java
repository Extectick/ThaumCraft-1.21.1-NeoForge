package thaumcraft.client.renderers.item;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.items.wands.WandParts;

public class WandItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation FOCUS = Thaumcraft.id("textures/models/wand.png");
    private static final ResourceLocation SCRIPT = Thaumcraft.id("textures/misc/script.png");

    private static final int FULL_BRIGHT = 0x00F000F0;
    private static final float MODEL_TEXTURE_WIDTH = 64.0F;
    private static final float MODEL_TEXTURE_HEIGHT = 32.0F;
    private static final float CAP_XZ_SCALE = 1.2F;

    private final ModelPart rod;
    private final ModelPart capTop;
    private final ModelPart capBottom;
    private final ModelPart focus;

    public WandItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);

        this.rod = part(
                cube(0, 8, -1.0F, -1.0F, -1.0F, 2.0F, 18.0F, 2.0F, MODEL_TEXTURE_WIDTH, MODEL_TEXTURE_HEIGHT),
                0.0F, 2.0F, 0.0F
        );

        this.capTop = part(
                cube(0, 0, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, MODEL_TEXTURE_WIDTH, MODEL_TEXTURE_HEIGHT),
                0.0F, 0.0F, 0.0F
        );

        this.capBottom = part(
                cube(0, 0, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, MODEL_TEXTURE_WIDTH, MODEL_TEXTURE_HEIGHT),
                0.0F, 20.0F, 0.0F
        );

        this.focus = part(
                cube(0, 0, -3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, MODEL_TEXTURE_WIDTH, MODEL_TEXTURE_HEIGHT),
                0.0F, 0.0F, 0.0F
        );
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        centerModelInItemSpace(poseStack);

        boolean staff = isStaff(stack);
        boolean sceptre = isSceptre(stack);
        WandParts.Rod rodData = rodData(stack);
        ResourceLocation rodTexture = rodTexture(rodData);
        ResourceLocation capTexture = capTexture(stack);

        if (staff) {
            poseStack.translate(0.0F, 0.2F, 0.0F);
        }

        poseStack.pushPose();
        if (staff) {
            poseStack.translate(0.0F, -0.1F, 0.0F);
            poseStack.scale(1.2F, 2.0F, 1.2F);
        }
        renderPart(this.rod, rodTexture, poseStack, buffer, rodData.glowing() ? FULL_BRIGHT : packedLight,
                packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        if (staff) {
            poseStack.scale(1.3F, 1.1F, 1.3F);
        } else {
            poseStack.scale(CAP_XZ_SCALE, 1.0F, CAP_XZ_SCALE);
        }
        renderTopCap(capTexture, sceptre, poseStack, buffer, packedLight, packedOverlay);
        if (staff) {
            poseStack.translate(0.0F, 0.225F, 0.0F);
            poseStack.pushPose();
            poseStack.scale(1.0F, 0.66F, 1.0F);
            renderPart(this.capTop, capTexture, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
            poseStack.translate(0.0F, 0.65F, 0.0F);
        }
        renderPart(this.capBottom, capTexture, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();

        ItemFocusBasic focusItem = WandFocusHelper.getFocus(stack);
        if (focusItem != null) {
            ItemStack focusStack = WandFocusHelper.getFocusItem(stack);
            renderFocus(focusItem, focusStack, poseStack, buffer, packedOverlay);
        }

        renderRunes(stack, staff, sceptre, poseStack, buffer, packedOverlay);

        poseStack.popPose();
    }

    private void renderTopCap(ResourceLocation capTexture, boolean sceptre, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!sceptre) {
            renderPart(this.capTop, capTexture, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
            return;
        }

        poseStack.pushPose();
        poseStack.scale(1.3F, 1.3F, 1.3F);
        renderPart(this.capTop, capTexture, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.3F, 0.0F);
        poseStack.scale(1.0F, 0.66F, 1.0F);
        renderPart(this.capTop, capTexture, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();
    }

    private void renderFocus(ItemFocusBasic focusItem, ItemStack focusStack, PoseStack poseStack,
            MultiBufferSource buffer, int packedOverlay) {
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
    }

    private void renderRunes(ItemStack stack, boolean staff, boolean sceptre, PoseStack poseStack, MultiBufferSource buffer,
            int packedOverlay) {
        int ticks = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0;
        if (sceptre) {
            for (int rot = 0; rot < 10; rot++) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(36.0F * rot + ticks));
                drawRune(0.16D, -0.01D, -0.125D, rot, ticks, poseStack, buffer, packedOverlay);
                poseStack.popPose();
            }
        }

        if (staff || hasRunes(stack)) {
            poseStack.pushPose();
            for (int rot = 0; rot < 4; rot++) {
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                for (int a = 0; a < 14; a++) {
                    int rune = (a + rot * 3) % 16;
                    drawRune(0.36D + a * 0.14D, -0.01D, -0.08D, rune, ticks, poseStack, buffer, packedOverlay);
                }
            }
            poseStack.popPose();
        }
    }

    private static void drawRune(double x, double y, double z, int rune, int ticks, PoseStack poseStack,
            MultiBufferSource buffer, int packedOverlay) {
        float red = (float) Math.sin((ticks + rune * 5) / 5.0F) * 0.1F + 0.88F;
        float green = (float) Math.sin((ticks + rune * 5) / 7.0F) * 0.1F + 0.63F;
        float pulse = (float) Math.sin((ticks + rune * 5) / 10.0F) * 0.2F;
        float alpha = pulse + 0.6F;
        int color = color(red, green, 0.2F, alpha);
        float minU = 0.0625F * rune;
        float maxU = minU + 0.0625F;
        float grow = pulse / 40.0F;

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.translate(x, y, z);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(SCRIPT));
        addPlaneVertex(consumer, poseStack, -0.06F - grow, 0.06F + grow, 0.0F, maxU, 1.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.06F + grow, 0.06F + grow, 0.0F, maxU, 0.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.06F + grow, -0.06F - grow, 0.0F, minU, 0.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, -0.06F - grow, -0.06F - grow, 0.0F, minU, 1.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, -0.06F - grow, -0.06F - grow, 0.0F, minU, 1.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.06F + grow, -0.06F - grow, 0.0F, minU, 0.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, 0.06F + grow, 0.06F + grow, 0.0F, maxU, 0.0F, FULL_BRIGHT,
                packedOverlay, color);
        addPlaneVertex(consumer, poseStack, -0.06F - grow, 0.06F + grow, 0.0F, maxU, 1.0F, FULL_BRIGHT,
                packedOverlay, color);
        poseStack.popPose();
    }

    private static void centerModelInItemSpace(PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    }

    private static boolean isStaff(ItemStack stack) {
        return stack.getItem() instanceof WandCastingItem wand && wand.isStaff(stack);
    }

    private static boolean isSceptre(ItemStack stack) {
        return stack.getItem() instanceof WandCastingItem wand && wand.isSceptre(stack);
    }

    private static boolean hasRunes(ItemStack stack) {
        return stack.getItem() instanceof WandCastingItem wand && wand.hasRunes(stack);
    }

    private static ResourceLocation rodTexture(ItemStack stack) {
        return rodTexture(rodData(stack));
    }

    private static ResourceLocation rodTexture(WandParts.Rod rod) {
        return Thaumcraft.id("textures/models/wand_rod_" + rod.textureTag() + ".png");
    }

    private static WandParts.Rod rodData(ItemStack stack) {
        String rod = stack.getItem() instanceof WandCastingItem wand ? wand.getRod(stack) : WandCastingItem.ROD_WOOD;
        return WandParts.rod(rod);
    }

    private static ResourceLocation capTexture(ItemStack stack) {
        String cap = stack.getItem() instanceof WandCastingItem wand ? wand.getCap(stack) : WandCastingItem.CAP_IRON;
        return Thaumcraft.id("textures/models/wand_cap_" + cap + ".png");
    }

    private static void renderPart(ModelPart part, ResourceLocation texture, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay, int color) {
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

    private static void addPlaneVertex(VertexConsumer consumer, PoseStack poseStack, float x, float y, float z,
            float u, float v, int packedLight, int packedOverlay, int color) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private static int color(float red, float green, float blue, float alpha) {
        int a = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));
        int r = Math.max(0, Math.min(255, (int) (red * 255.0F)));
        int g = Math.max(0, Math.min(255, (int) (green * 255.0F)));
        int b = Math.max(0, Math.min(255, (int) (blue * 255.0F)));
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static ModelPart part(ModelPart.Cube cube, float x, float y, float z) {
        ModelPart part = new ModelPart(List.of(cube), Map.of());
        part.setPos(x, y, z);
        return part;
    }

    private static ModelPart.Cube cube(int textureU, int textureV, float x, float y, float z,
            float sizeX, float sizeY, float sizeZ, float textureWidth, float textureHeight) {
        return new ModelPart.Cube(
                textureU,
                textureV,
                x,
                y,
                z,
                sizeX,
                sizeY,
                sizeZ,
                0.0F,
                0.0F,
                0.0F,
                false,
                textureWidth,
                textureHeight,
                Set.of(Direction.values())
        );
    }

    public static WandItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new WandItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
