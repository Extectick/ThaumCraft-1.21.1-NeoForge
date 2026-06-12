package thaumictinkerer.common.items.equipment;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpellClothItem extends Item {
    public SpellClothItem(Properties properties) {
        super(properties.stacksTo(1).durability(35));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        copy.setDamageValue(itemStack.getDamageValue() + 1);
        if (copy.getDamageValue() >= copy.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return copy;
    }
}

