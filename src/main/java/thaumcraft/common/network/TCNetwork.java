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
    }
}
