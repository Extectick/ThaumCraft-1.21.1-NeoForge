package thaumcraft.common.util;

import java.util.function.BooleanSupplier;

public final class ClientInteractionState {
    private static BooleanSupplier shiftDown = () -> false;

    private ClientInteractionState() {
    }

    public static void registerShiftDown(BooleanSupplier supplier) {
        shiftDown = supplier == null ? () -> false : supplier;
    }

    public static boolean isShiftDown() {
        return shiftDown.getAsBoolean();
    }
}
