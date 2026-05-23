package thaumcraft.api.aspects;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum Aspect {
    AIR("air", 0xffff7e),
    FIRE("fire", 0xff5a01),
    WATER("water", 0x3cd4fc),
    EARTH("earth", 0x56c000),
    ORDER("order", 0xd5d4ec),
    ENTROPY("entropy", 0x404040);

    private static final List<Aspect> PRIMAL_ASPECTS = List.of(AIR, FIRE, WATER, EARTH, ORDER, ENTROPY);
    public static final Codec<Aspect> CODEC = Codec.STRING.xmap(Aspect::byTagOrThrow, Aspect::getTag);
    public static final StreamCodec<io.netty.buffer.ByteBuf, Aspect> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(Aspect::byTagOrThrow, Aspect::getTag);

    private final String tag;
    private final int color;

    Aspect(String tag, int color) {
        this.tag = tag;
        this.color = color;
    }

    public static List<Aspect> getPrimalAspects() {
        return PRIMAL_ASPECTS;
    }

    public static Optional<Aspect> byTag(String tag) {
        String normalized = tag.toLowerCase(Locale.ROOT);
        for (Aspect aspect : values()) {
            if (aspect.tag.equals(normalized)) {
                return Optional.of(aspect);
            }
        }
        return Optional.empty();
    }

    private static Aspect byTagOrThrow(String tag) {
        return byTag(tag).orElseThrow(() -> new IllegalArgumentException("Unknown aspect: " + tag));
    }

    public String getTag() {
        return this.tag;
    }

    public int getColor() {
        return this.color;
    }
}
