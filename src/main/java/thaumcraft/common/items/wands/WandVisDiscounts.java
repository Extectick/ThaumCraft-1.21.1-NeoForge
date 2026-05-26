package thaumcraft.common.items.wands;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import top.theillusivec4.curios.api.SlotResult;

public final class WandVisDiscounts {
    private WandVisDiscounts() {
    }

    public static int getTotalVisDiscount(Player player, Aspect aspect) {
        if (player == null) {
            return 0;
        }

        int total = getCuriosDiscount(player, aspect) + getArmorDiscount(player, aspect);
        // TC4 applied Vis Exhaustion here as a negative discount. The effect itself is not ported yet.
        return total;
    }

    private static int getCuriosDiscount(Player player, Aspect aspect) {
        int total = 0;
        for (SlotResult result : ThaumcraftCuriosCompat.findEquipped(player,
                stack -> stack.getItem() instanceof IVisDiscountGear)) {
            total += getStackDiscount(result.stack(), player, aspect);
        }
        return total;
    }

    private static int getArmorDiscount(Player player, Aspect aspect) {
        int total = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            total += getStackDiscount(player.getItemBySlot(slot), player, aspect);
        }
        return total;
    }

    private static int getStackDiscount(ItemStack stack, Player player, Aspect aspect) {
        return stack.getItem() instanceof IVisDiscountGear gear ? gear.getVisDiscount(stack, player, aspect) : 0;
    }
}
