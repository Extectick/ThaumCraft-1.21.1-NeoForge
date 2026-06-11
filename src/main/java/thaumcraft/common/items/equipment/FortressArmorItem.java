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
import thaumcraft.client.renderers.models.armor.FortressArmorModel;
import thaumcraft.client.renderers.CustomModelLayer;
import thaumcraft.common.registry.TCDataComponents;

public class FortressArmorItem extends ArmorItem implements IRunicArmor {

    public FortressArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(getMaxDurability(type)));
    }

    public static IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            private FortressArmorModel model;

            @Override
            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot,
                                                          @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    EntityModelSet models = Minecraft.getInstance().getEntityModels();
                    ModelPart part = models.bakeLayer(CustomModelLayer.FORTRESS_ARMOR);
                    model = new FortressArmorModel(part);
                }
                model.setupVisibility(living, slot);
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


    public static boolean hasGoggles(ItemStack stack) {
        return stack.has(TCDataComponents.FORTRESS_GOGGLES);
    }

    public static int getMask(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.FORTRESS_MASK, -1);
    }

    public static ItemStack withGoggles(ItemStack stack) {
        stack.set(TCDataComponents.FORTRESS_GOGGLES, true);
        return stack;
    }

    public static ItemStack withMask(ItemStack stack, int maskType) {
        stack.set(TCDataComponents.FORTRESS_MASK, maskType);
        return stack;
    }

    private static int getMaxDurability(Type type) {
        return switch (type) {
            case HELMET -> 462;
            case CHESTPLATE -> 672;
            case LEGGINGS -> 630;
            default -> 546;
        };
    }
}



