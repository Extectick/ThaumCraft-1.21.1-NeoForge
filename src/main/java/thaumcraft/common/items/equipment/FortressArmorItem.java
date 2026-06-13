package thaumcraft.common.items.equipment;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.common.registry.TCDataComponents;

public class FortressArmorItem extends ArmorItem implements IRunicArmor {

    public FortressArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(getMaxDurability(type)));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int mask = getMask(stack);
        if (mask >= 0) {
            tooltip.add(Component.translatable("item.HelmetFortress.mask." + mask).withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, context, tooltip, flag);
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



