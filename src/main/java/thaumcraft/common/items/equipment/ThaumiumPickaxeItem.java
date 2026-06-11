package thaumcraft.common.items.equipment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.items.equipment.TCTiers;

public class ThaumiumPickaxeItem extends PickaxeItem {
    public ThaumiumPickaxeItem() {
        super(TCTiers.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(PickaxeItem.createAttributes(TCTiers.THAUMIUM, 1.0F, -2.8F)));
    }
}




