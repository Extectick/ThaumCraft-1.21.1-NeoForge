package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.Unbreakable;

public class IchorPickaxeItem extends PickaxeItem {
    public IchorPickaxeItem(Properties properties) {
        super(IchoriumTier.INSTANCE, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}

