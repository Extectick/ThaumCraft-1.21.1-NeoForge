package thaumcraft.common.util;

import net.minecraft.world.entity.player.Player;
import thaumcraft.common.services.ServerServices;

public final class ServerWarpHooks {
    private ServerWarpHooks() {
    }

    public static void addWarpToPlayer(Player player, int amount, boolean temporary) {
        ServerServices.get().addWarpToPlayer(player, amount, temporary);
    }

    public static void addStickyWarpToPlayer(Player player, int amount) {
        ServerServices.get().addStickyWarpToPlayer(player, amount);
    }
}
