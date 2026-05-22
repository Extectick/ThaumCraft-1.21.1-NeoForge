package thaumcraft.api.aspects;

import java.util.List;

public enum Aspect {
    AIR("air", 0xffff7e),
    FIRE("fire", 0xff5a01),
    WATER("water", 0x3cd4fc),
    EARTH("earth", 0x56c000),
    ORDER("order", 0xd5d4ec),
    ENTROPY("entropy", 0x404040);

    private static final List<Aspect> PRIMAL_ASPECTS = List.of(AIR, FIRE, WATER, EARTH, ORDER, ENTROPY);

    private final String tag;
    private final int color;

    Aspect(String tag, int color) {
        this.tag = tag;
        this.color = color;
    }

    public static List<Aspect> getPrimalAspects() {
        return PRIMAL_ASPECTS;
    }

    public String getTag() {
        return this.tag;
    }

    public int getColor() {
        return this.color;
    }
}
