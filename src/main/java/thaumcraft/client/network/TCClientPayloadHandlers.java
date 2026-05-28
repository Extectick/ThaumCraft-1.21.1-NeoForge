package thaumcraft.client.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.WarpMessagePayload;

public final class TCClientPayloadHandlers {
    private TCClientPayloadHandlers() {
    }

    public static void handleEssentiaSourceFx(EssentiaSourceFxPayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handleEssentiaSourceFx(payload);
    }

    public static void handleInfusionSourceFx(InfusionSourceFxPayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handleInfusionSourceFx(payload);
    }

    public static void handleBlockZapFx(BlockZapFxPayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handleBlockZapFx(payload);
    }

    public static void handlePedestalSparkleFx(PedestalSparkleFxPayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handlePedestalSparkleFx(payload);
    }

    public static void handleWarpMessage(WarpMessagePayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handleWarpMessage(payload);
    }

    public static void handleResearchComplete(ResearchCompleteNotificationPayload payload, IPayloadContext context) {
        TCClientPayloadHandler.handleResearchComplete(payload);
    }
}
