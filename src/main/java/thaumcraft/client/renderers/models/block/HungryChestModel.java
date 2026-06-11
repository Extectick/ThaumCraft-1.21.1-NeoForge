package thaumcraft.client.renderers.models.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

import javax.annotation.ParametersAreNonnullByDefault;

public class HungryChestModel extends Model {

    private final ModelPart lid;
    private final ModelPart lock;
    private final ModelPart bottom;

    public HungryChestModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
        this.bottom = root.getChild("bottom");
    }

    public static LayerDefinition createLayerDefinition() {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);

        root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));

        root.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setLidAngle(float angle) {
        this.lid.xRot = angle;
        this.lock.xRot = angle;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.lid.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.lock.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}

