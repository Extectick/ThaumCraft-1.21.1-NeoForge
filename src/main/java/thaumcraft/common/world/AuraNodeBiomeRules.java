package thaumcraft.common.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import thaumcraft.api.aspects.Aspect;

/**
 * Modern tag equivalents of the BiomeDictionary registrations from the old
 * Config.registerBiomes method.
 */
public final class AuraNodeBiomeRules {
    private static final Profile OCEAN = profile(entry(120, Aspect.WATER));
    private static final Profile PLAINS = profile(entry(80, Aspect.AIR));
    private static final Profile DESERT = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.ENTROPY), entry(80, Aspect.EARTH));
    private static final Profile MOUNTAIN_HILLS = profile(entry(100, Aspect.AIR), entry(120, Aspect.AIR));
    private static final Profile FOREST = profile(entry(120, Aspect.EARTH));
    private static final Profile TAIGA = profile(
            entry(80, Aspect.ORDER), entry(100, Aspect.EARTH), entry(120, Aspect.EARTH));
    private static final Profile TAIGA_HILLS = profile(
            entry(80, Aspect.ORDER), entry(100, Aspect.EARTH), entry(120, Aspect.EARTH),
            entry(120, Aspect.AIR));
    private static final Profile SWAMP = profile(entry(80, Aspect.WATER), entry(120, Aspect.ENTROPY));
    private static final Profile RIVER = profile(entry(100, Aspect.WATER));
    private static final Profile FROZEN_OCEAN = profile(
            entry(80, Aspect.ORDER), entry(120, Aspect.WATER), entry(80, Aspect.ORDER));
    private static final Profile FROZEN_RIVER = profile(
            entry(80, Aspect.ORDER), entry(100, Aspect.WATER), entry(80, Aspect.ORDER));
    private static final Profile SNOWY_PLAINS = profile(
            entry(80, Aspect.ORDER), entry(80, Aspect.ORDER), entry(80, Aspect.ENTROPY));
    private static final Profile SNOWY_MOUNTAINS = profile(
            entry(80, Aspect.ORDER), entry(80, Aspect.ORDER), entry(100, Aspect.AIR));
    private static final Profile BEACH = profile(entry(80, Aspect.EARTH));
    private static final Profile SNOWY_BEACH = profile(
            entry(80, Aspect.ORDER), entry(80, Aspect.EARTH), entry(80, Aspect.ORDER));
    private static final Profile JUNGLE = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.WATER), entry(100, Aspect.ORDER),
            entry(100, Aspect.PLANT));
    private static final Profile JUNGLE_HILLS = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.WATER), entry(100, Aspect.ORDER),
            entry(100, Aspect.PLANT), entry(120, Aspect.AIR));
    private static final Profile JUNGLE_EDGE = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.WATER), entry(100, Aspect.PLANT),
            entry(120, Aspect.EARTH));
    private static final Profile FOREST_HILLS = profile(entry(120, Aspect.EARTH), entry(120, Aspect.AIR));
    private static final Profile END = profile(
            entry(80, Aspect.ORDER), entry(80, Aspect.ENTROPY), entry(80, Aspect.VOID));
    private static final Profile NETHER = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.ENTROPY), entry(120, Aspect.FIRE));
    private static final Profile MUSHROOM = profile(entry(140, Aspect.ORDER));
    private static final Profile MUSHROOM_SHORE = profile(entry(140, Aspect.ORDER), entry(80, Aspect.EARTH));
    private static final Profile MOUNTAIN = profile(entry(100, Aspect.AIR));
    private static final Profile DARK_FOREST = profile(
            entry(80, Aspect.SOUL), entry(100, Aspect.ORDER), entry(120, Aspect.EARTH));
    private static final Profile SNOWY_TAIGA = profile(
            entry(80, Aspect.ORDER), entry(100, Aspect.EARTH), entry(120, Aspect.EARTH),
            entry(80, Aspect.ORDER));
    private static final Profile SNOWY_TAIGA_HILLS = profile(
            entry(80, Aspect.ORDER), entry(100, Aspect.EARTH), entry(120, Aspect.EARTH),
            entry(80, Aspect.ORDER), entry(120, Aspect.AIR));
    private static final Profile WINDSWEPT_FOREST = profile(
            entry(100, Aspect.AIR), entry(120, Aspect.EARTH), entry(80, Aspect.ENTROPY));
    private static final Profile SAVANNA = profile(
            entry(100, Aspect.FIRE), entry(80, Aspect.AIR), entry(80, Aspect.AIR),
            entry(80, Aspect.ENTROPY));
    private static final Profile BADLANDS = profile(entry(80, Aspect.FIRE), entry(80, Aspect.EARTH));
    private static final Profile WOODED_BADLANDS = profile(
            entry(80, Aspect.FIRE), entry(80, Aspect.ENTROPY), entry(80, Aspect.EARTH));

    private static final List<Rule> RULES = List.of(
            rule(Tags.Biomes.IS_AQUATIC, 100, Aspect.WATER),
            rule(Tags.Biomes.IS_OCEAN, 120, Aspect.WATER),
            rule(Tags.Biomes.IS_RIVER, 100, Aspect.WATER),
            rule(Tags.Biomes.IS_WET, 80, Aspect.WATER),
            rule(Tags.Biomes.IS_HOT, 100, Aspect.FIRE),
            rule(Tags.Biomes.IS_DESERT, 100, Aspect.FIRE),
            rule(Tags.Biomes.IS_NETHER, 120, Aspect.FIRE),
            rule(Tags.Biomes.IS_BADLANDS, 80, Aspect.FIRE),
            rule(Tags.Biomes.IS_DENSE_VEGETATION, 100, Aspect.ORDER),
            rule(Tags.Biomes.IS_SNOWY, 80, Aspect.ORDER),
            rule(Tags.Biomes.IS_COLD, 80, Aspect.ORDER),
            rule(Tags.Biomes.IS_ICY, 100, Aspect.ORDER),
            rule(Tags.Biomes.IS_MUSHROOM, 140, Aspect.ORDER),
            rule(Tags.Biomes.IS_CONIFEROUS_TREE, 100, Aspect.EARTH),
            rule(Tags.Biomes.IS_FOREST, 120, Aspect.EARTH),
            rule(Tags.Biomes.IS_SANDY, 80, Aspect.EARTH),
            rule(Tags.Biomes.IS_BEACH, 80, Aspect.EARTH),
            rule(Tags.Biomes.IS_SAVANNA, 80, Aspect.AIR),
            rule(Tags.Biomes.IS_MOUNTAIN, 100, Aspect.AIR),
            rule(Tags.Biomes.IS_HILL, 120, Aspect.AIR),
            rule(Tags.Biomes.IS_PLAINS, 80, Aspect.AIR),
            rule(Tags.Biomes.IS_DRY, 80, Aspect.ENTROPY),
            rule(Tags.Biomes.IS_SPARSE_VEGETATION, 80, Aspect.ENTROPY),
            rule(Tags.Biomes.IS_SWAMP, 120, Aspect.ENTROPY),
            rule(Tags.Biomes.IS_WASTELAND, 80, Aspect.ENTROPY),
            rule(Tags.Biomes.IS_JUNGLE, 100, Aspect.PLANT),
            rule(Tags.Biomes.IS_LUSH, 100, Aspect.PLANT),
            rule(Tags.Biomes.IS_MAGICAL, 100, null),
            rule(Tags.Biomes.IS_END, 80, Aspect.VOID),
            rule(Tags.Biomes.IS_SPOOKY, 80, Aspect.SOUL),
            rule(Tags.Biomes.IS_DEAD, 50, Aspect.DEATH));

    private AuraNodeBiomeRules() {
    }

    public static int aura(Holder<Biome> biome) {
        Profile vanilla = vanillaProfile(biome);
        if (vanilla != null) {
            return vanilla.aura();
        }
        List<Rule> matches = matches(biome);
        if (matches.isEmpty()) {
            return 100;
        }
        int total = 0;
        for (Rule rule : matches) {
            total += rule.aura();
        }
        return total / matches.size();
    }

    public static Aspect randomTag(Holder<Biome> biome, RandomSource random) {
        Profile vanilla = vanillaProfile(biome);
        if (vanilla != null) {
            return vanilla.randomAspect(random);
        }
        List<Rule> matches = matches(biome);
        return matches.isEmpty() ? null : matches.get(random.nextInt(matches.size())).aspect();
    }

    private static Profile vanillaProfile(Holder<Biome> biome) {
        var key = biome.unwrapKey();
        if (key.isEmpty() || !key.get().location().getNamespace().equals("minecraft")) {
            return null;
        }
        return switch (key.get().location().getPath()) {
            case "ocean", "deep_ocean", "lukewarm_ocean", "deep_lukewarm_ocean",
                    "warm_ocean", "cold_ocean", "deep_cold_ocean" -> OCEAN;
            case "plains", "sunflower_plains", "meadow" -> PLAINS;
            case "desert" -> DESERT;
            case "windswept_hills", "windswept_gravelly_hills" -> MOUNTAIN_HILLS;
            case "forest", "flower_forest", "birch_forest", "old_growth_birch_forest" -> FOREST;
            case "taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga" -> TAIGA;
            case "snowy_taiga" -> SNOWY_TAIGA;
            case "swamp", "mangrove_swamp" -> SWAMP;
            case "river" -> RIVER;
            case "frozen_ocean", "deep_frozen_ocean" -> FROZEN_OCEAN;
            case "frozen_river" -> FROZEN_RIVER;
            case "snowy_plains", "ice_spikes" -> SNOWY_PLAINS;
            case "snowy_slopes", "frozen_peaks", "jagged_peaks" -> SNOWY_MOUNTAINS;
            case "beach" -> BEACH;
            case "snowy_beach" -> SNOWY_BEACH;
            case "jungle", "bamboo_jungle" -> JUNGLE;
            case "sparse_jungle" -> JUNGLE_EDGE;
            case "windswept_forest" -> WINDSWEPT_FOREST;
            case "the_end", "end_highlands", "end_midlands", "small_end_islands",
                    "end_barrens" -> END;
            case "nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest",
                    "basalt_deltas" -> NETHER;
            case "mushroom_fields" -> MUSHROOM;
            case "stony_shore" -> MOUNTAIN;
            case "dark_forest" -> DARK_FOREST;
            case "savanna", "savanna_plateau", "windswept_savanna" -> SAVANNA;
            case "badlands", "eroded_badlands" -> BADLANDS;
            case "wooded_badlands" -> WOODED_BADLANDS;
            case "grove" -> SNOWY_TAIGA_HILLS;
            case "cherry_grove" -> FOREST_HILLS;
            case "dripstone_caves" -> MOUNTAIN;
            case "lush_caves" -> JUNGLE;
            case "deep_dark" -> DARK_FOREST;
            default -> null;
        };
    }

    private static List<Rule> matches(Holder<Biome> biome) {
        List<Rule> matches = new ArrayList<>();
        for (Rule rule : RULES) {
            if (biome.is(rule.tag())) {
                matches.add(rule);
            }
        }
        return matches;
    }

    private static Rule rule(TagKey<Biome> tag, int aura, Aspect aspect) {
        return new Rule(tag, aura, aspect);
    }

    private static Entry entry(int aura, Aspect aspect) {
        return new Entry(aura, aspect);
    }

    private static Profile profile(Entry... entries) {
        return new Profile(List.of(entries));
    }

    private record Rule(TagKey<Biome> tag, int aura, Aspect aspect) {
    }

    private record Entry(int aura, Aspect aspect) {
    }

    private record Profile(List<Entry> entries) {
        int aura() {
            int total = 0;
            for (Entry entry : this.entries) {
                total += entry.aura();
            }
            return total / this.entries.size();
        }

        Aspect randomAspect(RandomSource random) {
            return this.entries.get(random.nextInt(this.entries.size())).aspect();
        }
    }
}
