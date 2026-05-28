package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class JarItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation JAR = Thaumcraft.id("textures/block/jar.png");
    private static final ResourceLocation JAR_VOID = Thaumcraft.id("textures/block/jar_void.png");
    private static final ResourceLocation BRINE = Thaumcraft.id("textures/block/jarbrine.png");
    private static final ResourceLocation BRAIN = Thaumcraft.id("textures/block/brain2.png");

    private final ModelPart jarCore = part(cube(0, 0, -5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F, 64.0F, 32.0F));
    private final ModelPart jarLid = part(cube(0, 24, -3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F, 64.0F, 32.0F),
            0.0F, -14.0F, 0.0F);
    private final ModelPart brine = part(cube(0, 0, -4.0F, -11.0F, -4.0F, 8.0F, 10.0F, 8.0F, 64.0F, 32.0F));
    private final ModelPart brainCore = part(cube(0, 0, 0.0F, 0.0F, 0.0F, 12.0F, 10.0F, 16.0F, 128.0F, 64.0F),
            -6.0F, 8.0F, -8.0F);
    private final ModelPart brainLower = part(cube(64, 0, 0.0F, 0.0F, 0.0F, 8.0F, 3.0F, 7.0F, 128.0F, 64.0F),
            -4.0F, 18.0F, 0.0F);
    private final ModelPart brainStem = part(cube(0, 32, 0.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, 128.0F, 64.0F),
            -1.0F, 18.0F, -2.0F);

    public JarItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.12F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0.0F, 0.03F, 0.0F);
            poseStack.scale(0.86F, 0.86F, 0.86F);
        }

        if (stack.is(TCItems.BRAIN_IN_A_JAR.get())) {
            renderBrine(poseStack, buffer, packedLight);
            renderBrain(poseStack, buffer, packedLight);
        } else if (stack.is(TCItems.NODE_IN_A_JAR.get())) {
            renderBrine(poseStack, buffer, packedLight);
        } else {
            EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
            if (!essentia.isEmpty()) {
                renderEssentia(essentia, poseStack, buffer, packedLight, stack.is(TCItems.VOID_JAR.get()));
            }
        }

        renderJar(stack.is(TCItems.VOID_JAR.get()) ? JAR_VOID : JAR, poseStack, buffer, packedLight);
        Aspect filter = stack.get(TCDataComponents.JAR_FILTER);
        if (filter != null) {
            renderLabel(filter, poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

    private void renderJar(ResourceLocation texture, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        this.jarCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.jarLid.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    private void renderBrine(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(BRINE));
        this.brine.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    private void renderBrain(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.78F, 0.0F);
        poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
        poseStack.scale(0.4F, 0.4F, 0.4F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BRAIN));
        this.brainCore.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.brainLower.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.brainStem.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
    }

    private static void renderEssentia(EssentiaStorage essentia, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, boolean voidJar) {
        float fill = Math.min(1.0F, Math.max(0.0F, essentia.amount() / 64.0F));
        float height = voidJar ? 10.0F : 9.0F * fill;
        if (height <= 0.0F) {
            return;
        }

        int color = 0xBB000000 | essentia.aspect().getColor();
        int light = Math.max(packedLight, LightTexture.pack(12, 12));
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(Thaumcraft.id("block/animatedglow"));
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        renderModelCuboid(poseStack, consumer, sprite, -4.0F, -1.0F - height, -4.0F, 4.0F, -1.0F, 4.0F,
                light, color);
    }

    private static void renderLabel(Aspect aspect, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.326F, -0.39F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(0.56F, 0.56F, 0.56F);
        int light = Math.max(packedLight, LightTexture.FULL_BRIGHT);

        VertexConsumer label = buffer.getBuffer(RenderType.entityTranslucent(Thaumcraft.id("textures/models/label.png")));
        renderFlatQuad(poseStack, label, -0.5F, -0.5F, 0.5F, 0.5F, light, 0xFFFFFFFF);

        poseStack.translate(0.0F, 0.0F, 0.004F);
        poseStack.scale(0.77F, 0.77F, 0.77F);
        VertexConsumer icon = buffer.getBuffer(RenderType.entityTranslucent(Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png")));
        renderFlatQuad(poseStack, icon, -0.5F, -0.5F, 0.5F, 0.5F, light, 0xFF1A1A1A);
        poseStack.popPose();
    }

    private static void renderModelCuboid(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite,
            float x1, float y1, float z1, float x2, float y2, float z2, int light, int color) {
        float s = 1.0F / 16.0F;
        x1 *= s;
        y1 *= s;
        z1 *= s;
        x2 *= s;
        y2 *= s;
        z2 *= s;

        addQuad(poseStack, consumer, sprite, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0.0F, 1.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, 0.0F, -1.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, -1.0F, light, color);
        addQuad(poseStack, consumer, sprite, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, light, color);
        addQuad(poseStack, consumer, sprite, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, -1.0F, 0.0F, 0.0F, light, color);
        addQuad(poseStack, consumer, sprite, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 1.0F, 0.0F, 0.0F, light, color);
    }

    private static void renderFlatQuad(PoseStack poseStack, VertexConsumer consumer, float x1, float y1, float x2,
            float y2, int light, int color) {
        addVertex(poseStack, consumer, x1, y1, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F, light, color);
        addVertex(poseStack, consumer, x2, y1, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, light, color);
        addVertex(poseStack, consumer, x2, y2, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F, light, color);
        addVertex(poseStack, consumer, x1, y2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, light, color);
    }

    private static void addQuad(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float x1,
            float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4,
            float z4, float normalX, float normalY, float normalZ, int light, int color) {
        addVertex(poseStack, consumer, x1, y1, z1, sprite.getU0(), sprite.getV0(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x2, y2, z2, sprite.getU1(), sprite.getV0(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x3, y3, z3, sprite.getU1(), sprite.getV1(), normalX, normalY, normalZ, light, color);
        addVertex(poseStack, consumer, x4, y4, z4, sprite.getU0(), sprite.getV1(), normalX, normalY, normalZ, light, color);
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float x, float y, float z, float u,
            float v, float normalX, float normalY, float normalZ, int light, int color) {
        consumer.addVertex(poseStack.last(), x, y, z)
                .setColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalX, normalY, normalZ);
    }

    private static ModelPart part(ModelPart.Cube cube) {
        return new ModelPart(List.of(cube), Map.of());
    }

    private static ModelPart part(ModelPart.Cube cube, float x, float y, float z) {
        ModelPart part = part(cube);
        part.setPos(x, y, z);
        return part;
    }

    private static ModelPart.Cube cube(int textureU, int textureV, float x, float y, float z,
            float width, float height, float depth, float textureWidth, float textureHeight) {
        return new ModelPart.Cube(textureU, textureV, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F,
                false, textureWidth, textureHeight, Set.of(Direction.values()));
    }

    public static JarItemRenderer create() {
        Minecraft minecraft = Minecraft.getInstance();
        return new JarItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }
}
