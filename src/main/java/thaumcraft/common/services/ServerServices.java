package thaumcraft.common.services;

import java.util.ServiceLoader;

import thaumcraft.Thaumcraft;

public final class ServerServices {
    private static final ThaumcraftServerServices INSTANCE = load();

    private ServerServices() {
    }

    public static ThaumcraftServerServices get() {
        return INSTANCE;
    }

    private static ThaumcraftServerServices load() {
        try {
            return ServiceLoader.load(ThaumcraftServerServices.class)
                    .findFirst()
                    .orElseGet(ThaumcraftServerServices.Empty::new);
        } catch (RuntimeException exception) {
            Thaumcraft.LOGGER.error("Unable to load Thaumcraft server services", exception);
            return new ThaumcraftServerServices.Empty();
        }
    }
}
