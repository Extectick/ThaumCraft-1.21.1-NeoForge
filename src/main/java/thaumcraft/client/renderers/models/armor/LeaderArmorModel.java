package thaumcraft.client.renderers.models.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class LeaderArmorModel extends HumanoidModel<LivingEntity> {

    public LeaderArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 1.0F);

        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        PartDefinition body = root.getChild("body");
        PartDefinition rightArm = root.getChild("right_arm");
        PartDefinition leftArm = root.getChild("left_arm");
        PartDefinition rightLeg = root.getChild("right_leg");
        PartDefinition leftLeg = root.getChild("left_leg");

        // --- Head parts ---
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(41, 8).addBox(-4.5F, -9.0F, -4.5F, 9, 9, 9), PartPose.ZERO);

        head.addOrReplaceChild("collar_f", CubeListBuilder.create().texOffs(17, 31).addBox(-4.5F, -1.5F, -3.0F, 9, 4, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.2268928F, 0.0F, 0.0F));

        head.addOrReplaceChild("collar_b", CubeListBuilder.create().texOffs(17, 26).addBox(-4.5F, -1.5F, 7.0F, 9, 4, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.2268928F, 0.0F, 0.0F));

        head.addOrReplaceChild("collar_r", CubeListBuilder.create().texOffs(17, 11).addBox(-5.5F, -1.5F, -3.0F, 1, 4, 11),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.2268928F, 0.0F, 0.0F));

        head.addOrReplaceChild("collar_l", CubeListBuilder.create().texOffs(17, 11).addBox(4.5F, -1.5F, -3.0F, 1, 4, 11),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.2268928F, 0.0F, 0.0F));

        // --- Body parts ---
        body.addOrReplaceChild("belt_r", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("belt_l", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(56, 55).addBox(-4.0F, 8.0F, -3.0F, 8, 4, 1), PartPose.ZERO);

        body.addOrReplaceChild("mbelt_l", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("mbelt_r", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(56, 45).addBox(-4.0F, 1.0F, -3.8F, 8, 7, 2), PartPose.ZERO);

        body.addOrReplaceChild("chest_ornament", CubeListBuilder.create().texOffs(76, 53).addBox(-2.5F, 3.0F, -4.8F, 5, 5, 1), PartPose.ZERO);

        body.addOrReplaceChild("chest_cloth_l", CubeListBuilder.create().texOffs(20, 47).mirror().addBox(1.5F, 1.2F, -4.5F, 3, 9, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0663225F, 0.0F, 0.0F));

        body.addOrReplaceChild("chest_cloth_r", CubeListBuilder.create().texOffs(20, 47).addBox(-4.5F, 1.2F, -4.5F, 3, 9, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0663225F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg_cloth_r", CubeListBuilder.create().texOffs(20, 55).addBox(0.0F, 0.0F, 0.0F, 3, 8, 1),
                PartPose.offsetAndRotation(-4.5F, 10.4F, -3.9F, -0.0349066F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg_cloth_l", CubeListBuilder.create().texOffs(20, 55).mirror().addBox(0.0F, 0.0F, 0.0F, 3, 8, 1),
                PartPose.offsetAndRotation(1.5F, 10.4F, -3.9F, -0.0349066F, 0.0F, 0.0F));

        body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45).addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);

        body.addOrReplaceChild("cloak_tl", CubeListBuilder.create().texOffs(0, 43).addBox(2.5F, 1.0F, -1.0F, 2, 1, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak_tr", CubeListBuilder.create().texOffs(0, 43).addBox(-4.5F, 1.0F, -1.0F, 2, 1, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak_1", CubeListBuilder.create().texOffs(0, 47).addBox(-4.5F, 2.0F, 1.0F, 9, 12, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak_2", CubeListBuilder.create().texOffs(0, 59).addBox(-4.5F, 14.0F, -1.3F, 9, 4, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.3069452F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak_3", CubeListBuilder.create().texOffs(0, 59).addBox(-4.5F, 17.0F, -3.7F, 9, 4, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.4465716F, 0.0F, 0.0F));

        // --- Right arm parts ---
        rightArm.addOrReplaceChild("gauntlet_r", CubeListBuilder.create().texOffs(100, 26).addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("gauntletstrap_r1", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("gauntletstrap_r2", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("gauntlet_r2", CubeListBuilder.create().texOffs(102, 37).addBox(-5.0F, 3.5F, -2.0F, 1, 5, 4),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1675516F));

        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35).addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("shoulder_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.3F, -1.5F, -3.0F, 3, 5, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r2", CubeListBuilder.create().texOffs(0, 19).addBox(-3.3F, 3.5F, -2.5F, 1, 1, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r3", CubeListBuilder.create().texOffs(0, 11).addBox(-2.3F, 3.5F, -3.0F, 1, 2, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r4", CubeListBuilder.create().texOffs(18, 4).addBox(-2.3F, -1.5F, -4.0F, 1, 6, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r5", CubeListBuilder.create().texOffs(18, 4).addBox(-2.3F, -1.5F, 3.0F, 1, 6, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        // --- Left arm parts ---
        leftArm.addOrReplaceChild("gauntlet_l", CubeListBuilder.create().texOffs(114, 26).addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("gauntlet_l2", CubeListBuilder.create().texOffs(102, 37).addBox(4.0F, 3.5F, -2.0F, 1, 5, 4),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1675516F));

        leftArm.addOrReplaceChild("gauntletstrap_l1", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("gauntletstrap_l2", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("shoulder_l1", CubeListBuilder.create().texOffs(0, 0).addBox(1.3F, -1.5F, -3.0F, 3, 5, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l2", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(2.3F, 3.5F, -2.5F, 1, 1, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l3", CubeListBuilder.create().texOffs(0, 11).addBox(1.3F, 3.5F, -3.0F, 1, 2, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l4", CubeListBuilder.create().texOffs(18, 4).addBox(1.3F, -1.5F, -4.0F, 1, 6, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l5", CubeListBuilder.create().texOffs(18, 4).addBox(1.3F, -1.5F, 3.0F, 1, 6, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        // --- Right leg parts ---
        rightLeg.addOrReplaceChild("backpanel_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.5F, 2.5F, 5, 7, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0698132F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("backpanel_r2", CubeListBuilder.create().texOffs(96, 14).addBox(-3.0F, -0.5F, -2.5F, 5, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        rightLeg.addOrReplaceChild("backpanel_r3", CubeListBuilder.create().texOffs(116, 13).addBox(-3.0F, 2.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        rightLeg.addOrReplaceChild("backpanel_r4", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-3.0F, -0.5F, -3.5F, 5, 7, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0349066F, 0.0F, 0.0F));

        // --- Left leg parts ---
        leftLeg.addOrReplaceChild("backpanel_l1", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -0.5F, 2.5F, 5, 7, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0698132F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("backpanel_l2", CubeListBuilder.create().texOffs(96, 14).addBox(-2.0F, -0.5F, -2.5F, 5, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        leftLeg.addOrReplaceChild("backpanel_l3", CubeListBuilder.create().texOffs(116, 13).addBox(2.0F, 2.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        leftLeg.addOrReplaceChild("backpanel_l4", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -0.5F, -3.5F, 5, 7, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0349066F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
