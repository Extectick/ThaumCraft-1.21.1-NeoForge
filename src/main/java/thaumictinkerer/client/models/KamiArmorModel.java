package thaumictinkerer.client.models;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

public class KamiArmorModel extends HumanoidModel<LivingEntity> {

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public KamiArmorModel(ModelPart root) {
        super(root);
        this.rightWing = root.getChild("body").getChild("right_wing");
        this.leftWing = root.getChild("body").getChild("left_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.getChild("body");

        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-15.0F, -2.0F, 0.0F, 15.0F, 24.0F, 0.0F),
                PartPose.offsetAndRotation(-2.0F, 2.0F, 3.0F, 0.0F, 0.5F, 0.0F));

        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(0, 32).mirror().addBox(0.0F, -2.0F, 0.0F, 15.0F, 24.0F, 0.0F),
                PartPose.offsetAndRotation(2.0F, 2.0F, 3.0F, 0.0F, -0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity.isFallFlying() || !entity.onGround()) {
            this.rightWing.yRot = 0.2F + Mth.cos(ageInTicks * 0.4F) * 0.2F;
            this.leftWing.yRot = -0.2F - Mth.cos(ageInTicks * 0.4F) * 0.2F;
            this.rightWing.zRot = 0.2F;
            this.leftWing.zRot = -0.2F;
        } else {
            this.rightWing.yRot = 0.5F + Mth.cos(ageInTicks * 0.05F) * 0.1F;
            this.leftWing.yRot = -0.5F - Mth.cos(ageInTicks * 0.05F) * 0.1F;
            this.rightWing.zRot = 0.0F;
            this.leftWing.zRot = 0.0F;
        }
    }
}

