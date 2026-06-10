package thaumcraft.common.items.equipment;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.items.equipment.TCTiers;

public class ThaumiumHoeItem extends HoeItem {
    public ThaumiumHoeItem() {
        super(TCTiers.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(HoeItem.createAttributes(TCTiers.THAUMIUM, -3.0F, 0.0F)));
    }
}




