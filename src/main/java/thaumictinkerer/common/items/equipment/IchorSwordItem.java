package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.Unbreakable;

public class IchorSwordItem extends SwordItem {
    public IchorSwordItem(Properties properties) {
        super(IchoriumTier.INSTANCE, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}

