package thaumcraft.common.items.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.renderers.models.armor.RobeArmorModel;
import thaumcraft.client.renderers.CustomModelLayer;

import java.util.List;

public class VoidRobeArmorItem extends ArmorItem implements IRunicArmor, IVisDiscountGear, IWarpingGear {

    public VoidRobeArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().component(DataComponents.DYED_COLOR, new DyedItemColor(6961280, true)).rarity(Rarity.EPIC).stacksTo(1));
    }

    public static IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            private RobeArmorModel model;

            @Override
            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot,
                                                          @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    EntityModelSet models = Minecraft.getInstance().getEntityModels();
                    ModelPart part = models.bakeLayer(CustomModelLayer.ROBE_ARMOR);
                    model = new RobeArmorModel(part);
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

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return 5;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull net.minecraft.world.entity.Entity entity, int slotId,
                              boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && stack.isDamaged() && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§5Vis Discount: " + getVisDiscount(stack, null, null) + "%"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}



