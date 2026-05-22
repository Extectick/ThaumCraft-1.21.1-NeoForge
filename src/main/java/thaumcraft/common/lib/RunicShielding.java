package thaumcraft.common.lib;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.IRunicArmor;

public final class RunicShielding {
    public static final String HARDENING_KEY = "RS.HARDEN";

    private RunicShielding() {
    }

    public static int getFinalCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof IRunicArmor armor)) {
            return 0;
        }
        return Math.max(0, armor.getRunicCharge(stack) + getHardening(stack));
    }

    public static int getHardening(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!customData.contains(HARDENING_KEY)) {
            return 0;
        }

        CompoundTag tag = customData.copyTag();
        return Math.max(0, tag.getByte(HARDENING_KEY));
    }
}
