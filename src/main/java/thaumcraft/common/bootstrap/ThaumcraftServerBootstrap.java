package thaumcraft.common.bootstrap;

import net.neoforged.bus.api.IEventBus;
import thaumcraft.common.services.ServerServices;

public final class ThaumcraftServerBootstrap {
    private ThaumcraftServerBootstrap() {
    }

    public static void register(IEventBus gameEventBus) {
        ServerServices.get().registerServerEventHandlers(gameEventBus);
    }
}
