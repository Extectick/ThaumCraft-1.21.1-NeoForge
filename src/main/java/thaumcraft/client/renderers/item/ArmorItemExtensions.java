package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import thaumcraft.client.renderers.CustomModelLayer;
import thaumcraft.client.renderers.models.armor.FortressArmorModel;
import thaumcraft.client.renderers.models.armor.KnightArmorModel;
import thaumcraft.client.renderers.models.armor.LeaderArmorModel;
import thaumcraft.client.renderers.models.armor.RobeArmorModel;

final class ArmorItemExtensions {
    private ArmorItemExtensions() {
    }

    static IClientItemExtensions fortress() {
        return new IClientItemExtensions() {
            private FortressArmorModel model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living,
                    @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    ModelPart part = Minecraft.getInstance().getEntityModels()
                            .bakeLayer(CustomModelLayer.FORTRESS_ARMOR);
                    model = new FortressArmorModel(part);
                }
                model.setupVisibility(living, slot);
                copyPose(original, model);
                return model;
            }
        };
    }

    static IClientItemExtensions cultistRobe() {
        return robe(CustomModelLayer.ROBE_ARMOR);
    }

    static IClientItemExtensions voidRobe() {
        return robe(CustomModelLayer.ROBE_ARMOR);
    }

    static IClientItemExtensions cultistPlate() {
        return new IClientItemExtensions() {
            private KnightArmorModel model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living,
                    @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    ModelPart part = Minecraft.getInstance().getEntityModels()
                            .bakeLayer(CustomModelLayer.KNIGHT_ARMOR);
                    model = new KnightArmorModel(part);
                }
                setVisibleParts(model, slot);
                copyPose(original, model);
                return model;
            }
        };
    }

    static IClientItemExtensions cultistLeader() {
        return new IClientItemExtensions() {
            private LeaderArmorModel model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living,
                    @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    ModelPart part = Minecraft.getInstance().getEntityModels()
                            .bakeLayer(CustomModelLayer.LEADER_ARMOR);
                    model = new LeaderArmorModel(part);
                }
                setVisibleParts(model, slot);
                copyPose(original, model);
                return model;
            }
        };
    }

    private static IClientItemExtensions robe(net.minecraft.client.model.geom.ModelLayerLocation layer) {
        return new IClientItemExtensions() {
            private RobeArmorModel model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living,
                    @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    ModelPart part = Minecraft.getInstance().getEntityModels().bakeLayer(layer);
                    model = new RobeArmorModel(part);
                }
                setVisibleParts(model, slot);
                copyPose(original, model);
                return model;
            }
        };
    }

    private static void setVisibleParts(HumanoidModel<?> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        model.head.visible = slot == EquipmentSlot.HEAD;
        model.body.visible = slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS;
        model.rightArm.visible = slot == EquipmentSlot.CHEST;
        model.leftArm.visible = slot == EquipmentSlot.CHEST;
        model.rightLeg.visible = slot == EquipmentSlot.LEGS;
        model.leftLeg.visible = slot == EquipmentSlot.LEGS;
    }

    private static void copyPose(HumanoidModel<?> original, HumanoidModel<?> model) {
        model.young = original.young;
        model.crouching = original.crouching;
        model.riding = original.riding;
        model.rightArmPose = original.rightArmPose;
        model.leftArmPose = original.leftArmPose;
    }
}
