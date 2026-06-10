package thaumcraft.common.items.equipment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import thaumcraft.common.items.equipment.TCTiers;

public class ThaumiumShovelItem extends ShovelItem {
    public ThaumiumShovelItem() {
        super(TCTiers.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(ShovelItem.createAttributes(TCTiers.THAUMIUM, 1.5F, -3.0F)));
    }
}




