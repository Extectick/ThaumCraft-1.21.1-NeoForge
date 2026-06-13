package thaumcraft.common.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.IWarpingGear;

public class SinisterLodestoneItem extends Item implements IWarpingGear {
    public SinisterLodestoneItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }
}
