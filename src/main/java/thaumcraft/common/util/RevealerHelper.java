package thaumcraft.common.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.reveal.IRevealerItem;

public final class RevealerHelper {
    private RevealerHelper() {
    }

    public static boolean showsNodes(LivingEntity entity) {
        return showsNodesInHand(entity.getMainHandItem(), entity)
                || showsNodesInHand(entity.getOffhandItem(), entity)
                || showsNodes(entity.getItemBySlot(EquipmentSlot.HEAD), entity)
                || showsNodes(entity.getItemBySlot(EquipmentSlot.CHEST), entity)
                || showsNodes(entity.getItemBySlot(EquipmentSlot.LEGS), entity)
                || showsNodes(entity.getItemBySlot(EquipmentSlot.FEET), entity);
    }

    public static boolean showsNodes(ItemStack stack, LivingEntity entity) {
        return !stack.isEmpty()
                && stack.getItem() instanceof IRevealerItem revealer
                && revealer.showNodes(stack, entity);
    }

    public static boolean showsIngamePopups(LivingEntity entity) {
        return showsIngamePopupsInHand(entity.getMainHandItem(), entity)
                || showsIngamePopupsInHand(entity.getOffhandItem(), entity)
                || showsIngamePopups(entity.getItemBySlot(EquipmentSlot.HEAD), entity)
                || showsIngamePopups(entity.getItemBySlot(EquipmentSlot.CHEST), entity)
                || showsIngamePopups(entity.getItemBySlot(EquipmentSlot.LEGS), entity)
                || showsIngamePopups(entity.getItemBySlot(EquipmentSlot.FEET), entity);
    }

    public static boolean showsIngamePopups(ItemStack stack, LivingEntity entity) {
        return !stack.isEmpty()
                && stack.getItem() instanceof IRevealerItem revealer
                && revealer.showIngamePopups(stack, entity);
    }

    private static boolean showsNodesInHand(ItemStack stack, LivingEntity entity) {
        return !stack.isEmpty()
                && !(stack.getItem() instanceof ArmorItem)
                && showsNodes(stack, entity);
    }

    private static boolean showsIngamePopupsInHand(ItemStack stack, LivingEntity entity) {
        return !stack.isEmpty()
                && !(stack.getItem() instanceof ArmorItem)
                && showsIngamePopups(stack, entity);
    }
}
