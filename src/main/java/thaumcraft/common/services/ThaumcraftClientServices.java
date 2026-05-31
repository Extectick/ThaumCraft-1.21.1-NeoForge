package thaumcraft.common.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.WarpMessagePayload;

public interface ThaumcraftClientServices {
    final class Empty implements ThaumcraftClientServices {
    }

    default void handleResearchComplete(ResearchCompleteNotificationPayload payload) {
    }

    default void handleEssentiaSourceFx(EssentiaSourceFxPayload payload) {
    }

    default void handleInfusionSourceFx(InfusionSourceFxPayload payload) {
    }

    default void handleBlockZapFx(BlockZapFxPayload payload) {
    }

    default void handlePedestalSparkleFx(PedestalSparkleFxPayload payload) {
    }

    default void handleWarpMessage(WarpMessagePayload payload) {
    }

    default void blockRunes(Level level, BlockPos pos) {
    }

    default void instabilityBolt(Level level, BlockPos pos, int instability) {
    }
}
