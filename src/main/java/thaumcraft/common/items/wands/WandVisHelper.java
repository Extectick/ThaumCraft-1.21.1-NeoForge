package thaumcraft.common.items.wands;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.registry.TCDataComponents;

public final class WandVisHelper {
    public static final int WOOD_IRON_MAX_VIS = 2500;

    private WandVisHelper() {
    }

    public static int getVis(ItemStack stack, Aspect aspect) {
        if (stack.isEmpty() || aspect == null) {
            return 0;
        }
        return stack.getOrDefault(TCDataComponents.WAND_VIS, PrimalVisStorage.EMPTY).get(aspect);
    }

    public static void setVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack.isEmpty() || aspect == null) {
            return;
        }

        PrimalVisStorage storage = stack.getOrDefault(TCDataComponents.WAND_VIS, PrimalVisStorage.EMPTY)
                .with(aspect, amount, getMaxVis(stack));
        if (storage.isEmpty()) {
            stack.remove(TCDataComponents.WAND_VIS);
        } else {
            stack.set(TCDataComponents.WAND_VIS, storage);
        }
    }

    public static int addVis(ItemStack stack, Aspect aspect, int amount) {
        if (amount <= 0) {
            return 0;
        }

        int current = getVis(stack, aspect);
        int inserted = Math.min(amount, Math.max(0, getMaxVis(stack) - current));
        if (inserted > 0) {
            setVis(stack, aspect, current + inserted);
        }
        return inserted;
    }

    public static boolean consumeVis(ItemStack stack, Aspect aspect, int amount) {
        if (!hasEnoughVis(stack, aspect, amount)) {
            return false;
        }

        setVis(stack, aspect, getVis(stack, aspect) - amount);
        return true;
    }

    public static int getMaxVis(ItemStack stack) {
        if (stack.getItem() instanceof WandCastingItem wand) {
            return wand.getMaxVis(stack);
        }
        return 0;
    }

    public static boolean hasEnoughVis(ItemStack stack, Aspect aspect, int amount) {
        return amount <= 0 || getVis(stack, aspect) >= amount;
    }

    public static ItemStack fillAllVis(ItemStack stack) {
        int max = getMaxVis(stack);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            setVis(stack, aspect, max);
        }
        return stack;
    }
}
