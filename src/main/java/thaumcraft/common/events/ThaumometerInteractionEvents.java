package thaumcraft.common.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.services.ServerServices;

public final class ThaumometerInteractionEvents {
    private ThaumometerInteractionEvents() {
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        if (isHoldingThaumometer(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (isHoldingThaumometer(event.getEntity())) {
            event.setUseBlock(TriState.FALSE);
            event.setUseItem(TriState.FALSE);
            event.setCanceled(true);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!isHoldingThaumometer(player)) {
            return;
        }

        event.setUseBlock(TriState.FALSE);
        event.setUseItem(TriState.FALSE);
        if (event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            player.startUsingItem(event.getHand());
        }
        if (!player.level().isClientSide && event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            ServerServices.get().startThaumometerBlockScan(player, event.getHand(), event.getHitVec());
        }
        event.setCancellationResult(InteractionResult.CONSUME);
        event.setCanceled(true);
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!isHoldingThaumometer(player)) {
            return;
        }

        if (!event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            return;
        }

        if (!player.level().isClientSide) {
            ServerServices.get().startThaumometerScan(player, event.getHand());
        }
    }

    private static boolean isHoldingThaumometer(Player player) {
        return player.getMainHandItem().is(TCItems.THAUMOMETER.get())
                || player.getOffhandItem().is(TCItems.THAUMOMETER.get());
    }
}
