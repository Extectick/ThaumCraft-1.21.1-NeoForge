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

public class KnightArmorModel extends HumanoidModel<LivingEntity> {

    public KnightArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.getChild("head");
        PartDefinition body = root.getChild("body");
        PartDefinition rightArm = root.getChild("right_arm");
        PartDefinition leftArm = root.getChild("left_arm");
        PartDefinition rightLeg = root.getChild("right_leg");
        PartDefinition leftLeg = root.getChild("left_leg");

        // === Head ===
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(41, 8).addBox(-4.5F, -9.0F, -4.5F, 9, 9, 9), PartPose.ZERO);

        // === Body ===
        body.addOrReplaceChild("belt_r", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(56, 55).addBox(-4.0F, 8.0F, -3.0F, 8, 4, 1), PartPose.ZERO);

        body.addOrReplaceChild("mbelt_l", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("mbelt_r", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("belt_l", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

        body.addOrReplaceChild("tabbard", CubeListBuilder.create().texOffs(114, 52).addBox(-3.0F, 1.2F, -3.5F, 6, 10, 1), PartPose.ZERO);

        body.addOrReplaceChild("cloak_at_l", CubeListBuilder.create().texOffs(0, 43).addBox(2.5F, 1.0F, 2.0F, 2, 1, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45).addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);

        body.addOrReplaceChild("cloak1", CubeListBuilder.create().texOffs(0, 47).addBox(0.0F, 0.0F, 0.0F, 9, 12, 1),
                PartPose.offsetAndRotation(-4.5F, 1.3F, 4.2F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak2", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 11.7F, -2.0F, 9, 4, 1),
                PartPose.offsetAndRotation(-4.5F, 1.3F, 4.2F, 0.3069452F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak3", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 15.2F, -4.2F, 9, 4, 1),
                PartPose.offsetAndRotation(-4.5F, 1.3F, 4.2F, 0.4465716F, 0.0F, 0.0F));

        body.addOrReplaceChild("cloak_at_r", CubeListBuilder.create().texOffs(0, 43).addBox(-4.5F, 1.0F, 2.0F, 2, 1, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396263F, 0.0F, 0.0F));

        body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(56, 45).addBox(-4.0F, 1.0F, -3.0F, 8, 7, 1), PartPose.ZERO);

        body.addOrReplaceChild("frontcloth1", CubeListBuilder.create().texOffs(120, 39).addBox(0.0F, 0.0F, 0.0F, 6, 8, 1),
                PartPose.offsetAndRotation(-3.0F, 11.0F, -3.5F, -0.1047198F, 0.0F, 0.0F));

        body.addOrReplaceChild("frontcloth2", CubeListBuilder.create().texOffs(100, 37).addBox(0.0F, 7.5F, 1.8F, 6, 3, 1),
                PartPose.offsetAndRotation(-3.0F, 11.0F, -3.5F, -0.3316126F, 0.0F, 0.0F));

        // === Right Arm ===
        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35).addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("shoulder_r0", CubeListBuilder.create().texOffs(0, 0).addBox(-4.3F, -1.5F, -3.0F, 3, 5, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-3.3F, 3.5F, -2.5F, 1, 1, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("shoulder_r2", CubeListBuilder.create().texOffs(0, 11).addBox(-2.3F, 3.5F, -3.0F, 1, 2, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

        rightArm.addOrReplaceChild("gauntlet_r", CubeListBuilder.create().texOffs(100, 26).addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("gauntletstrap_r1", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        rightArm.addOrReplaceChild("gauntletstrap_r2", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // === Left Arm ===
        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("shoulder_l0", CubeListBuilder.create().texOffs(0, 0).addBox(1.3F, -1.5F, -3.0F, 3, 5, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l1", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(2.3F, 3.5F, -2.5F, 1, 1, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("shoulder_l2", CubeListBuilder.create().texOffs(0, 11).addBox(1.3F, 3.5F, -3.0F, 1, 2, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7853982F));

        leftArm.addOrReplaceChild("gauntlet_l", CubeListBuilder.create().texOffs(114, 26).addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("gauntletstrap_l1", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        leftArm.addOrReplaceChild("gauntletstrap_l2", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // === Right Leg Side Panels ===
        rightLeg.addOrReplaceChild("sidepanel_r0", CubeListBuilder.create().texOffs(96, 14).addBox(-3.0F, -0.5F, -2.5F, 5, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        rightLeg.addOrReplaceChild("sidepanel_r1", CubeListBuilder.create().texOffs(96, 7).mirror().addBox(0.0F, 2.5F, -2.5F, 2, 2, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        rightLeg.addOrReplaceChild("sidepanel_r2", CubeListBuilder.create().texOffs(114, 5).mirror().addBox(-2.0F, 2.5F, -2.5F, 2, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        rightLeg.addOrReplaceChild("sidepanel_r3", CubeListBuilder.create().texOffs(116, 13).addBox(-3.0F, 2.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396263F));

        // === Left Leg Side Panels ===
        leftLeg.addOrReplaceChild("sidepanel_l0", CubeListBuilder.create().texOffs(96, 14).addBox(-2.0F, -0.5F, -2.5F, 5, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        leftLeg.addOrReplaceChild("sidepanel_l1", CubeListBuilder.create().texOffs(96, 7).addBox(-2.0F, 2.5F, -2.5F, 2, 2, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        leftLeg.addOrReplaceChild("sidepanel_l2", CubeListBuilder.create().texOffs(114, 5).addBox(0.0F, 2.5F, -2.5F, 2, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        leftLeg.addOrReplaceChild("sidepanel_l3", CubeListBuilder.create().texOffs(116, 13).addBox(2.0F, 2.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396263F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
