package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.Unbreakable;

public class IchorShovelItem extends ShovelItem {
    public IchorShovelItem(Properties properties) {
        super(IchoriumTier.INSTANCE, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}

