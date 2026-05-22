package thaumcraft.common.items.curios;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.common.lib.RunicShielding;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ThaumcraftCurioItem extends Item implements ICurioItem {
    private final String slot;

    public ThaumcraftCurioItem(String slot, Properties properties) {
        super(properties.stacksTo(1));
        this.slot = slot;
    }

    public String getCuriosSlot() {
        return this.slot;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals(this.slot);
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, Item.TooltipContext context, ItemStack stack) {
        List<Component> result = new ArrayList<>(tooltips);
        result.add(Component.translatable("curios.identifier." + this.slot));
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        int charge = RunicShielding.getFinalCharge(stack);
        if (charge > 0) {
            tooltipComponents.add(Component.translatable("item.runic.charge").append(" +" + charge)
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
