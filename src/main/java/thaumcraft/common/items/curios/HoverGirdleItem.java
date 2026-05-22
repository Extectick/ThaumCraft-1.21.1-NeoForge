package thaumcraft.common.items.curios;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IRunicArmor;
import thaumcraft.common.curios.TCSlots;
import top.theillusivec4.curios.api.SlotContext;

public class HoverGirdleItem extends ThaumcraftCurioItem implements IRunicArmor {
    public HoverGirdleItem(Properties properties) {
        super(TCSlots.BELT, properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().fallDistance > 0.0F) {
            slotContext.entity().fallDistance = Math.max(0.0F, slotContext.entity().fallDistance - 0.33F);
        }
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return 0;
    }
}
