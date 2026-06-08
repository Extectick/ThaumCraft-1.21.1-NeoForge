package thaumcraft.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class TCNetwork {
    private TCNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(CycleWandFocusPayload.TYPE, CycleWandFocusPayload.STREAM_CODEC,
                TCPayloadHandlerBridge::handleCycleWandFocus);
        registrar.playToServer(ResearchTablePlaceAspectPayload.TYPE, ResearchTablePlaceAspectPayload.STREAM_CODEC,
                TCPayloadHandlerBridge::handleResearchTablePlaceAspect);
        registrar.playToServer(ResearchTableCombineAspectPayload.TYPE, ResearchTableCombineAspectPayload.STREAM_CODEC,
                TCPayloadHandlerBridge::handleResearchTableCombineAspect);
        registrar.playToServer(ThaumonomiconCreateNotePayload.TYPE, ThaumonomiconCreateNotePayload.STREAM_CODEC,
                TCPayloadHandlerBridge::handleThaumonomiconCreateNote);
        registrar.playToClient(EssentiaSourceFxPayload.TYPE,
                EssentiaSourceFxPayload.STREAM_CODEC, TCPayloadHandlerBridge::handleEssentiaSourceFx);
        registrar.playToClient(InfusionSourceFxPayload.TYPE,
                InfusionSourceFxPayload.STREAM_CODEC, TCPayloadHandlerBridge::handleInfusionSourceFx);
        registrar.playToClient(BlockZapFxPayload.TYPE,
                BlockZapFxPayload.STREAM_CODEC, TCPayloadHandlerBridge::handleBlockZapFx);
        registrar.playToClient(PedestalSparkleFxPayload.TYPE,
                PedestalSparkleFxPayload.STREAM_CODEC, TCPayloadHandlerBridge::handlePedestalSparkleFx);
        registrar.playToClient(ThaumometerScanFxPayload.TYPE,
                ThaumometerScanFxPayload.STREAM_CODEC, TCPayloadHandlerBridge::handleThaumometerScanFx);
        registrar.playToClient(ThaumometerScanMessagePayload.TYPE,
                ThaumometerScanMessagePayload.STREAM_CODEC, TCPayloadHandlerBridge::handleThaumometerScanMessage);
        registrar.playToClient(WarpMessagePayload.TYPE,
                WarpMessagePayload.STREAM_CODEC, TCPayloadHandlerBridge::handleWarpMessage);
        registrar.playToClient(ResearchCompleteNotificationPayload.TYPE,
                ResearchCompleteNotificationPayload.STREAM_CODEC, TCPayloadHandlerBridge::handleResearchComplete);
    }
}
