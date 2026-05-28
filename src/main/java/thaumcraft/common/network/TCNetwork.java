package thaumcraft.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class TCNetwork {
    private TCNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(CycleWandFocusPayload.TYPE, CycleWandFocusPayload.STREAM_CODEC,
                CycleWandFocusPayload::handle);
        registrar.playToServer(ResearchTablePlaceAspectPayload.TYPE, ResearchTablePlaceAspectPayload.STREAM_CODEC,
                ResearchTablePlaceAspectPayload::handle);
        registrar.playToServer(ResearchTableCombineAspectPayload.TYPE, ResearchTableCombineAspectPayload.STREAM_CODEC,
                ResearchTableCombineAspectPayload::handle);
        registrar.playToServer(ThaumonomiconCreateNotePayload.TYPE, ThaumonomiconCreateNotePayload.STREAM_CODEC,
                ThaumonomiconCreateNotePayload::handle);
        registrar.playToClient(EssentiaSourceFxPayload.TYPE,
                EssentiaSourceFxPayload.STREAM_CODEC, EssentiaSourceFxPayload::handle);
        registrar.playToClient(InfusionSourceFxPayload.TYPE,
                InfusionSourceFxPayload.STREAM_CODEC, InfusionSourceFxPayload::handle);
        registrar.playToClient(BlockZapFxPayload.TYPE,
                BlockZapFxPayload.STREAM_CODEC, BlockZapFxPayload::handle);
        registrar.playToClient(ResearchCompleteNotificationPayload.TYPE,
                ResearchCompleteNotificationPayload.STREAM_CODEC, ResearchCompleteNotificationPayload::handle);
    }
}
