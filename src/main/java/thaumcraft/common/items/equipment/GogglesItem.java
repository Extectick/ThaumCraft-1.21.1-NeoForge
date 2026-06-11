package thaumcraft.common.items.equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.items.equipment.TCArmorMaterials;

public class GogglesItem extends ArmorItem {

    public GogglesItem() {
        super(TCArmorMaterials.GOGGLES, Type.HELMET, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(350));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

}




