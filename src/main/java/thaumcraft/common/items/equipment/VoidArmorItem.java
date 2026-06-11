package thaumcraft.common.items.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IWarpingGear;

public class VoidArmorItem extends ArmorItem implements IRunicArmor, IWarpingGear {

    public VoidArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(getMaxDurability(type)));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && stack.isDamaged() && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    private static int getMaxDurability(Type type) {
        return switch (type) {
            case HELMET -> 110;
            case CHESTPLATE -> 160;
            case LEGGINGS -> 150;
            case BOOTS -> 130;
            default -> 110;
        };
    }
}



