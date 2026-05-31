package thaumcraft.server.warp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.common.network.WarpMessagePayload;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.research.WarpData;
import thaumcraft.common.research.WarpManager;

public final class ServerWarpService {
    private ServerWarpService() {
    }

    public static void addWarpToPlayer(Player player, int amount, boolean temporary) {
        if (!(player instanceof ServerPlayer serverPlayer) || amount == 0) {
            return;
        }
        if (!temporary && amount < 0) {
            return;
        }

        WarpData data = WarpManager.get(serverPlayer);
        WarpData updated;
        byte type;
        if (temporary) {
            if (amount < 0 && data.temporary() <= 0) {
                return;
            }
            updated = data.addTemporary(amount);
            type = 2;
        } else {
            updated = data.addPermanent(amount);
            type = 0;
        }
        set(serverPlayer, updated);
        PacketDistributor.sendToPlayer(serverPlayer, new WarpMessagePayload(type, amount));
    }

    public static void addStickyWarpToPlayer(Player player, int amount) {
        if (!(player instanceof ServerPlayer serverPlayer) || amount == 0) {
            return;
        }
        WarpData data = WarpManager.get(serverPlayer);
        if (amount < 0 && data.sticky() <= 0) {
            return;
        }
        set(serverPlayer, data.addSticky(amount));
        PacketDistributor.sendToPlayer(serverPlayer, new WarpMessagePayload((byte) 1, amount));
    }

    private static void set(ServerPlayer player, WarpData data) {
        player.setData(TCDataAttachments.WARP, data);
        player.syncData(TCDataAttachments.WARP);
    }
}
