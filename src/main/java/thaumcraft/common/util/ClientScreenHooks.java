package thaumcraft.common.util;

public final class ClientScreenHooks {
    private static Runnable thaumonomiconOpener = () -> {
    };

    private ClientScreenHooks() {
    }

    public static void registerThaumonomiconOpener(Runnable opener) {
        thaumonomiconOpener = opener == null ? () -> {
        } : opener;
    }

    public static void openThaumonomicon() {
        thaumonomiconOpener.run();
    }
}
