package thaumcraft.common.items.curios;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IRunicArmor;
import top.theillusivec4.curios.api.SlotContext;

public class RunicCurioItem extends ThaumcraftCurioItem implements IRunicArmor {
    private final int runicCharge;

    public RunicCurioItem(String slot, int runicCharge, Properties properties) {
        super(slot, properties);
        this.runicCharge = runicCharge;
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return this.runicCharge;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack previousStack, ItemStack stack) {
        // TODO port EventHandlerRunic dirty-state sync when player runic data is moved.
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // TODO port EventHandlerRunic dirty-state sync when player runic data is moved.
    }
}
