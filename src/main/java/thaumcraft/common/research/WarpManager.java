package thaumcraft.common.research;

import net.minecraft.world.entity.player.Player;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.util.ServerWarpHooks;

public final class WarpManager {
    private WarpManager() {
    }

    public static WarpData get(Player player) {
        return player == null ? WarpData.EMPTY : player.getData(TCDataAttachments.WARP);
    }

    public static void addWarpToPlayer(Player player, int amount, boolean temporary) {
        ServerWarpHooks.addWarpToPlayer(player, amount, temporary);
    }

    public static void addStickyWarpToPlayer(Player player, int amount) {
        ServerWarpHooks.addStickyWarpToPlayer(player, amount);
    }
}
