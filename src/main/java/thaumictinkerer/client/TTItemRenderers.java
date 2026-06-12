package thaumictinkerer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import thaumictinkerer.client.models.KamiArmorModel;
import thaumictinkerer.common.registry.TTItems;

public final class TTItemRenderers {
    private TTItemRenderers() {
    }

    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(advancedIchorArmor(),
                TTItems.ADVANCED_ICHOR_HELMET.get(),
                TTItems.ADVANCED_ICHOR_CHESTPLATE.get(),
                TTItems.ADVANCED_ICHOR_LEGGINGS.get(),
                TTItems.ADVANCED_ICHOR_BOOTS.get());
    }

    private static IClientItemExtensions advancedIchorArmor() {
        return new IClientItemExtensions() {
            private KamiArmorModel model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living,
                    @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
                if (slot != EquipmentSlot.CHEST) {
                    return original;
                }
                if (model == null) {
                    ModelPart part = Minecraft.getInstance().getEntityModels().bakeLayer(TTClientSetup.KAMI_ARMOR_LAYER);
                    model = new KamiArmorModel(part);
                }
                copyPose(original, model);
                return model;
            }
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyPose(HumanoidModel<?> original, HumanoidModel<?> model) {
        ((HumanoidModel) original).copyPropertiesTo((HumanoidModel) model);
        model.young = original.young;
        model.crouching = original.crouching;
        model.riding = original.riding;
        model.rightArmPose = original.rightArmPose;
        model.leftArmPose = original.leftArmPose;
    }
}
