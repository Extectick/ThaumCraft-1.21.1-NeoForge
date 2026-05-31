package thaumcraft.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.services.ClientServices;
import thaumcraft.common.services.ServerServices;

public final class TCPayloadHandlerBridge {
    private TCPayloadHandlerBridge() {
    }

    public static void handleCycleWandFocus(CycleWandFocusPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ServerServices.get().handleCycleWandFocus(payload, player);
        }
    }

    public static void handleResearchTablePlaceAspect(ResearchTablePlaceAspectPayload payload,
            IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ServerServices.get().handleResearchTablePlaceAspect(payload, player);
        }
    }

    public static void handleResearchTableCombineAspect(ResearchTableCombineAspectPayload payload,
            IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ServerServices.get().handleResearchTableCombineAspect(payload, player);
        }
    }

    public static void handleThaumonomiconCreateNote(ThaumonomiconCreateNotePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ServerServices.get().handleThaumonomiconCreateNote(payload, player);
        }
    }

    public static void handleResearchComplete(ResearchCompleteNotificationPayload payload, IPayloadContext context) {
        ClientServices.get().handleResearchComplete(payload);
    }

    public static void handleEssentiaSourceFx(EssentiaSourceFxPayload payload, IPayloadContext context) {
        ClientServices.get().handleEssentiaSourceFx(payload);
    }

    public static void handleInfusionSourceFx(InfusionSourceFxPayload payload, IPayloadContext context) {
        ClientServices.get().handleInfusionSourceFx(payload);
    }

    public static void handleBlockZapFx(BlockZapFxPayload payload, IPayloadContext context) {
        ClientServices.get().handleBlockZapFx(payload);
    }

    public static void handlePedestalSparkleFx(PedestalSparkleFxPayload payload, IPayloadContext context) {
        ClientServices.get().handlePedestalSparkleFx(payload);
    }

    public static void handleWarpMessage(WarpMessagePayload payload, IPayloadContext context) {
        ClientServices.get().handleWarpMessage(payload);
    }
}
