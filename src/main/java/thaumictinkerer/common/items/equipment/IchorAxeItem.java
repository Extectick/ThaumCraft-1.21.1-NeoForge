package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.component.Unbreakable;

public class IchorAxeItem extends AxeItem {
    public IchorAxeItem(Properties properties) {
        super(IchoriumTier.INSTANCE, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}

