package thaumcraft.client.renderers.models.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import thaumcraft.common.items.equipment.FortressArmorItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

public class FortressArmorModel extends HumanoidModel<LivingEntity> {

    // Head decorations
    private final ModelPart ornamentL;
    private final ModelPart ornamentL2;
    private final ModelPart ornamentR;
    private final ModelPart ornamentR2;
    private final ModelPart helmet;
    private final ModelPart helmetR;
    private final ModelPart helmetL;
    private final ModelPart helmetB;
    private final ModelPart capsthingy;
    private final ModelPart flapR;
    private final ModelPart flapL;
    private final ModelPart gemornament;
    private final ModelPart gem;
    private final ModelPart[] mask;
    private final ModelPart goggles;

    // Body decorations
    private final ModelPart beltR;
    private final ModelPart beltL;
    private final ModelPart mbelt;
    private final ModelPart mbeltL;
    private final ModelPart mbeltR;
    private final ModelPart chestplate;
    private final ModelPart scroll;
    private final ModelPart backplate;
    private final ModelPart book;

    // Right arm
    private final ModelPart shoulderR;
    private final ModelPart gauntletR;
    private final ModelPart gauntletstrapR1;
    private final ModelPart gauntletstrapR2;
    private final ModelPart shoulderplateRtop;
    private final ModelPart shoulderplateR1;
    private final ModelPart shoulderplateR2;
    private final ModelPart shoulderplateR3;

    // Left arm
    private final ModelPart shoulderL;
    private final ModelPart gauntletL;
    private final ModelPart gauntletstrapl1;
    private final ModelPart gauntletstrapL2;
    private final ModelPart shoulderplateLtop;
    private final ModelPart shoulderplateL1;
    private final ModelPart shoulderplateL2;
    private final ModelPart shoulderplateL3;

    // Right leg
    private final ModelPart legpanelR1;
    private final ModelPart legpanelR2;
    private final ModelPart legpanelR3;
    private final ModelPart legpanelR4;
    private final ModelPart legpanelR5;
    private final ModelPart legpanelR6;
    private final ModelPart sidepanelR1;
    private final ModelPart sidepanelR2;
    private final ModelPart sidepanelR3;
    private final ModelPart backpanelR1;
    private final ModelPart backpanelR2;
    private final ModelPart backpanelR3;

    // Left leg
    private final ModelPart legpanelL1;
    private final ModelPart legpanelL2;
    private final ModelPart legpanelL3;
    private final ModelPart legpanelL4;
    private final ModelPart legpanelL5;
    private final ModelPart legpanelL6;
    private final ModelPart sidepanelL1;
    private final ModelPart sidepanelL2;
    private final ModelPart sidepanelL3;
    private final ModelPart backpanelL1;
    private final ModelPart backpanelL2;
    private final ModelPart backpanelL3;

    private final Map<Integer, Integer> hasSet = new HashMap<>();
    private final Map<Integer, Integer> hasMask = new HashMap<>();
    private final Map<Integer, Boolean> hasGoggles = new HashMap<>();

    public FortressArmorModel(ModelPart root) {
        super(root, RenderType::entityCutout);
        // Head
        this.ornamentL = root.getChild("head").getChild("ornamentL");
        this.ornamentL2 = root.getChild("head").getChild("ornamentL2");
        this.ornamentR = root.getChild("head").getChild("ornamentR");
        this.ornamentR2 = root.getChild("head").getChild("ornamentR2");
        this.helmet = root.getChild("head").getChild("helmet");
        this.helmetR = root.getChild("head").getChild("helmetR");
        this.helmetL = root.getChild("head").getChild("helmetL");
        this.helmetB = root.getChild("head").getChild("helmetB");
        this.capsthingy = root.getChild("head").getChild("capsthingy");
        this.flapR = root.getChild("head").getChild("flapR");
        this.flapL = root.getChild("head").getChild("flapL");
        this.gemornament = root.getChild("head").getChild("gemornament");
        this.gem = root.getChild("head").getChild("gem");
        this.mask = new ModelPart[]{root.getChild("head").getChild("mask0"), root.getChild("head").getChild("mask1"), root.getChild("head").getChild("mask2")};
        this.goggles = root.getChild("head").getChild("goggles");
        // Body
        this.beltR = root.getChild("body").getChild("beltR");
        this.beltL = root.getChild("body").getChild("beltL");
        this.mbelt = root.getChild("body").getChild("mbelt");
        this.mbeltL = root.getChild("body").getChild("mbeltL");
        this.mbeltR = root.getChild("body").getChild("mbeltR");
        this.chestplate = root.getChild("body").getChild("chestplate");
        this.scroll = root.getChild("body").getChild("scroll");
        this.backplate = root.getChild("body").getChild("backplate");
        this.book = root.getChild("body").getChild("book");
        // Right arm
        this.shoulderR = root.getChild("right_arm").getChild("shoulderR");
        this.gauntletR = root.getChild("right_arm").getChild("gauntletR");
        this.gauntletstrapR1 = root.getChild("right_arm").getChild("gauntletstrapR1");
        this.gauntletstrapR2 = root.getChild("right_arm").getChild("gauntletstrapR2");
        this.shoulderplateRtop = root.getChild("right_arm").getChild("shoulderplateRtop");
        this.shoulderplateR1 = root.getChild("right_arm").getChild("shoulderplateR1");
        this.shoulderplateR2 = root.getChild("right_arm").getChild("shoulderplateR2");
        this.shoulderplateR3 = root.getChild("right_arm").getChild("shoulderplateR3");
        // Left arm
        this.shoulderL = root.getChild("left_arm").getChild("shoulderL");
        this.gauntletL = root.getChild("left_arm").getChild("gauntletL");
        this.gauntletstrapl1 = root.getChild("left_arm").getChild("gauntletstrapl1");
        this.gauntletstrapL2 = root.getChild("left_arm").getChild("gauntletstrapL2");
        this.shoulderplateLtop = root.getChild("left_arm").getChild("shoulderplateLtop");
        this.shoulderplateL1 = root.getChild("left_arm").getChild("shoulderplateL1");
        this.shoulderplateL2 = root.getChild("left_arm").getChild("shoulderplateL2");
        this.shoulderplateL3 = root.getChild("left_arm").getChild("shoulderplateL3");
        // Right leg
        this.legpanelR1 = root.getChild("right_leg").getChild("legpanelR1");
        this.legpanelR2 = root.getChild("right_leg").getChild("legpanelR2");
        this.legpanelR3 = root.getChild("right_leg").getChild("legpanelR3");
        this.legpanelR4 = root.getChild("right_leg").getChild("legpanelR4");
        this.legpanelR5 = root.getChild("right_leg").getChild("legpanelR5");
        this.legpanelR6 = root.getChild("right_leg").getChild("legpanelR6");
        this.sidepanelR1 = root.getChild("right_leg").getChild("sidepanelR1");
        this.sidepanelR2 = root.getChild("right_leg").getChild("sidepanelR2");
        this.sidepanelR3 = root.getChild("right_leg").getChild("sidepanelR3");
        this.backpanelR1 = root.getChild("right_leg").getChild("backpanelR1");
        this.backpanelR2 = root.getChild("right_leg").getChild("backpanelR2");
        this.backpanelR3 = root.getChild("right_leg").getChild("backpanelR3");
        // Left leg
        this.legpanelL1 = root.getChild("left_leg").getChild("legpanelL1");
        this.legpanelL2 = root.getChild("left_leg").getChild("legpanelL2");
        this.legpanelL3 = root.getChild("left_leg").getChild("legpanelL3");
        this.legpanelL4 = root.getChild("left_leg").getChild("legpanelL4");
        this.legpanelL5 = root.getChild("left_leg").getChild("legpanelL5");
        this.legpanelL6 = root.getChild("left_leg").getChild("legpanelL6");
        this.sidepanelL1 = root.getChild("left_leg").getChild("sidepanelL1");
        this.sidepanelL2 = root.getChild("left_leg").getChild("sidepanelL2");
        this.sidepanelL3 = root.getChild("left_leg").getChild("sidepanelL3");
        this.backpanelL1 = root.getChild("left_leg").getChild("backpanelL1");
        this.backpanelL2 = root.getChild("left_leg").getChild("backpanelL2");
        this.backpanelL3 = root.getChild("left_leg").getChild("backpanelL3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Empty body parts (no default cubes — only fortress children)
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Head parts
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(41, 8).addBox(-4.5F, -9.0F, -4.5F, 9, 4, 9), PartPose.ZERO);
        head.addOrReplaceChild("helmetR", CubeListBuilder.create().texOffs(21, 13).addBox(-6.5F, -3.0F, -4.5F, 1, 5, 9),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.5235988F));
        head.addOrReplaceChild("helmetL", CubeListBuilder.create().texOffs(21, 13).mirror().addBox(5.5F, -3.0F, -4.5F, 1, 5, 9),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.5235988F));
        head.addOrReplaceChild("helmetB", CubeListBuilder.create().texOffs(41, 21).addBox(-4.5F, -3.0F, 5.5F, 9, 5, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.5235988F, 0, 0));
        head.addOrReplaceChild("capsthingy", CubeListBuilder.create().texOffs(21, 0).addBox(-4.5F, -6.0F, -6.5F, 9, 1, 2), PartPose.ZERO);
        head.addOrReplaceChild("ornamentL", CubeListBuilder.create().texOffs(78, 8).mirror().addBox(1.5F, -9.0F, -6.5F, 2, 2, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        head.addOrReplaceChild("ornamentL2", CubeListBuilder.create().texOffs(78, 8).mirror().addBox(3.5F, -10.0F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        head.addOrReplaceChild("ornamentR", CubeListBuilder.create().texOffs(78, 8).addBox(-3.5F, -9.0F, -6.5F, 2, 2, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        head.addOrReplaceChild("ornamentR2", CubeListBuilder.create().texOffs(78, 8).addBox(-4.5F, -10.0F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        head.addOrReplaceChild("flapR", CubeListBuilder.create().texOffs(59, 10).addBox(-10.0F, -2.0F, -1.0F, 3, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0, -0.5235988F, 0.5235988F));
        head.addOrReplaceChild("flapL", CubeListBuilder.create().texOffs(59, 10).mirror().addBox(7.0F, -2.0F, -1.0F, 3, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0.5235988F, -0.5235988F));
        head.addOrReplaceChild("gemornament", CubeListBuilder.create().texOffs(68, 11).addBox(-1.5F, -9.0F, -7.0F, 3, 3, 2),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        head.addOrReplaceChild("gem", CubeListBuilder.create().texOffs(72, 8).addBox(-1.0F, -8.5F, -7.5F, 2, 2, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.1396263F, 0, 0));
        for (int a = 0; a < 3; a++) {
            head.addOrReplaceChild("mask" + a, CubeListBuilder.create().texOffs(52 + a * 24, 2).addBox(-4.5F, -5.0F, -4.6F, 9, 5, 1), PartPose.ZERO);
        }
        head.addOrReplaceChild("goggles", CubeListBuilder.create().texOffs(100, 18).addBox(-4.5F, -5.0F, -4.25F, 9, 5, 1), PartPose.ZERO);

        // Body parts
        body.addOrReplaceChild("beltR", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
        body.addOrReplaceChild("beltL", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
        body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(56, 55).addBox(-4.0F, 8.0F, -3.0F, 8, 4, 1), PartPose.ZERO);
        body.addOrReplaceChild("mbeltL", CubeListBuilder.create().texOffs(76, 44).addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
        body.addOrReplaceChild("mbeltR", CubeListBuilder.create().texOffs(76, 44).addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
        body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(56, 45).addBox(-4.0F, 1.0F, -4.0F, 8, 7, 2), PartPose.ZERO);
        body.addOrReplaceChild("scroll", CubeListBuilder.create().texOffs(34, 27).addBox(-2.0F, 9.5F, 4.0F, 8, 3, 3),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.1919862F));
        body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45).addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);
        body.addOrReplaceChild("book", CubeListBuilder.create().texOffs(100, 8).addBox(1.0F, -0.3F, 4.0F, 5, 7, 2),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.7679449F));

        // Right arm
        rightArm.addOrReplaceChild("shoulderR", CubeListBuilder.create().texOffs(56, 35).addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletR", CubeListBuilder.create().texOffs(100, 26).addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrapR1", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrapR2", CubeListBuilder.create().texOffs(84, 31).addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("shoulderplateRtop", CubeListBuilder.create().texOffs(110, 37).addBox(-5.5F, -2.5F, -3.5F, 2, 1, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplateR1", CubeListBuilder.create().texOffs(110, 45).addBox(-4.5F, -1.5F, -3.5F, 1, 4, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplateR2", CubeListBuilder.create().texOffs(94, 45).addBox(-3.5F, 1.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplateR3", CubeListBuilder.create().texOffs(94, 45).addBox(-2.5F, 3.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));

        // Left arm
        leftArm.addOrReplaceChild("shoulderL", CubeListBuilder.create().texOffs(56, 35).mirror().addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletL", CubeListBuilder.create().texOffs(114, 26).addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrapl1", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrapL2", CubeListBuilder.create().texOffs(84, 31).mirror().addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("shoulderplateLtop", CubeListBuilder.create().texOffs(110, 37).mirror().addBox(3.5F, -2.5F, -3.5F, 2, 1, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplateL1", CubeListBuilder.create().texOffs(110, 45).mirror().addBox(3.5F, -1.5F, -3.5F, 1, 4, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplateL2", CubeListBuilder.create().texOffs(94, 45).mirror().addBox(2.5F, 1.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplateL3", CubeListBuilder.create().texOffs(94, 45).mirror().addBox(1.5F, 3.5F, -3.5F, 1, 3, 7),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));

        // Right leg — FULL rewrite matching original TC4 coordinates
        rightLeg.addOrReplaceChild("legpanelR1", CubeListBuilder.create().texOffs(0, 51).addBox(-1.0F, 0.5F, -3.5F, 3, 4, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("legpanelR2", CubeListBuilder.create().texOffs(8, 51).addBox(-1.0F, 3.5F, -2.5F, 3, 4, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("legpanelR3", CubeListBuilder.create().texOffs(0, 56).addBox(-1.0F, 6.5F, -1.5F, 3, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("legpanelR4", CubeListBuilder.create().texOffs(0, 43).addBox(-3.0F, 0.5F, -3.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("legpanelR5", CubeListBuilder.create().texOffs(0, 47).addBox(-3.0F, 2.5F, -2.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("legpanelR6", CubeListBuilder.create().texOffs(6, 43).addBox(-3.0F, 4.5F, -1.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("sidepanelR1", CubeListBuilder.create().texOffs(0, 22).addBox(-2.5F, 0.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightLeg.addOrReplaceChild("sidepanelR2", CubeListBuilder.create().texOffs(0, 31).addBox(-1.5F, 3.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightLeg.addOrReplaceChild("sidepanelR3", CubeListBuilder.create().texOffs(12, 31).addBox(-0.5F, 5.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0.4363323F));
        rightLeg.addOrReplaceChild("backpanelR1", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, 0.5F, 2.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("backpanelR2", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, 2.5F, 1.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));
        rightLeg.addOrReplaceChild("backpanelR3", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, 4.5F, 0.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));

        // Left leg — DIFFERENT coordinates from right, not just mirrored!
        leftLeg.addOrReplaceChild("legpanelL1", CubeListBuilder.create().texOffs(0, 51).mirror().addBox(-2.0F, 0.5F, -3.5F, 3, 4, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("legpanelL2", CubeListBuilder.create().texOffs(8, 51).mirror().addBox(-2.0F, 3.5F, -2.5F, 3, 4, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("legpanelL3", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(-2.0F, 6.5F, -1.5F, 3, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("legpanelL4", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(1.0F, 0.5F, -3.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("legpanelL5", CubeListBuilder.create().texOffs(0, 47).mirror().addBox(1.0F, 2.5F, -2.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("legpanelL6", CubeListBuilder.create().texOffs(6, 43).mirror().addBox(1.0F, 4.5F, -1.5F, 2, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, -0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("sidepanelL1", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(1.5F, 0.5F, -2.5F, 1, 4, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftLeg.addOrReplaceChild("sidepanelL2", CubeListBuilder.create().texOffs(0, 31).mirror().addBox(0.5F, 3.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftLeg.addOrReplaceChild("sidepanelL3", CubeListBuilder.create().texOffs(12, 31).mirror().addBox(-0.5F, 5.5F, -2.5F, 1, 3, 5),
                PartPose.offsetAndRotation(0, 0, 0, 0, 0, -0.4363323F));
        leftLeg.addOrReplaceChild("backpanelL1", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-2.0F, 0.5F, 2.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("backpanelL2", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-2.0F, 2.5F, 1.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));
        leftLeg.addOrReplaceChild("backpanelL3", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-2.0F, 4.5F, 0.5F, 5, 3, 1),
                PartPose.offsetAndRotation(0, 0, 0, 0.4363323F, 0, 0));

        return LayerDefinition.create(mesh, 128, 64);
    }

    private void checkSet(LivingEntity entity) {
        if (entity.tickCount % 20 == 0) {
            int set = 0;
            int entityId = entity.getId();
            Integer maskFound = null;
            boolean goggleFound = false;
            for (net.minecraft.world.item.ItemStack piece : entity.getArmorSlots()) {
                if (piece.getItem() instanceof FortressArmorItem) {
                    set++;
                    if (piece.getItem() instanceof FortressArmorItem && ((net.minecraft.world.item.ArmorItem) piece.getItem()).getType() == net.minecraft.world.item.ArmorItem.Type.HELMET) {
                        if (FortressArmorItem.hasGoggles(piece)) {
                            goggleFound = true;
                        }
                        int m = FortressArmorItem.getMask(piece);
                        if (m >= 0) {
                            maskFound = m;
                        }
                    }
                }
            }
            if (set > 0) {
                hasSet.put(entityId, set);
            } else {
                hasSet.remove(entityId);
            }

            if (maskFound != null) {
                hasMask.put(entityId, maskFound);
            } else {
                hasMask.remove(entityId);
            }

            if (goggleFound) {
                hasGoggles.put(entityId, true);
            } else {
                hasGoggles.remove(entityId);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        LivingEntity entity = null; // We need entity context, but renderToBuffer doesn't provide it
        // visibility control is handled pre-render in setupVisibility
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void resetVisibility() {
        // Reset all parts to visible so setupVisibility can toggle them per slot & set
        this.helmet.visible = true;
        this.helmetR.visible = true;
        this.helmetL.visible = true;
        this.helmetB.visible = true;
        this.capsthingy.visible = true;
        this.ornamentL.visible = true;
        this.ornamentL2.visible = true;
        this.ornamentR.visible = true;
        this.ornamentR2.visible = true;
        this.gemornament.visible = true;
        this.gem.visible = true;
        this.goggles.visible = true;
        this.flapR.visible = true;
        this.flapL.visible = true;
        for (int a = 0; a < 3; a++) {
            this.mask[a].visible = true;
        }

        this.beltR.visible = true;
        this.beltL.visible = true;
        this.mbelt.visible = true;
        this.mbeltL.visible = true;
        this.mbeltR.visible = true;
        this.chestplate.visible = true;
        this.scroll.visible = true;
        this.backplate.visible = true;
        this.book.visible = true;

        this.shoulderR.visible = true;
        this.gauntletR.visible = true;
        this.gauntletstrapR1.visible = true;
        this.gauntletstrapR2.visible = true;
        this.shoulderplateRtop.visible = true;
        this.shoulderplateR1.visible = true;
        this.shoulderplateR2.visible = true;
        this.shoulderplateR3.visible = true;
        this.shoulderL.visible = true;
        this.gauntletL.visible = true;
        this.gauntletstrapl1.visible = true;
        this.gauntletstrapL2.visible = true;
        this.shoulderplateLtop.visible = true;
        this.shoulderplateL1.visible = true;
        this.shoulderplateL2.visible = true;
        this.shoulderplateL3.visible = true;

        this.legpanelR1.visible = true;
        this.legpanelR2.visible = true;
        this.legpanelR3.visible = true;
        this.legpanelR4.visible = true;
        this.legpanelR5.visible = true;
        this.legpanelR6.visible = true;
        this.sidepanelR1.visible = true;
        this.sidepanelR2.visible = true;
        this.sidepanelR3.visible = true;
        this.backpanelR1.visible = true;
        this.backpanelR2.visible = true;
        this.backpanelR3.visible = true;
        this.legpanelL1.visible = true;
        this.legpanelL2.visible = true;
        this.legpanelL3.visible = true;
        this.legpanelL4.visible = true;
        this.legpanelL5.visible = true;
        this.legpanelL6.visible = true;
        this.sidepanelL1.visible = true;
        this.sidepanelL2.visible = true;
        this.sidepanelL3.visible = true;
        this.backpanelL1.visible = true;
        this.backpanelL2.visible = true;
        this.backpanelL3.visible = true;
    }

    public void setupVisibility(LivingEntity entity, EquipmentSlot slot) {
        checkSet(entity);
        int entityId = entity.getId();
        int set = hasSet.containsKey(entityId) ? hasSet.get(entityId) : -1;
        int mask = hasMask.containsKey(entityId) ? hasMask.get(entityId) : -1;
        boolean hasGogs = hasGoggles.containsKey(entityId);

        // Reset all then hide what doesn't belong to this slot
        resetVisibility();

        // Head-only parts
        boolean isHead = slot == EquipmentSlot.HEAD;
        this.helmet.visible = isHead;
        this.helmetR.visible = isHead;
        this.helmetL.visible = isHead;
        this.helmetB.visible = isHead;
        this.capsthingy.visible = isHead;
        this.ornamentL.visible = isHead;
        this.ornamentL2.visible = isHead;
        this.ornamentR.visible = isHead;
        this.ornamentR2.visible = isHead;
        this.gemornament.visible = isHead;
        this.gem.visible = isHead;
        this.goggles.visible = isHead;
        this.flapR.visible = isHead;
        this.flapL.visible = isHead;
        for (int a = 0; a < 3; a++) {
            this.mask[a].visible = isHead;
        }

        // Chest/body parts
        boolean isChest = slot == EquipmentSlot.CHEST;
        this.beltR.visible = isChest;
        this.beltL.visible = isChest;
        this.chestplate.visible = isChest;
        this.backplate.visible = isChest;
        this.scroll.visible = isChest;
        this.book.visible = isChest;
        this.shoulderR.visible = isChest;
        this.gauntletR.visible = isChest;
        this.gauntletstrapR1.visible = isChest;
        this.gauntletstrapR2.visible = isChest;
        this.shoulderL.visible = isChest;
        this.gauntletL.visible = isChest;
        this.gauntletstrapl1.visible = isChest;
        this.gauntletstrapL2.visible = isChest;

        // Leg parts
        boolean isLegs = slot == EquipmentSlot.LEGS;
        this.mbelt.visible = isLegs;
        this.mbeltL.visible = isLegs;
        this.mbeltR.visible = isLegs;
        this.legpanelR1.visible = isLegs;
        this.legpanelR2.visible = isLegs;
        this.legpanelR3.visible = isLegs;
        this.legpanelR4.visible = isLegs;
        this.legpanelR5.visible = isLegs;
        this.legpanelR6.visible = isLegs;
        this.sidepanelR1.visible = isLegs;
        this.backpanelR1.visible = isLegs;
        this.backpanelR2.visible = isLegs;
        this.backpanelR3.visible = isLegs;
        this.legpanelL1.visible = isLegs;
        this.legpanelL2.visible = isLegs;
        this.legpanelL3.visible = isLegs;
        this.legpanelL4.visible = isLegs;
        this.legpanelL5.visible = isLegs;
        this.legpanelL6.visible = isLegs;
        this.sidepanelL1.visible = isLegs;
        this.backpanelL1.visible = isLegs;
        this.backpanelL2.visible = isLegs;
        this.backpanelL3.visible = isLegs;
        this.sidepanelR2.visible = isLegs;
        this.sidepanelL2.visible = isLegs;
        this.sidepanelR3.visible = isLegs;
        this.sidepanelL3.visible = isLegs;

        // Set progression: more pieces = more decorations (only relevant for chest)
        if (isChest) {
            this.scroll.visible = (set >= 3);
            this.book.visible = (set >= 2);
            this.shoulderplateRtop.visible = (set >= 2);
            this.shoulderplateR1.visible = (set >= 2);
            this.shoulderplateR2.visible = (set >= 3);
            this.shoulderplateR3.visible = (set >= 3);
            this.shoulderplateLtop.visible = (set >= 2);
            this.shoulderplateL1.visible = (set >= 2);
            this.shoulderplateL2.visible = (set >= 3);
            this.shoulderplateL3.visible = (set >= 3);
        }

        if (isHead) {
            this.goggles.visible = hasGogs;
            for (int a = 0; a < 3; a++) {
                this.mask[a].visible = (mask == a);
            }

            // Head set decorations
            this.ornamentL.visible = (set >= 3);
            this.ornamentL2.visible = (set >= 3);
            this.ornamentR.visible = (set >= 3);
            this.ornamentR2.visible = (set >= 3);
            this.gemornament.visible = (set >= 3);
            this.gem.visible = (set >= 3);
            this.flapL.visible = (set >= 2);
            this.flapR.visible = (set >= 2);
        }

        if (isLegs) {
            this.sidepanelR2.visible = (set >= 2);
            this.sidepanelL2.visible = (set >= 2);
            this.sidepanelR3.visible = (set >= 3);
            this.sidepanelL3.visible = (set >= 3);
        }
    }
}

