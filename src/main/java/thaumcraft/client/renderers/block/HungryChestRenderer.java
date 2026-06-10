package thaumcraft.client.renderers.block;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.HungryChestBlockEntity;

import java.io.IOException;
import java.util.Optional;

public class HungryChestRenderer implements BlockEntityRenderer<HungryChestBlockEntity> {

    private static final ResourceLocation ORIGINAL_TEXTURE = Thaumcraft.id("textures/block/hungry_chest.png");
    private static final ResourceLocation SWAPPED_TEXTURE = Thaumcraft.id("textures/block/hungry_chest_swapped");

    private final ModelPart lid;
    private final ModelPart lock;
    private final ModelPart bottom;
    private boolean textureSwapped = false;

    public HungryChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }

    private void ensureSwappedTexture() {
        if (textureSwapped) {
            return;
        }
        textureSwapped = true;

        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(ORIGINAL_TEXTURE);
            if (res.isPresent()) {
                NativeImage img = NativeImage.read(res.get().open());

                swapRegions(img, 14, 19, 28, 19, 14, 14);
                swapRegions(img, 14, 0, 28, 0, 14, 14);

                DynamicTexture dynamic = new DynamicTexture(img);
                Minecraft.getInstance().getTextureManager().register(SWAPPED_TEXTURE, dynamic);
            }
        } catch (IOException e) {
            thaumcraft.Thaumcraft.LOGGER.error("Failed to load hungry chest texture for swap", e);
        }
    }

    private static void swapRegions(NativeImage img, int x1, int y1, int x2, int y2, int w, int h) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = img.getPixelRGBA(x1 + x, y1 + y);
                int b = img.getPixelRGBA(x2 + x, y2 + y);
                img.setPixelRGBA(x1 + x, y1 + y, b);
                img.setPixelRGBA(x2 + x, y2 + y, a);
            }
        }
    }

    @Override
    public void render(HungryChestBlockEntity chest, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        ensureSwappedTexture();

        poseStack.pushPose();

        // Vanilla ChestRenderer transforms
        float facingAngle = chest.getBlockState().getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH).toYRot();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facingAngle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        // TC4-style lid animation
        float openness = chest.getOpenNess(partialTick);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        this.lid.xRot = -(openness * ((float) Math.PI / 2.0F));
        this.lock.xRot = this.lid.xRot;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(SWAPPED_TEXTURE));
        this.lid.render(poseStack, consumer, packedLight, packedOverlay);
        this.lock.render(poseStack, consumer, packedLight, packedOverlay);
        this.bottom.render(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}


