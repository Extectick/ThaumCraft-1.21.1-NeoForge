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

public class RobeArmorModel extends HumanoidModel<LivingEntity> {

    public RobeArmorModel(ModelPart root) {
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

        // --- Head parts ---
        head.addOrReplaceChild("Hood1", CubeListBuilder.create().texOffs(16, 7).addBox(-4.5F, -9.0F, -4.6F, 9, 9, 9), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("Hood2", CubeListBuilder.create().texOffs(52, 13).addBox(-4.0F, -9.7F, 2.0F, 8, 9, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2268928F, 0.0F, 0.0F));

        head.addOrReplaceChild("Hood3", CubeListBuilder.create().texOffs(52, 14).addBox(-3.5F, -10.0F, 3.5F, 7, 8, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3490659F, 0.0F, 0.0F));

        head.addOrReplaceChild("Hood4", CubeListBuilder.create().texOffs(53, 15).addBox(-3.0F, -10.7F, 3.5F, 6, 7, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5759587F, 0.0F, 0.0F));

        // --- Body parts (all variants) ---
        body.addOrReplaceChild("Chestthing", CubeListBuilder.create().texOffs(56, 50).addBox(-2.5F, 1.0F, -4.0F, 5, 7, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("Mbelt", CubeListBuilder.create().texOffs(16, 55).addBox(-4.0F, 7.0F, -3.0F, 8, 5, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("MbeltB", CubeListBuilder.create().texOffs(16, 55).addBox(-4.0F, 7.0F, -4.0F, 8, 5, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.141593F, 0.0F));

        body.addOrReplaceChild("ClothchestL", CubeListBuilder.create().texOffs(108, 38).mirror().addBox(2.1F, 0.5F, -3.5F, 2, 8, 1),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothchestR", CubeListBuilder.create().texOffs(108, 38).addBox(-4.1F, 0.5F, -3.5F, 2, 8, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("Book", CubeListBuilder.create().texOffs(81, 16).addBox(1.0F, 0.0F, 4.0F, 5, 7, 2),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7679449F));

        body.addOrReplaceChild("Scroll", CubeListBuilder.create().texOffs(78, 25).addBox(-2.0F, 9.5F, 4.0F, 8, 3, 3),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1919862F));

        body.addOrReplaceChild("BeltR", CubeListBuilder.create().texOffs(16, 36).addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("Backplate", CubeListBuilder.create().texOffs(36, 45).addBox(-4.0F, 1.0F, 1.9F, 8, 11, 2), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("MbeltL", CubeListBuilder.create().texOffs(16, 36).addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("MbeltR", CubeListBuilder.create().texOffs(16, 36).addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("BeltL", CubeListBuilder.create().texOffs(16, 36).addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("Chestplate", CubeListBuilder.create().texOffs(16, 25).addBox(-4.0F, 1.0F, -3.0F, 8, 6, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        // --- Front cloth panels (body) ---
        body.addOrReplaceChild("FrontclothR1", CubeListBuilder.create().texOffs(108, 38).addBox(0.0F, 0.0F, 0.0F, 3, 8, 1),
                PartPose.offsetAndRotation(-3.0F, 11.0F, -2.9F, -0.1047198F, 0.0F, 0.0F));

        body.addOrReplaceChild("FrontclothR2", CubeListBuilder.create().texOffs(108, 47).addBox(0.0F, 7.5F, 1.7F, 3, 3, 1),
                PartPose.offsetAndRotation(-3.0F, 11.0F, -2.9F, -0.3316126F, 0.0F, 0.0F));

        body.addOrReplaceChild("FrontclothL1", CubeListBuilder.create().texOffs(108, 38).mirror().addBox(0.0F, 0.0F, 0.0F, 3, 8, 1),
                PartPose.offsetAndRotation(0.0F, 11.0F, -2.9F, -0.1047198F, 0.0F, 0.0F));

        body.addOrReplaceChild("FrontclothL2", CubeListBuilder.create().texOffs(108, 47).mirror().addBox(0.0F, 7.5F, 1.7F, 3, 3, 1),
                PartPose.offsetAndRotation(0.0F, 11.0F, -2.9F, -0.3316126F, 0.0F, 0.0F));

        // --- Back cloth panels (body) ---
        body.addOrReplaceChild("ClothBackR1", CubeListBuilder.create().texOffs(118, 16).mirror().addBox(0.0F, 0.0F, 0.0F, 4, 8, 1),
                PartPose.offsetAndRotation(-4.0F, 11.5F, 2.9F, 0.1047198F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothBackR2", CubeListBuilder.create().texOffs(123, 9).addBox(0.0F, 7.8F, -0.9F, 1, 2, 1),
                PartPose.offsetAndRotation(-4.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothBackR3", CubeListBuilder.create().texOffs(120, 12).mirror().addBox(1.0F, 7.8F, -0.9F, 3, 3, 1),
                PartPose.offsetAndRotation(-4.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothBackL1", CubeListBuilder.create().texOffs(118, 16).addBox(0.0F, 0.0F, 0.0F, 4, 8, 1),
                PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.1047198F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothBackL2", CubeListBuilder.create().texOffs(123, 9).mirror().addBox(3.0F, 7.8F, -0.9F, 1, 2, 1),
                PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));

        body.addOrReplaceChild("ClothBackL3", CubeListBuilder.create().texOffs(120, 12).addBox(0.0F, 7.8F, -0.9F, 3, 3, 1),
                PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));

        // --- Right arm parts ---
        rightArm.addOrReplaceChild("ShoulderR", CubeListBuilder.create().texOffs(16, 45).mirror().addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        rightArm.addOrReplaceChild("RArm1", CubeListBuilder.create().texOffs(88, 39).addBox(-3.5F, 2.5F, -2.5F, 5, 7, 5), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightArm.addOrReplaceChild("RArm2", CubeListBuilder.create().texOffs(76, 32).addBox(-3.0F, 5.5F, 2.5F, 4, 4, 2), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightArm.addOrReplaceChild("RArm3", CubeListBuilder.create().texOffs(88, 32).addBox(-2.5F, 3.5F, 2.5F, 3, 2, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightArm.addOrReplaceChild("ShoulderplateTopR", CubeListBuilder.create().texOffs(56, 25).addBox(-5.5F, -2.5F, -3.5F, 2, 1, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363323F));

        rightArm.addOrReplaceChild("ShoulderplateR1", CubeListBuilder.create().texOffs(56, 33).addBox(-4.5F, -1.5F, -3.5F, 1, 4, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363323F));

        rightArm.addOrReplaceChild("ShoulderplateR2", CubeListBuilder.create().texOffs(40, 33).addBox(-3.5F, 1.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363323F));

        rightArm.addOrReplaceChild("ShoulderplateR3", CubeListBuilder.create().texOffs(40, 33).addBox(-2.5F, 3.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363323F));

        // --- Left arm parts ---
        leftArm.addOrReplaceChild("ShoulderL", CubeListBuilder.create().texOffs(16, 45).addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        leftArm.addOrReplaceChild("LArm1", CubeListBuilder.create().texOffs(88, 39).mirror().addBox(-1.5F, 2.5F, -2.5F, 5, 7, 5),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        leftArm.addOrReplaceChild("LArm2", CubeListBuilder.create().texOffs(76, 32).addBox(-1.0F, 5.5F, 2.5F, 4, 4, 2), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftArm.addOrReplaceChild("LArm3", CubeListBuilder.create().texOffs(88, 32).addBox(-0.5F, 3.5F, 2.5F, 3, 2, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftArm.addOrReplaceChild("ShoulderplateTopL", CubeListBuilder.create().texOffs(56, 25).addBox(3.5F, -2.5F, -3.5F, 2, 1, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363323F));

        leftArm.addOrReplaceChild("ShoulderplateL1", CubeListBuilder.create().texOffs(56, 33).addBox(3.5F, -1.5F, -3.5F, 1, 4, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363323F));

        leftArm.addOrReplaceChild("ShoulderplateL2", CubeListBuilder.create().texOffs(40, 33).addBox(2.5F, 1.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363323F));

        leftArm.addOrReplaceChild("ShoulderplateL3", CubeListBuilder.create().texOffs(40, 33).addBox(1.5F, 3.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363323F));

        // --- Right leg parts ---
        rightLeg.addOrReplaceChild("LegpanelR4", CubeListBuilder.create().texOffs(76, 38).addBox(-3.0F, 0.5F, -3.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("LegpanelR5", CubeListBuilder.create().texOffs(76, 42).addBox(-3.0F, 2.5F, -2.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("LegpanelR6", CubeListBuilder.create().texOffs(82, 38).addBox(-3.0F, 4.5F, -1.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("SidepanelR1", CubeListBuilder.create().texOffs(116, 25).addBox(-2.5F, 0.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363323F));

        rightLeg.addOrReplaceChild("SideclothR1", CubeListBuilder.create().texOffs(116, 42).addBox(-2.5F, 0.5F, -2.5F, 1, 5, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.122173F));

        rightLeg.addOrReplaceChild("SideclothR2", CubeListBuilder.create().texOffs(116, 34).addBox(-1.5F, 5.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.296706F));

        rightLeg.addOrReplaceChild("SideclothR3", CubeListBuilder.create().texOffs(116, 1).addBox(0.4F, 8.4F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));

        // --- Left leg parts ---
        leftLeg.addOrReplaceChild("LegpanelL4", CubeListBuilder.create().texOffs(76, 38).mirror().addBox(1.0F, 0.5F, -3.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("LegpanelL5", CubeListBuilder.create().texOffs(76, 42).mirror().addBox(1.0F, 2.5F, -2.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("LegpanelL6", CubeListBuilder.create().texOffs(82, 38).mirror().addBox(1.0F, 4.5F, -1.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363323F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("SidepanelL1", CubeListBuilder.create().texOffs(116, 25).addBox(1.5F, 0.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363323F));

        leftLeg.addOrReplaceChild("SideclothL1", CubeListBuilder.create().texOffs(116, 42).addBox(1.5F, 0.5F, -2.5F, 1, 5, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.122173F));

        leftLeg.addOrReplaceChild("SideclothL2", CubeListBuilder.create().texOffs(116, 34).addBox(0.5F, 5.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.296706F));

        leftLeg.addOrReplaceChild("SideclothL3", CubeListBuilder.create().texOffs(116, 1).addBox(-1.4F, 8.4F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));

        leftLeg.addOrReplaceChild("Focipouch", CubeListBuilder.create().texOffs(100, 20).addBox(3.5F, 0.5F, -2.5F, 3, 6, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.122173F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
