package thaumcraft.common.services;

import java.util.ServiceLoader;

public final class ClientServices {
    private static final ThaumcraftClientServices INSTANCE = ServiceLoader.load(ThaumcraftClientServices.class)
            .findFirst()
            .orElseGet(ThaumcraftClientServices.Empty::new);

    private ClientServices() {
    }

    public static ThaumcraftClientServices get() {
        return INSTANCE;
    }
}
