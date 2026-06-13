package thaumcraft.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SanityCheckerItem extends Item {
    public SanityCheckerItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}
