package thaumcraft.common.items.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IRunicArmor;
import thaumcraft.common.registry.TCItems;

public class BootsTravellerItem extends ArmorItem implements IRunicArmor {

    public BootsTravellerItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(350));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) { // TODO: replace with IRepairable vis-based auto-repair when vis system is implemented
        return repair.is(TCItems.ENCHANTED_FABRIC.get()) || super.isValidRepairItem(toRepair, repair);
    }
}
