package thaumcraft.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.services.ServerServices;

public final class ServerResearchHooks {
    private ServerResearchHooks() {
    }

    public static boolean grantResearchTree(Player player, String key) {
        return ServerServices.get().grantResearchTree(player, key);
    }

    public static int grantAllResearch(Player player) {
        return ServerServices.get().grantAllResearch(player);
    }

    public static int resetResearch(Player player) {
        return ServerServices.get().resetResearch(player);
    }

    public static boolean tryCreateResearchNote(ServerPlayer player, String key) {
        return ServerServices.get().tryCreateResearchNote(player, key);
    }

    public static boolean completeResearch(Player player, String key) {
        return ServerServices.get().completeResearch(player, key);
    }

    public static void tickResearchTable(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity table) {
        ServerServices.get().tickResearchTable(level, pos, state, table);
    }
}
