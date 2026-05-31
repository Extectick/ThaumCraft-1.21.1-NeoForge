package thaumcraft.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.InfusionMatrixClientFx;
import thaumcraft.client.network.TCClientPayloadHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.WarpMessagePayload;
import thaumcraft.common.services.ThaumcraftClientServices;

public final class ThaumcraftClientServicesProvider implements ThaumcraftClientServices {
    @Override
    public void handleResearchComplete(ResearchCompleteNotificationPayload payload) {
        TCClientPayloadHandler.handleResearchComplete(payload);
    }

    @Override
    public void handleEssentiaSourceFx(EssentiaSourceFxPayload payload) {
        TCClientPayloadHandler.handleEssentiaSourceFx(payload);
    }

    @Override
    public void handleInfusionSourceFx(InfusionSourceFxPayload payload) {
        TCClientPayloadHandler.handleInfusionSourceFx(payload);
    }

    @Override
    public void handleBlockZapFx(BlockZapFxPayload payload) {
        TCClientPayloadHandler.handleBlockZapFx(payload);
    }

    @Override
    public void handlePedestalSparkleFx(PedestalSparkleFxPayload payload) {
        TCClientPayloadHandler.handlePedestalSparkleFx(payload);
    }

    @Override
    public void handleWarpMessage(WarpMessagePayload payload) {
        TCClientPayloadHandler.handleWarpMessage(payload);
    }

    @Override
    public void blockRunes(Level level, BlockPos pos) {
        InfusionMatrixClientFx.blockRunes(level, pos);
    }

    @Override
    public void instabilityBolt(Level level, BlockPos pos, int instability) {
        InfusionMatrixClientFx.instabilityBolt(level, pos, instability);
    }
}
