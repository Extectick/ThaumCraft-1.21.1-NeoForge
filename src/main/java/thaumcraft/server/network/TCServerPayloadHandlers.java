package thaumcraft.server.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.network.CycleWandFocusPayload;
import thaumcraft.common.network.ResearchTableCombineAspectPayload;
import thaumcraft.common.network.ResearchTablePlaceAspectPayload;
import thaumcraft.common.network.ThaumonomiconCreateNotePayload;

public final class TCServerPayloadHandlers {
    private TCServerPayloadHandlers() {
    }

    public static void handleCycleWandFocus(CycleWandFocusPayload payload, IPayloadContext context) {
        CycleWandFocusPayload.handle(payload, context);
    }

    public static void handleResearchTablePlaceAspect(ResearchTablePlaceAspectPayload payload, IPayloadContext context) {
        ResearchTablePlaceAspectPayload.handle(payload, context);
    }

    public static void handleResearchTableCombineAspect(ResearchTableCombineAspectPayload payload, IPayloadContext context) {
        ResearchTableCombineAspectPayload.handle(payload, context);
    }

    public static void handleThaumonomiconCreateNote(ThaumonomiconCreateNotePayload payload, IPayloadContext context) {
        ThaumonomiconCreateNotePayload.handle(payload, context);
    }
}
