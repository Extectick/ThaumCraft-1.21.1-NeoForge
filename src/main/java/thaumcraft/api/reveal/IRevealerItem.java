package thaumcraft.api.reveal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IRevealerItem {
    boolean showNodes(ItemStack stack, LivingEntity entity);

    default boolean showIngamePopups(ItemStack stack, LivingEntity entity) {
        return showNodes(stack, entity);
    }
}
