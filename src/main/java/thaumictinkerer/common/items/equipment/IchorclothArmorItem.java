package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

public class IchorclothArmorItem extends ArmorItem implements IVisDiscountGear {

    public IchorclothArmorItem(DeferredHolder<ArmorMaterial, ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return (this.getType() == Type.HELMET || this.getType() == Type.BOOTS) ? 3 : 4;
    }
}

