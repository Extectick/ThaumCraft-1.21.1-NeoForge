package thaumcraft.common.curios;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.registry.TCItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class ThaumcraftCuriosCompat {
    private ThaumcraftCuriosCompat() {
    }

    public static List<SlotResult> findEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findCurios(predicate))
                .orElse(List.of());
    }

    public static Optional<SlotResult> findFirstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(predicate));
    }

    public static Optional<ItemStack> getStackInSlot(LivingEntity entity, String slot, int index) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findCurio(slot, index))
                .map(SlotResult::stack);
    }

    public static boolean setStackInSlot(LivingEntity entity, String slot, int index, ItemStack stack) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> {
                    handler.setEquippedCurio(slot, index, stack);
                    return true;
                })
                .orElse(false);
    }

    public static int getRunicCharge(LivingEntity entity) {
        return findEquipped(entity, stack -> stack.getItem() instanceof IRunicArmor).stream()
                .map(SlotResult::stack)
                .mapToInt(stack -> ((IRunicArmor) stack.getItem()).getRunicCharge(stack))
                .sum();
    }

    public static int getVisDiscount(Player player, Aspect aspect) {
        return findEquipped(player, stack -> stack.getItem() instanceof IVisDiscountGear).stream()
                .map(SlotResult::stack)
                .mapToInt(stack -> ((IVisDiscountGear) stack.getItem()).getVisDiscount(stack, player, aspect))
                .sum();
    }

    public static Optional<SlotResult> findFocusPouch(LivingEntity entity) {
        return findFirstEquipped(entity, stack -> stack.is(TCItems.FOCUS_POUCH.get()));
    }

    public static boolean isHoverGirdleEquipped(LivingEntity entity) {
        return findFirstEquipped(entity, stack -> stack.is(TCItems.HOVER_GIRDLE.get())).isPresent();
    }
}
