package thaumcraft.common.research;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.ResearchEntry.ResearchFlag;

public final class ResearchRegistry {
    public static final String BASICS = "BASICS";
    public static final String THAUMATURGY = "THAUMATURGY";
    public static final String ALCHEMY = "ALCHEMY";
    public static final String ARTIFICE = "ARTIFICE";
    public static final String GOLEMANCY = "GOLEMANCY";
    public static final String ELDRITCH = "ELDRITCH";
    public static final String FIRST_STEPS = "ASPECTS";

    private static final Map<String, ResearchCategory> CATEGORIES = new LinkedHashMap<>();
    private static final Map<String, ResearchEntry> ENTRIES = new LinkedHashMap<>();

    static {
        registerCategory(new ResearchCategory(BASICS, Thaumcraft.id("textures/item/thaumonomicon.png"),
                Thaumcraft.id("textures/gui/gui_researchback.png"), -8, 8, -8, 8, 0));
        registerCategory(new ResearchCategory(THAUMATURGY, Thaumcraft.id("textures/misc/r_thaumaturgy.png"),
                Thaumcraft.id("textures/gui/gui_researchback.png"), -8, 8, -8, 8, 1));
        registerCategory(new ResearchCategory(ALCHEMY, Thaumcraft.id("textures/misc/r_crucible.png"),
                Thaumcraft.id("textures/gui/gui_researchback.png"), -8, 8, -8, 8, 2));
        registerCategory(new ResearchCategory(ARTIFICE, Thaumcraft.id("textures/misc/r_artifice.png"),
                Thaumcraft.id("textures/gui/gui_researchback.png"), -8, 8, -8, 8, 3));
        registerCategory(new ResearchCategory(GOLEMANCY, Thaumcraft.id("textures/misc/r_golemancy.png"),
                Thaumcraft.id("textures/gui/gui_researchback.png"), -8, 8, -8, 8, 4));
        registerCategory(new ResearchCategory(ELDRITCH, Thaumcraft.id("textures/misc/r_eldritch.png"),
                Thaumcraft.id("textures/gui/gui_researchbackeldritch.png"), -8, 8, -8, 8, 5));

        register(ResearchEntry.builder("ASPECTS", BASICS)
                .tags(List.of(Aspect.MAGIC))
                .position(0, 0)
                .complexity(1)
                .icon(miscIcon("r_aspects"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("ASPECTS", 3))
                .build());
        register(ResearchEntry.builder("PECH", BASICS)
                .tags(List.of(Aspect.MAN))
                .position(-4, -4)
                .complexity(1)
                .icon(miscIcon("r_pech"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("PECH", 2))
                .build());
        register(ResearchEntry.builder("NODES", BASICS)
                .tags(List.of(Aspect.AURA))
                .position(-2, 0)
                .complexity(1)
                .icon(miscIcon("r_nodes"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("NODES", 3))
                .build());
        register(ResearchEntry.builder("WARP", BASICS)
                .tags(List.of(Aspect.TAINT))
                .position(0, 2)
                .complexity(1)
                .icon(miscIcon("r_warp"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("WARP", 3))
                .build());
        register(ResearchEntry.builder("RESEARCH", BASICS)
                .tags(List.of(Aspect.MIND))
                .position(2, 0)
                .complexity(1)
                .icon(Thaumcraft.id("textures/item/scribing_tools.png"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("RESEARCH", 12))
                .build());
        register(ResearchEntry.builder("KNOWFRAG", BASICS)
                .tags(List.of(Aspect.MIND))
                .position(3, -2)
                .complexity(1)
                .icon(Thaumcraft.id("textures/item/knowledge_fragment.png"))
                .parents("RESEARCH")
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(page("KNOWFRAG", 1))
                .build());
        register(ResearchEntry.builder("THAUMONOMICON", BASICS)
                .tags(List.of(Aspect.MAGIC))
                .position(1, -2)
                .complexity(1)
                .icon(Thaumcraft.id("textures/item/thaumonomicon.png"))
                .parents("RESEARCH")
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(page("THAUMONOMICON", 1))
                .build());
        register(ResearchEntry.builder("ORE", BASICS)
                .tags(List.of(Aspect.MINE))
                .position(-2, -2)
                .complexity(1)
                .cyclingIcon(30,
                        new ItemStack(TCItems.INFUSED_AIR_ORE.get()),
                        new ItemStack(TCItems.INFUSED_FIRE_ORE.get()),
                        new ItemStack(TCItems.INFUSED_WATER_ORE.get()),
                        new ItemStack(TCItems.INFUSED_EARTH_ORE.get()),
                        new ItemStack(TCItems.INFUSED_ORDER_ORE.get()),
                        new ItemStack(TCItems.INFUSED_ENTROPY_ORE.get()),
                        new ItemStack(TCItems.CINNABAR_ORE.get()),
                        new ItemStack(TCItems.AMBER_ORE.get()))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("ORE", 4))
                .build());
        register(ResearchEntry.builder("PLANTS", BASICS)
                .tags(List.of(Aspect.PLANT))
                .position(-2, -4)
                .complexity(1)
                .icon(Thaumcraft.id("textures/block/greatwood_sapling.png"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("PLANTS", 6))
                .build());
        register(ResearchEntry.builder("ENCHANT", BASICS)
                .tags(List.of(Aspect.MAGIC))
                .position(-4, -2)
                .complexity(1)
                .icon(miscIcon("r_enchant"))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(pages("ENCHANT", 2))
                .build());
        register(ResearchEntry.builder("NODETAPPER1", BASICS)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.MOTION, Aspect.EXCHANGE))
                .position(-4, 1)
                .complexity(2)
                .icon(miscIcon("r_nodetap1"))
                .parents("NODES")
                .flag(ResearchFlag.ROUND)
                .pages(page("NODETAPPER1", 1))
                .build());
        register(ResearchEntry.builder("NODEPRESERVE", BASICS)
                .tags(List.of(Aspect.AURA, Aspect.GREED, Aspect.SENSES))
                .position(-6, 2)
                .complexity(2)
                .icon(miscIcon("r_nodepreserve"))
                .parents("NODETAPPER1")
                .flag(ResearchFlag.ROUND)
                .pages(ResearchPage.text("tc.research_page.NODEPRESERVE"))
                .build());
        register(ResearchEntry.builder("NODEJAR", BASICS)
                .tags(List.of(Aspect.AURA, Aspect.GREED, Aspect.EXCHANGE, Aspect.MOTION))
                .position(-7, 4)
                .complexity(3)
                .icon(Thaumcraft.id("textures/block/jar.png"))
                .parents("NODEPRESERVE")
                .flag(ResearchFlag.CONCEALED)
                .pages(pages("NODEJAR", 2))
                .build());
        register(ResearchEntry.builder("NODETAPPER2", BASICS)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.MOTION, Aspect.EXCHANGE))
                .position(-3, 3)
                .complexity(2)
                .icon(miscIcon("r_nodetap2"))
                .parents("NODETAPPER1")
                .flags(ResearchFlag.SPECIAL, ResearchFlag.ROUND)
                .pages(page("NODETAPPER2", 1))
                .build());
        register(ResearchEntry.builder("RESEARCHER1", BASICS)
                .tags(List.of(Aspect.MIND, Aspect.SENSES, Aspect.ORDER))
                .position(4, 1)
                .complexity(1)
                .icon(miscIcon("r_researcher1"))
                .parents("RESEARCH")
                .flag(ResearchFlag.ROUND)
                .pages(page("RESEARCHER1", 1))
                .build());
        register(ResearchEntry.builder("DECONSTRUCTOR", BASICS)
                .tags(List.of(Aspect.MIND, Aspect.CRAFT, Aspect.ENTROPY))
                .position(6, 2)
                .complexity(1)
                .icon(new ItemStack(TCItems.DECONSTRUCTION_TABLE.get()))
                .parents("RESEARCHER1")
                .flag(ResearchFlag.ROUND)
                .pages(pages("DECONSTRUCTOR", 2))
                .build());
        register(ResearchEntry.builder("RESEARCHER2", BASICS)
                .tags(List.of(Aspect.MIND, Aspect.ORDER, Aspect.SENSES, Aspect.MAGIC))
                .position(3, 3)
                .complexity(2)
                .icon(miscIcon("r_researcher2"))
                .parents("RESEARCHER1")
                .flags(ResearchFlag.SPECIAL, ResearchFlag.ROUND)
                .warp(1)
                .pages(page("RESEARCHER2", 1))
                .build());
        register(ResearchEntry.builder("RESEARCHDUPE", BASICS)
                .tags(List.of(Aspect.MIND, Aspect.EXCHANGE, Aspect.SENSES, Aspect.GREED, Aspect.CRAFT))
                .position(4, 5)
                .complexity(3)
                .icon(miscIcon("r_resdupe"))
                .parents("RESEARCHER2")
                .flag(ResearchFlag.ROUND)
                .pages(page("RESEARCHDUPE", 1))
                .build());
        register(ResearchEntry.builder("CRIMSON", BASICS)
                .tags(List.of(Aspect.ELDRITCH))
                .position(0, 4)
                .complexity(1)
                .icon(miscIcon("r_eldritch"))
                .flags(ResearchFlag.HIDDEN, ResearchFlag.STUB, ResearchFlag.ROUND, ResearchFlag.SPECIAL)
                .warp(3)
                .pages(page("CRIMSON", 1))
                .build());
        register(ResearchEntry.builder("BASIC_ALCHEMY", ALCHEMY)
                .tags(List.of(Aspect.WATER, Aspect.EARTH, Aspect.EXCHANGE))
                .position(0, 0)
                .complexity(2)
                .parents(FIRST_STEPS)
                .pages(ResearchPage.text("tc.research_page.BASIC_ALCHEMY.1"))
                .build());
    }

    private ResearchRegistry() {
    }

    public static void registerCategory(ResearchCategory category) {
        CATEGORIES.put(category.key(), category);
    }

    public static Collection<ResearchCategory> categories() {
        return CATEGORIES.values().stream()
                .sorted(Comparator.comparingInt(ResearchCategory::sortOrder).thenComparing(ResearchCategory::key))
                .toList();
    }

    public static Optional<ResearchCategory> category(String key) {
        return Optional.ofNullable(CATEGORIES.get(key));
    }

    public static void register(ResearchEntry entry) {
        ENTRIES.put(entry.key(), entry);
    }

    public static Optional<ResearchEntry> get(String key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static Collection<ResearchEntry> entries() {
        return List.copyOf(ENTRIES.values());
    }

    public static List<ResearchEntry> entriesInCategory(String category) {
        return ENTRIES.values().stream()
                .filter(entry -> entry.category().equals(category))
                .sorted(Comparator.comparingInt(ResearchEntry::displayRow)
                        .thenComparingInt(ResearchEntry::displayColumn)
                        .thenComparing(ResearchEntry::key))
                .toList();
    }

    public static List<ResearchEntry> visibleEntriesInCategory(String category, ResearchKnowledgeData knowledge) {
        return entriesInCategory(category).stream()
                .filter(entry -> entry.isVisibleInBook(knowledge))
                .toList();
    }

    public static List<ResearchEntry> triggerEntries() {
        return ENTRIES.values().stream()
                .filter(ResearchEntry::hasTriggers)
                .toList();
    }

    public static ResearchNoteData createNoteData(String key, Random random) {
        return get(key).map(entry -> ResearchNoteData.create(entry, random))
                .orElseGet(() -> ResearchNoteData.create(get(FIRST_STEPS).orElseThrow(), random));
    }

    public static Random deterministicRandom(String key) {
        return new Random(0x544341554D435246L ^ key.hashCode());
    }

    public static Set<String> keys() {
        return Set.copyOf(ENTRIES.keySet());
    }

    private static ResearchPage page(String key, int page) {
        return ResearchPage.text("tc.research_page." + key + "." + page);
    }

    private static List<ResearchPage> pages(String key, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(index -> page(key, index))
                .toList();
    }

    private static ResourceLocation miscIcon(String name) {
        return Thaumcraft.id("textures/misc/" + name + ".png");
    }
}
