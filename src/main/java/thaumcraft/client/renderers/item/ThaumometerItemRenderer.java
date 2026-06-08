package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import thaumcraft.Thaumcraft;

public class ThaumometerItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL = Thaumcraft.id("models/item/thaumometer.obj");
    private static final ResourceLocation SCREEN = Thaumcraft.id("textures/models/scanscreen.png");
    private static CompositeRenderable model;

    public ThaumometerItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        getModel().render(poseStack, buffer, RenderType::entityCutoutNoCull, packedLight,
                OverlayTexture.NO_OVERLAY, 0.0F, CompositeRenderable.Transforms.EMPTY);
        renderScreen(poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    private static CompositeRenderable getModel() {
        if (model == null) {
            ObjModel.ModelSettings settings = new ObjModel.ModelSettings(MODEL, false, true, true, true, null);
            model = ObjLoader.INSTANCE.loadModel(settings).bakeRenderable(StandaloneGeometryBakingContext.create(MODEL));
        }
        return model;
    }

    private static void renderScreen(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Minecraft minecraft = Minecraft.getInstance();
        int ticks = minecraft.player == null ? 0 : minecraft.player.tickCount;
        int alpha = (int)(190.0F + Math.sin(ticks) * 10.0F + 10.0F);
        int light = Math.max(packedLight, LightTexture.pack(12, 12));

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.111F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(SCREEN));
        float size = 2.5F;
        float half = size / 2.0F;
        addVertex(poseStack, consumer, -half, -half, 0.0F, 0.0F, 1.0F, light, alpha);
        addVertex(poseStack, consumer, half, -half, 0.0F, 1.0F, 1.0F, light, alpha);
        addVertex(poseStack, consumer, half, half, 0.0F, 1.0F, 0.0F, light, alpha);
        addVertex(poseStack, consumer, -half, half, 0.0F, 0.0F, 0.0F, light, alpha);
        poseStack.popPose();
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float z,
            float u, float v, int light, int alpha) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor(255, 255, 255, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 0.0F, -1.0F);
    }

    public static ThaumometerItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new ThaumometerItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
