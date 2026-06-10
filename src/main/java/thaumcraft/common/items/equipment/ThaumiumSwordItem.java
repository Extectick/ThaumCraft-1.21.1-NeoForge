package thaumcraft.common.items.equipment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import thaumcraft.common.items.equipment.TCTiers;

public class ThaumiumSwordItem extends SwordItem {
    public ThaumiumSwordItem() {
        super(TCTiers.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(TCTiers.THAUMIUM, 3, -2.4F)));
    }
}




