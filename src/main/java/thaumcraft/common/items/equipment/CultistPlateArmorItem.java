package thaumcraft.common.items.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.client.renderers.models.armor.KnightArmorModel;
import thaumcraft.client.renderers.CustomModelLayer;

public class CultistPlateArmorItem extends ArmorItem implements IRunicArmor {

    public CultistPlateArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            private KnightArmorModel model;

            @Override
            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot,
                                                          @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    EntityModelSet models = Minecraft.getInstance().getEntityModels();
                    ModelPart part = models.bakeLayer(CustomModelLayer.KNIGHT_ARMOR);
                    model = new KnightArmorModel(part);
                }
                model.setAllVisible(false);
                model.head.visible = slot == EquipmentSlot.HEAD;
                model.body.visible = slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS;
                model.rightArm.visible = slot == EquipmentSlot.CHEST;
                model.leftArm.visible = slot == EquipmentSlot.CHEST;
                model.rightLeg.visible = slot == EquipmentSlot.LEGS;
                model.leftLeg.visible = slot == EquipmentSlot.LEGS;
                model.young = original.young;
                model.crouching = original.crouching;
                model.riding = original.riding;
                model.rightArmPose = original.rightArmPose;
                model.leftArmPose = original.leftArmPose;
                return model;
            }
        };
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }
}



