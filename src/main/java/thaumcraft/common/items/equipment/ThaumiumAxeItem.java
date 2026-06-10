package thaumcraft.common.items.equipment;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.items.equipment.TCTiers;

public class ThaumiumAxeItem extends AxeItem {
    public ThaumiumAxeItem() {
        super(TCTiers.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(AxeItem.createAttributes(TCTiers.THAUMIUM, 5.0F, -3.0F)));
    }
}




