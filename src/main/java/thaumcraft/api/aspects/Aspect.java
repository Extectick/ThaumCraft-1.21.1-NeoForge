package thaumcraft.api.aspects;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum Aspect {
    AIR("aer", 0xffff7e),
    EARTH("terra", 0x56c000),
    FIRE("ignis", 0xff5a01),
    WATER("aqua", 0x3cd4fc),
    ORDER("ordo", 0xd5d4ec),
    ENTROPY("perditio", 0x404040),
    VOID("vacuos", 0x888888, AIR, ENTROPY),
    LIGHT("lux", 0xfff7a3, AIR, FIRE),
    WEATHER("tempestas", 0xffffff, AIR, WATER),
    MOTION("motus", 0xcdccf4, AIR, ORDER),
    COLD("gelum", 0xe1ffff, FIRE, ENTROPY),
    CRYSTAL("vitreus", 0x80ffff, EARTH, ORDER),
    LIFE("victus", 0xde0005, WATER, EARTH),
    POISON("venenum", 0x89f000, WATER, ENTROPY),
    ENERGY("potentia", 0xc0ffff, ORDER, FIRE),
    EXCHANGE("permutatio", 0x578357, ENTROPY, ORDER),
    METAL("metallum", 0xb5b5cd, EARTH, CRYSTAL),
    DEATH("mortuus", 0x887788, LIFE, ENTROPY),
    FLIGHT("volatus", 0xe7e7d7, AIR, MOTION),
    DARKNESS("tenebrae", 0x222222, VOID, LIGHT),
    SOUL("spiritus", 0xebebfb, LIFE, DEATH),
    HEAL("sano", 0xff2b4c, LIFE, ORDER),
    TRAVEL("iter", 0xe05a9b, MOTION, EARTH),
    ELDRITCH("alienis", 0x805080, VOID, DARKNESS),
    MAGIC("praecantatio", 0x9700c0, VOID, ENERGY),
    AURA("auram", 0xffaa7f, MAGIC, AIR),
    TAINT("vitium", 0x800080, MAGIC, ENTROPY),
    SLIME("limus", 0x01f000, LIFE, WATER),
    PLANT("herba", 0x01ac00, LIFE, EARTH),
    TREE("arbor", 0x875a21, AIR, PLANT),
    BEAST("bestia", 0x9f6409, MOTION, LIFE),
    FLESH("corpus", 0xee6a6a, DEATH, BEAST),
    UNDEAD("exanimis", 0x3a4000, MOTION, DEATH),
    MIND("cognitio", 0xffc1f3, FIRE, SOUL),
    SENSES("sensus", 0x0fd9ff, AIR, SOUL),
    MAN("humanus", 0xffd7a3, BEAST, MIND),
    CROP("messis", 0xe1f55b, PLANT, MAN),
    MINE("perfodio", 0xdcad70, MAN, EARTH),
    TOOL("instrumentum", 0x4040ee, MAN, ORDER),
    HARVEST("meto", 0xeecc82, CROP, TOOL),
    WEAPON("telum", 0xc05350, TOOL, FIRE),
    ARMOR("tutamen", 0x00c0c0, TOOL, EARTH),
    HUNGER("fames", 0x99cc05, LIFE, VOID),
    GREED("lucrum", 0xe6be44, MAN, HUNGER),
    CRAFT("fabrico", 0x809d80, MAN, TOOL),
    CLOTH("pannus", 0xeaeac2, TOOL, BEAST),
    MECHANISM("machina", 0x8080a0, MOTION, TOOL),
    TRAP("vinculum", 0x9a8080, MOTION, ENTROPY);

    private static final List<Aspect> PRIMAL_ASPECTS = List.of(AIR, EARTH, FIRE, WATER, ORDER, ENTROPY);
    private static final List<Aspect> COMPOUND_ASPECTS = List.of(VOID, LIGHT, WEATHER, MOTION, COLD, CRYSTAL, LIFE,
            POISON, ENERGY, EXCHANGE, METAL, DEATH, FLIGHT, DARKNESS, SOUL, HEAL, TRAVEL, ELDRITCH, MAGIC, AURA,
            TAINT, SLIME, PLANT, TREE, BEAST, FLESH, UNDEAD, MIND, SENSES, MAN, CROP, MINE, TOOL, HARVEST, WEAPON,
            ARMOR, HUNGER, GREED, CRAFT, CLOTH, MECHANISM, TRAP);
    public static final Codec<Aspect> CODEC = Codec.STRING.xmap(Aspect::byTagOrThrow, Aspect::getTag);
    public static final StreamCodec<io.netty.buffer.ByteBuf, Aspect> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(Aspect::byTagOrThrow, Aspect::getTag);

    private final String tag;
    private final int color;
    private final Aspect[] components;

    Aspect(String tag, int color) {
        this(tag, color, (Aspect[]) null);
    }

    Aspect(String tag, int color, Aspect... components) {
        this.tag = tag;
        this.color = color;
        this.components = components;
    }

    public static List<Aspect> getPrimalAspects() {
        return PRIMAL_ASPECTS;
    }

    public static List<Aspect> getCompoundAspects() {
        return COMPOUND_ASPECTS;
    }

    public static Optional<Aspect> byTag(String tag) {
        String normalized = tag.toLowerCase(Locale.ROOT);
        normalized = switch (normalized) {
            case "air" -> "aer";
            case "earth" -> "terra";
            case "fire" -> "ignis";
            case "water" -> "aqua";
            case "order" -> "ordo";
            case "entropy" -> "perditio";
            default -> normalized;
        };
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

    public Aspect[] getComponents() {
        return this.components;
    }

    public boolean isPrimal() {
        return this.components == null || this.components.length != 2;
    }
}
