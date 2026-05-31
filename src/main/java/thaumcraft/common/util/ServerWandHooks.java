package thaumcraft.common.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.services.ServerServices;

public final class ServerWandHooks {
    private ServerWandHooks() {
    }

    public static InteractionResultHolder<ItemStack> use(WandCastingItem wandItem, Level level, Player player,
            InteractionHand hand, ItemStack wand) {
        return ServerServices.get().useWand(wandItem, level, player, hand, wand);
    }

    public static InteractionResult useOnAfterWandable(WandCastingItem wandItem, UseOnContext context) {
        return ServerServices.get().useWandOnAfterWandable(wandItem, context);
    }
}
