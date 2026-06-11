package thaumcraft.common.items.equipment;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

import java.util.List;

public class RobeArmorItem extends ArmorItem implements IRunicArmor, IVisDiscountGear {

    private static final int DEFAULT_COLOR = 6961280;

    public RobeArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().component(DataComponents.DYED_COLOR, new DyedItemColor(DEFAULT_COLOR, true))
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .durability(getMaxDurability(type)));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return switch (this.getType()) {
            case CHESTPLATE, LEGGINGS -> 2;
            case BOOTS -> 1;
            default -> 0;
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int discount = getVisDiscount(stack, null, null);
        if (discount > 0) {
            tooltip.add(Component.literal("§5Vis Discount: " + discount + "%"));
        }
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        return repair.is(net.minecraft.world.item.Items.NETHERITE_INGOT) || super.isValidRepairItem(toRepair, repair);
    }

    private static int getMaxDurability(Type type) {
        return switch (type) {
            case HELMET -> 55;
            case CHESTPLATE -> 80;
            case LEGGINGS -> 75;
            case BOOTS -> 65;
            default -> 55;
        };
    }
}



