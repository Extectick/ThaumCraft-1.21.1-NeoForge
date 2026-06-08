package thaumcraft.api.nodes;

import javax.annotation.Nullable;

public enum NodeModifier {
    BRIGHT,
    PALE,
    FADING;

    @Nullable
    public static NodeModifier byOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < values().length ? values()[ordinal] : null;
    }
}
