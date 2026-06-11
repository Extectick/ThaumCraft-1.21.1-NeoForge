package thaumcraft.common.items.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;

public class CultistLeaderArmorItem extends ArmorItem implements IRunicArmor {

    public CultistLeaderArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }
}



