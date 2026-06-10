package thaumcraft.common.items.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;

public class ThaumiumArmorItem extends ArmorItem implements IRunicArmor {

    public ThaumiumArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(getMaxDurability(type)));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    private static int getMaxDurability(Type type) {
        return switch (type) {
            case HELMET -> 275;
            case CHESTPLATE -> 400;
            case LEGGINGS -> 375;
            case BOOTS -> 325;
            default -> 275;
        };
    }
}



