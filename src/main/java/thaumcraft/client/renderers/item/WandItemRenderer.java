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
import thaumcraft.common.items.wands.WandFocusHelper;

public class WandItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation ROD_WOOD = Thaumcraft.id("textures/item/wand_rod_wood.png");
    private static final ResourceLocation CAP_IRON = Thaumcraft.id("textures/item/wand_cap_iron_model.png");
    private static final ResourceLocation FOCUS = Thaumcraft.id("textures/models/wand.png");

    private static final float MODEL_SCALE = 1.0F / 16.0F;
    private static final int FULL_BRIGHT = 0x00F000F0;

    private final ModelPart rod;
    private final ModelPart capTop;
    private final ModelPart capBottom;
    private final ModelPart focus;

    public WandItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);

        /*
         * Модель в локальных pixel-координатах.
         *
         * ВАЖНО:
         * origin находится около точки хвата, а не в центре предмета.
         * Это главная разница с предыдущей версией.
         */
        this.rod = part(
                cube(0, 8, -1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, 32.0F, 32.0F),
                0.0F, 0.0F, 0.0F
        );

        this.capBottom = part(
                cube(0, 0, -1.25F, -11.25F, -1.25F, 2.5F, 2.5F, 2.5F, 32.0F, 32.0F),
                0.0F, 0.0F, 0.0F
        );

        this.capTop = part(
                cube(0, 0, -1.25F, 8.75F, -1.25F, 2.5F, 2.5F, 2.5F, 32.0F, 32.0F),
                0.0F, 0.0F, 0.0F
        );

        this.focus = part(
                cube(0, 0, -3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 32.0F, 32.0F),
                0.0F, 12.5F, 0.0F
        );
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        applyWandTransform(displayContext, poseStack);

        renderPart(this.rod, ROD_WOOD, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        renderPart(this.capBottom, CAP_IRON, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        renderPart(this.capTop, CAP_IRON, poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);

        ItemFocusBasic focusItem = WandFocusHelper.getFocus(stack);
        if (focusItem != null) {
            ItemStack focusStack = WandFocusHelper.getFocusItem(stack);
            renderFocus(focusItem, focusStack, poseStack, buffer, packedOverlay);
        }

        /*
         * Временно НЕ рендерим runes.
         *
         * На твоём скрине руны оторваны от палочки и рисуются огромным вертикальным шлейфом.
         * Сначала надо добиться правильного положения rod + caps в руке.
         * После этого руны лучше вернуть отдельным patch'ем уже как quads на поверхности rod.
         */
        // renderRunes(poseStack, buffer, packedOverlay);

        poseStack.popPose();
    }

    private void renderFocus(ItemFocusBasic focusItem, ItemStack focusStack, PoseStack poseStack,
            MultiBufferSource buffer, int packedOverlay) {
        float alpha = 0.95F;

        ResourceLocation depth = focusItem.getFocusDepthLayerTexture(focusStack);
        if (depth != null) {
            poseStack.pushPose();
            renderPart(this.focus, depth, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
            alpha = 0.60F;
        }

        int color = (((int) (alpha * 255.0F)) << 24) | (focusItem.getFocusColor(focusStack) & 0x00FFFFFF);

        poseStack.pushPose();
        renderPart(this.focus, FOCUS, poseStack, buffer, FULL_BRIGHT, packedOverlay, color);
        poseStack.popPose();

        ResourceLocation ornament = focusItem.getOrnamentTexture(focusStack);
        if (ornament != null) {
            poseStack.pushPose();
            poseStack.translate(-3.0F, 9.5F, -0.04F);
            poseStack.scale(6.0F, 6.0F, 6.0F);
            renderTexturedPlane(ornament, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.translate(-3.0F, 9.5F, -0.04F);
            poseStack.scale(6.0F, 6.0F, 6.0F);
            renderTexturedPlane(ornament, poseStack, buffer, FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
        }
    }

    private static void applyWandTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case GUI -> {
                poseStack.translate(0.50F, 0.50F, 0.00F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(25.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-20.0F));
                poseStack.scale(1.25F, 1.25F, 1.25F);
            }

            case GROUND -> {
                poseStack.translate(0.50F, 0.12F, 0.50F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(25.0F));
                poseStack.scale(0.80F, 0.80F, 0.80F);
            }

            case FIXED -> {
                poseStack.translate(0.50F, 0.50F, 0.08F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
                poseStack.scale(1.00F, 1.00F, 1.00F);
            }

            case FIRST_PERSON_RIGHT_HAND -> {
                /*
                 * Главная настройка для твоего текущего скрина.
                 * Палочка должна быть крупнее и ближе к центру руки.
                 */
                poseStack.translate(0.56F, 0.42F, -0.34F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-58.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(34.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-28.0F));
                poseStack.scale(1.55F, 1.55F, 1.55F);
            }

            case FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.44F, 0.42F, -0.34F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-58.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-34.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(28.0F));
                poseStack.scale(1.55F, 1.55F, 1.55F);
            }

            case THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.50F, 0.22F, 0.08F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-92.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(42.0F));
                poseStack.scale(0.95F, 0.95F, 0.95F);
            }

            case THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.50F, 0.22F, 0.08F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-92.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-42.0F));
                poseStack.scale(0.95F, 0.95F, 0.95F);
            }

            case HEAD -> {
                poseStack.translate(0.50F, 0.40F, 0.50F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
                poseStack.scale(0.90F, 0.90F, 0.90F);
            }

            default -> {
                poseStack.translate(0.50F, 0.50F, 0.50F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
                poseStack.scale(0.90F, 0.90F, 0.90F);
            }
        }

        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
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