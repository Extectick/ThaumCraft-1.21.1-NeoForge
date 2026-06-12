package thaumictinkerer.common.items.equipment;


import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.client.Minecraft;
import thaumictinkerer.client.TTClientSetup;
import thaumictinkerer.client.models.KamiArmorModel;

import java.util.function.Consumer;

public class AwakenedIchorclothArmorItem extends IchorclothArmorItem {

    public AwakenedIchorclothArmorItem(DeferredHolder<ArmorMaterial, ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (armorSlot == EquipmentSlot.CHEST) {
                    KamiArmorModel model = new KamiArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(TTClientSetup.KAMI_ARMOR_LAYER));
                    _default.copyPropertiesTo((HumanoidModel) model);
                    return model;
                }
                return _default;
            }
        });
    }
}




