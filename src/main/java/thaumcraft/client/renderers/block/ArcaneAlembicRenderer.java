package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.ArcaneAlembicBlockEntity;
import thaumcraft.common.registry.TCBlocks;

public class ArcaneAlembicRenderer implements BlockEntityRenderer<ArcaneAlembicBlockEntity> {
    private static final ResourceLocation MODEL = Thaumcraft.id("models/block/alembic.obj");
    private static final ITextureRenderTypeLookup RENDER_TYPE = RenderType::entityCutoutNoCull;
    private static final Matrix4f HIDDEN = new Matrix4f().scale(0.0F);
    private static CompositeRenderable model;

    public ArcaneAlembicRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ArcaneAlembicBlockEntity alembic, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation(alembic.getFacing())));
        getModel().render(poseStack, bufferSource, RENDER_TYPE, packedLight, OverlayTexture.NO_OVERLAY, partialTick,
                transforms(alembic));
        poseStack.popPose();
    }

    public static CompositeRenderable getModel() {
        if (model == null) {
            ObjModel.ModelSettings settings = new ObjModel.ModelSettings(MODEL, false, true, true, true, null);
            model = ObjLoader.INSTANCE.loadModel(settings).bakeRenderable(StandaloneGeometryBakingContext.create(MODEL));
        }
        return model;
    }

    private static CompositeRenderable.Transforms transforms(ArcaneAlembicBlockEntity alembic) {
        boolean aboveFurnace = isAboveFurnace(alembic);
        boolean aboveAlembic = isAboveAlembic(alembic);
        ImmutableMap.Builder<String, Matrix4f> hidden = ImmutableMap.builder();
        if (!aboveFurnace && !aboveAlembic) {
            hidden.put("TubeMain", HIDDEN);
            hidden.put("TubeSmall", HIDDEN);
        } else if (aboveFurnace) {
            hidden.put("TubeSmall", HIDDEN);
        } else {
            hidden.put("Legs", HIDDEN);
        }
        return CompositeRenderable.Transforms.of(hidden.build());
    }

    public static CompositeRenderable.Transforms itemTransforms() {
        return CompositeRenderable.Transforms.of(ImmutableMap.of("TubeMain", HIDDEN, "TubeSmall", HIDDEN));
    }

    private static boolean isAboveFurnace(ArcaneAlembicBlockEntity alembic) {
        Level level = alembic.getLevel();
        if (level == null) {
            return false;
        }
        BlockState below = level.getBlockState(alembic.getBlockPos().below());
        return below.is(TCBlocks.ALCHEMICAL_FURNACE.get());
    }

    private static boolean isAboveAlembic(ArcaneAlembicBlockEntity alembic) {
        Level level = alembic.getLevel();
        if (level == null) {
            return false;
        }
        BlockState below = level.getBlockState(alembic.getBlockPos().below());
        return below.is(TCBlocks.ARCANE_ALEMBIC.get());
    }

    private static float rotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 270.0F;
            case SOUTH -> 90.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }
}
