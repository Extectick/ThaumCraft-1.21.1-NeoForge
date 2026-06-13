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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCBlocks;
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
                .pages(page("KNOWFRAG", 1), ResearchPage.normalCrafting(Thaumcraft.id("unknown_research_notes")))
                .build());
        register(ResearchEntry.builder("THAUMONOMICON", BASICS)
                .tags(List.of(Aspect.MAGIC))
                .position(1, -2)
                .complexity(1)
                .icon(Thaumcraft.id("textures/item/thaumonomicon.png"))
                .parents("RESEARCH")
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(page("THAUMONOMICON", 1), ResearchPage.normalCrafting(Thaumcraft.id("wand_casting")))
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
                .pages(page("NODEJAR", 1), nodeJarConstruct(), page("NODEJAR", 2))
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
        register(ResearchEntry.builder("THAUMOMETER", ARTIFICE)
                .tags(List.of(Aspect.SENSES))
                .position(2, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.THAUMOMETER.get()))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(page("THAUMOMETER", 1), ResearchPage.normalCrafting(Thaumcraft.id("thaumometer")))
                .build());
        register(ResearchEntry.builder("GOGGLES", ARTIFICE)
                .tags(List.of(Aspect.SENSES, Aspect.AURA, Aspect.MAGIC))
                .position(4, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.GOGGLES.get()))
                .parents("THAUMOMETER")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("GOGGLES", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("goggles")))
                .build());
        register(ResearchEntry.builder("SINSTONE", ARTIFICE)
                .tags(List.of(Aspect.SENSES, Aspect.DARKNESS, Aspect.ELDRITCH, Aspect.AURA))
                .position(6, 2)
                .complexity(1)
                .icon(new ItemStack(TCItems.SINISTER_LODESTONE.get()))
                .parents("GOGGLES")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("SINSTONE", 1), ResearchPage.infusionCrafting(Thaumcraft.id("sinister_lodestone")))
                .warp(2)
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
        register(ResearchEntry.builder("BASICTHAUMATURGY", THAUMATURGY)
                .position(0, 0)
                .complexity(0)
                .icon(new ItemStack(TCItems.WAND_CASTING.get()))
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB, ResearchFlag.ROUND)
                .pages(page("BASICTHAUMATURGY", 1), page("BASICTHAUMATURGY", 2),
                        ResearchPage.normalCrafting(Thaumcraft.id("iron_wand_cap")),
                        ResearchPage.normalCrafting(Thaumcraft.id("wand_casting")))
                .build());
        register(ResearchEntry.builder("FOCUSFIRE", THAUMATURGY)
                .tags(List.of(Aspect.FIRE, Aspect.MAGIC))
                .position(2, -2)
                .complexity(1)
                .icon(new ItemStack(TCItems.FOCUS_FIRE.get()))
                .parents("BASICTHAUMATURGY")
                .pages(page("FOCUSFIRE", 1), page("FOCUSFIRE", 2),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("focus_fire")))
                .build());
        register(ResearchEntry.builder("FOCUSFROST", THAUMATURGY)
                .tags(List.of(Aspect.WATER, Aspect.MAGIC, Aspect.COLD))
                .position(1, -5)
                .complexity(1)
                .icon(new ItemStack(TCItems.FOCUS_FROST.get()))
                .parents("FOCUSFIRE")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("FOCUSFROST", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("focus_frost")))
                .build());
        register(ResearchEntry.builder("FOCUSSHOCK", THAUMATURGY)
                .tags(List.of(Aspect.AIR, Aspect.ENERGY, Aspect.MAGIC))
                .position(3, -5)
                .complexity(1)
                .icon(new ItemStack(TCItems.FOCUS_SHOCK.get()))
                .parents("FOCUSFIRE")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("FOCUSSHOCK", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("focus_shock")))
                .build());
        register(ResearchEntry.builder("FOCUSEXCAVATION", THAUMATURGY)
                .tags(List.of(Aspect.EARTH, Aspect.ENTROPY, Aspect.MAGIC))
                .position(0, -3)
                .complexity(2)
                .icon(new ItemStack(TCItems.FOCUS_EXCAVATION.get()))
                .parents("FOCUSFIRE")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("FOCUSEXCAVATION", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("focus_excavation")))
                .build());
        register(ResearchEntry.builder("FOCUSTRADE", THAUMATURGY)
                .tags(List.of(Aspect.EARTH, Aspect.EXCHANGE, Aspect.MAGIC))
                .position(4, -3)
                .complexity(2)
                .icon(new ItemStack(TCItems.FOCUS_TRADE.get()))
                .parents("FOCUSFIRE")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("FOCUSTRADE", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("focus_trade")))
                .build());
        register(ResearchEntry.builder("FOCUSHELLBAT", THAUMATURGY)
                .tags(List.of(Aspect.TRAVEL, Aspect.BEAST, Aspect.FIRE, Aspect.MAGIC))
                .position(3, -7)
                .complexity(2)
                .icon(new ItemStack(TCItems.FOCUS_HELLBAT.get()))
                .hiddenParents("FOCUSFIRE", "INFUSION")
                .flags(ResearchFlag.HIDDEN)
                .aspectTriggers(Aspect.FIRE)
                .pages(page("FOCUSHELLBAT", 1), ResearchPage.infusionCrafting(Thaumcraft.id("focus_hellbat")))
                .warp(2)
                .build());
        register(ResearchEntry.builder("FOCUSWARDING", THAUMATURGY)
                .tags(List.of(Aspect.EARTH, Aspect.ARMOR, Aspect.ORDER, Aspect.MIND))
                .position(-2, -4)
                .complexity(3)
                .icon(new ItemStack(TCItems.FOCUS_WARDING.get()))
                .parents("FOCUSEXCAVATION", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("FOCUSWARDING", 1), ResearchPage.infusionCrafting(Thaumcraft.id("focus_warding")))
                .build());
        register(ResearchEntry.builder("FOCUSPORTABLEHOLE", THAUMATURGY)
                .tags(List.of(Aspect.TRAVEL, Aspect.ENTROPY, Aspect.ELDRITCH, Aspect.AIR))
                .position(7, -2)
                .complexity(2)
                .icon(new ItemStack(TCItems.FOCUS_PORTABLE_HOLE.get()))
                .parents("FOCUSTRADE", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("FOCUSPORTABLEHOLE", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("focus_portable_hole")))
                .build());
        register(ResearchEntry.builder("BASIC_ALCHEMY", ALCHEMY)
                .tags(List.of(Aspect.WATER, Aspect.EARTH, Aspect.EXCHANGE))
                .position(0, 0)
                .complexity(2)
                .parents(FIRST_STEPS)
                .pages(ResearchPage.text("tc.research_page.BASIC_ALCHEMY.1"))
                .build());
        register(ResearchEntry.builder("DISTILESSENTIA", ALCHEMY)
                .tags(List.of(Aspect.EXCHANGE, Aspect.WATER, Aspect.MAGIC))
                .position(2, 2)
                .complexity(2)
                .icon(new ItemStack(TCItems.ARCANE_ALEMBIC.get()))
                .parents("BASIC_ALCHEMY")
                .pages(page("DISTILESSENTIA", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("alchemical_furnace")),
                        page("DISTILESSENTIA", 2), ResearchPage.arcaneCrafting(Thaumcraft.id("arcane_alembic")))
                .build());
        register(ResearchEntry.builder("ALUMENTUM", ALCHEMY)
                .tags(List.of(Aspect.FIRE, Aspect.ENERGY, Aspect.MAGIC))
                .position(-2, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.ALUMENTUM.get()))
                .parents("BASIC_ALCHEMY")
                .pages(page("ALUMENTUM", 1), ResearchPage.crucibleCrafting(Thaumcraft.id("crucible_alumentum")))
                .build());
        register(ResearchEntry.builder("NITOR", ALCHEMY)
                .tags(List.of(Aspect.LIGHT, Aspect.FIRE, Aspect.MAGIC))
                .position(-1, 3)
                .complexity(1)
                .icon(new ItemStack(TCItems.NITOR.get()))
                .parents("ALUMENTUM")
                .pages(page("NITOR", 1), ResearchPage.crucibleCrafting(Thaumcraft.id("crucible_nitor")))
                .build());
        register(ResearchEntry.builder("TALLOW", ALCHEMY)
                .tags(List.of(Aspect.FLESH, Aspect.FIRE, Aspect.CRAFT))
                .position(1, 3)
                .complexity(1)
                .icon(new ItemStack(TCItems.MAGIC_TALLOW.get()))
                .parents("BASIC_ALCHEMY")
                .pages(page("TALLOW", 1), ResearchPage.crucibleCrafting(Thaumcraft.id("crucible_tallow")))
                .build());
        register(ResearchEntry.builder("THAUMIUM", ALCHEMY)
                .tags(List.of(Aspect.METAL, Aspect.MAGIC, Aspect.CRAFT))
                .position(3, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.THAUMIUM_INGOT.get()))
                .parents("BASIC_ALCHEMY")
                .pages(page("THAUMIUM", 1), ResearchPage.crucibleCrafting(Thaumcraft.id("crucible_thaumium")))
                .build());
        register(ResearchEntry.builder("TUBES", ALCHEMY)
                .tags(List.of(Aspect.WATER, Aspect.MOTION, Aspect.EXCHANGE, Aspect.MECHANISM))
                .position(4, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.ESSENTIA_TUBE.get()))
                .parents("DISTILESSENTIA")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("TUBES", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("essentia_tube")))
                .build());
        register(ResearchEntry.builder("CENTRIFUGE", ALCHEMY)
                .tags(List.of(Aspect.EXCHANGE, Aspect.MOTION, Aspect.MECHANISM))
                .position(6, 4)
                .complexity(2)
                .icon(new ItemStack(TCItems.ALCHEMICAL_CENTRIFUGE.get()))
                .parents("TUBES")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("CENTRIFUGE", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("essentia_buffer")))
                .build());
        register(ResearchEntry.builder("JARVOID", ALCHEMY)
                .tags(List.of(Aspect.VOID, Aspect.WATER, Aspect.EXCHANGE))
                .position(3, 4)
                .complexity(2)
                .icon(new ItemStack(TCItems.VOID_JAR.get()))
                .parents("DISTILESSENTIA")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("JARVOID", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("void_jar")))
                .build());
        register(ResearchEntry.builder("INFUSION", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.MECHANISM, Aspect.CRAFT))
                .position(-4, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.RUNIC_MATRIX.get()))
                .parents("DISTILESSENTIA")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("INFUSION", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("runic_matrix")),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("arcane_pedestal")), page("INFUSION", 2),
                        infusionAltarConstruct(), page("INFUSION", 3), page("INFUSION", 4), page("INFUSION", 5))
                .build());
        register(ResearchEntry.builder("ENCHFABRIC", ARTIFICE)
                .tags(List.of(Aspect.CLOTH, Aspect.MAGIC))
                .position(0, 3)
                .complexity(1)
                .icon(new ItemStack(TCItems.ENCHANTED_FABRIC.get()))
                .parents("INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ENCHFABRIC", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("enchanted_fabric")),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("robe_chestplate")),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("robe_leggings")),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("robe_boots")), page("ENCHFABRIC", 2))
                .build());
        register(ResearchEntry.builder("ARCANELAMP", ARTIFICE)
                .tags(List.of(Aspect.LIGHT, Aspect.SENSES, Aspect.DARKNESS))
                .position(-3, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.ARCANE_LAMP.get()))
                .parents("NITOR")
                .flag(ResearchFlag.SECONDARY)
                .pages(page("ARCANELAMP", 1), page("ARCANELAMP", 2))
                .build());
        register(ResearchEntry.builder("RUNICARMOR", ARTIFICE)
                .tags(List.of(Aspect.ARMOR, Aspect.AIR, Aspect.MAGIC, Aspect.ENERGY, Aspect.MIND))
                .position(3, 4)
                .complexity(3)
                .icon(new ItemStack(TCItems.RUNIC_RING.get()))
                .parents("ENCHFABRIC")
                .hiddenParents("INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("RUNICARMOR", 1), page("RUNICARMOR", 2),
                        ResearchPage.infusionCrafting(Thaumcraft.id("runic_ring")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("runic_amulet")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("runic_girdle")))
                .build());
        register(ResearchEntry.builder("RUNICCHARGED", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.ARMOR, Aspect.ENERGY))
                .position(2, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.RUNIC_RING_CHARGED.get()))
                .parents("RUNICARMOR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("RUNICCHARGED", 1), ResearchPage.infusionCrafting(Thaumcraft.id("runic_ring_charged")))
                .build());
        register(ResearchEntry.builder("RUNICHEALING", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.ARMOR, Aspect.HEAL, Aspect.WATER))
                .position(4, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.RUNIC_RING_REGEN.get()))
                .parents("RUNICARMOR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("RUNICHEALING", 1), ResearchPage.infusionCrafting(Thaumcraft.id("runic_ring_regen")))
                .build());
        register(ResearchEntry.builder("RUNICKINETIC", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.ARMOR, Aspect.AIR))
                .position(2, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.RUNIC_GIRDLE_KINETIC.get()))
                .parents("RUNICARMOR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("RUNICKINETIC", 1), ResearchPage.infusionCrafting(Thaumcraft.id("runic_girdle_kinetic")))
                .build());
        register(ResearchEntry.builder("RUNICEMERGENCY", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.ARMOR, Aspect.EARTH, Aspect.VOID))
                .position(4, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.RUNIC_AMULET_EMERGENCY.get()))
                .parents("RUNICARMOR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("RUNICEMERGENCY", 1), ResearchPage.infusionCrafting(Thaumcraft.id("runic_amulet_emergency")))
                .build());
        register(ResearchEntry.builder("BOOTSTRAVELLER", ARTIFICE)
                .tags(List.of(Aspect.TRAVEL, Aspect.EARTH, Aspect.FLIGHT, Aspect.WATER))
                .position(-1, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.BOOTS_TRAVELLER.get()))
                .parents("ENCHFABRIC", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("BOOTSTRAVELLER", 1), ResearchPage.infusionCrafting(Thaumcraft.id("boots_traveller")))
                .build());
        register(ResearchEntry.builder("HOVERGIRDLE", ARTIFICE)
                .tags(List.of(Aspect.FLIGHT, Aspect.TRAVEL, Aspect.AIR, Aspect.MOTION))
                .position(2, 7)
                .complexity(3)
                .icon(new ItemStack(TCItems.HOVER_GIRDLE.get()))
                .parents("BOOTSTRAVELLER")
                .flags(ResearchFlag.HIDDEN, ResearchFlag.SECONDARY)
                .aspectTriggers(Aspect.FLIGHT)
                .pages(page("HOVERGIRDLE", 1), ResearchPage.infusionCrafting(Thaumcraft.id("hover_girdle")))
                .build());
        register(ResearchEntry.builder("MIRROR", ARTIFICE)
                .tags(List.of(Aspect.TRAVEL, Aspect.ELDRITCH, Aspect.DARKNESS, Aspect.CRYSTAL))
                .position(-1, 8)
                .complexity(2)
                .icon(new ItemStack(TCItems.MAGIC_MIRROR.get()))
                .parents("INFUSION")
                .flag(ResearchFlag.HIDDEN)
                .itemTriggers(new ItemStack(Items.ENDER_PEARL), new ItemStack(Blocks.NETHER_PORTAL),
                        new ItemStack(Blocks.END_PORTAL_FRAME))
                .pages(page("MIRROR", 1), page("MIRROR", 2),
                        ResearchPage.infusionCrafting(Thaumcraft.id("magic_mirror")), page("MIRROR", 3))
                .build());
        register(ResearchEntry.builder("MIRRORESSENTIA", ARTIFICE)
                .tags(List.of(Aspect.TRAVEL, Aspect.ELDRITCH, Aspect.WATER, Aspect.MAGIC))
                .position(-1, 10)
                .complexity(2)
                .icon(new ItemStack(TCItems.ESSENTIA_MIRROR.get()))
                .parents("MIRROR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("MIRRORESSENTIA", 1), page("MIRRORESSENTIA", 2),
                        ResearchPage.infusionCrafting(Thaumcraft.id("essentia_mirror")))
                .build());
        register(ResearchEntry.builder("MIRRORHAND", ARTIFICE)
                .tags(List.of(Aspect.TOOL, Aspect.ELDRITCH, Aspect.CRYSTAL, Aspect.TRAVEL))
                .position(1, 9)
                .complexity(2)
                .icon(new ItemStack(TCItems.HAND_MIRROR.get()))
                .parents("MIRROR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("MIRRORHAND", 1), ResearchPage.infusionCrafting(Thaumcraft.id("hand_mirror")))
                .build());
        register(ResearchEntry.builder("LAMPGROWTH", ARTIFICE)
                .tags(List.of(Aspect.LIGHT, Aspect.PLANT, Aspect.LIFE, Aspect.CROP))
                .position(-4, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.LAMP_OF_GROWTH.get()))
                .parents("ARCANELAMP", "INFUSION")
                .flag(ResearchFlag.HIDDEN)
                .aspectTriggers(Aspect.LIGHT, Aspect.CROP)
                .pages(page("LAMPGROWTH", 1), ResearchPage.infusionCrafting(Thaumcraft.id("lamp_growth")))
                .build());
        register(ResearchEntry.builder("LAMPFERTILITY", ARTIFICE)
                .tags(List.of(Aspect.BEAST, Aspect.LIFE, Aspect.LIGHT))
                .position(-2, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.LAMP_OF_FERTILITY.get()))
                .parents("ARCANELAMP", "INFUSION")
                .flag(ResearchFlag.HIDDEN)
                .aspectTriggers(Aspect.LIGHT, Aspect.LIFE)
                .pages(page("LAMPFERTILITY", 1), ResearchPage.infusionCrafting(Thaumcraft.id("lamp_fertility")))
                .build());
        register(ResearchEntry.builder("ELEMENTALAXE", ARTIFICE)
                .tags(List.of(Aspect.TOOL, Aspect.WATER, Aspect.MOTION))
                .position(-7, 4)
                .complexity(2)
                .icon(new ItemStack(TCItems.ELEMENTAL_AXE.get()))
                .parents("THAUMIUM", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("ELEMENTALAXE", 1), ResearchPage.infusionCrafting(Thaumcraft.id("elemental_axe")),
                        page("ELEMENTALAXE", 2))
                .build());
        register(ResearchEntry.builder("ELEMENTALPICK", ARTIFICE)
                .tags(List.of(Aspect.TOOL, Aspect.FIRE, Aspect.SENSES))
                .position(-7, 3)
                .complexity(2)
                .icon(new ItemStack(TCItems.ELEMENTAL_PICKAXE.get()))
                .parents("THAUMIUM", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("ELEMENTALPICK", 1), ResearchPage.infusionCrafting(Thaumcraft.id("elemental_pickaxe")),
                        page("ELEMENTALPICK", 2))
                .build());
        register(ResearchEntry.builder("ELEMENTALSHOVEL", ARTIFICE)
                .tags(List.of(Aspect.TOOL, Aspect.EARTH, Aspect.CRAFT))
                .position(-7, 6)
                .complexity(2)
                .icon(new ItemStack(TCItems.ELEMENTAL_SHOVEL.get()))
                .parents("THAUMIUM", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("ELEMENTALSHOVEL", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("elemental_shovel")), page("ELEMENTALSHOVEL", 2))
                .build());
        register(ResearchEntry.builder("ELEMENTALHOE", ARTIFICE)
                .tags(List.of(Aspect.TOOL, Aspect.LIFE, Aspect.CROP))
                .position(-7, 7)
                .complexity(2)
                .icon(new ItemStack(TCItems.ELEMENTAL_HOE.get()))
                .parents("THAUMIUM", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("ELEMENTALHOE", 1), ResearchPage.infusionCrafting(Thaumcraft.id("elemental_hoe")))
                .build());
        register(ResearchEntry.builder("ELEMENTALSWORD", ARTIFICE)
                .tags(List.of(Aspect.WEAPON, Aspect.AIR, Aspect.ENERGY))
                .position(-7, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.ELEMENTAL_SWORD.get()))
                .parents("THAUMIUM", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("ELEMENTALSWORD", 1), ResearchPage.infusionCrafting(Thaumcraft.id("elemental_sword")))
                .build());
        register(ResearchEntry.builder("INFUSIONENCHANTMENT", ARTIFICE)
                .tags(List.of(Aspect.MAGIC, Aspect.CRAFT, Aspect.EXCHANGE))
                .position(-6, 8)
                .complexity(2)
                .icon(miscIcon("r_infusionenchant"))
                .parents("INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("INFUSIONENCHANTMENT", 1), page("INFUSIONENCHANTMENT", 2),
                        page("INFUSIONENCHANTMENT", 3),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_protection")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_fire_protection")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_blast_protection")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_projectile_protection")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_feather_falling")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_respiration")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_aqua_affinity")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_thorns")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_sharpness")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_smite")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_bane_of_arthropods")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_knockback")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_fire_aspect")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_looting")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_efficiency")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_silk_touch")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_unbreaking")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_fortune")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_power")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_punch")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_flame")),
                        ResearchPage.infusionEnchantment(Thaumcraft.id("infusion_enchant_infinity")))
                .build());
        register(ResearchEntry.builder("ARMORFORTRESS", ARTIFICE)
                .tags(List.of(Aspect.METAL, Aspect.ARMOR, Aspect.CRAFT))
                .position(-8, 9)
                .complexity(2)
                .icon(new ItemStack(TCItems.FORTRESS_HELMET.get()))
                .parents("THAUMIUM", "INFUSIONENCHANTMENT")
                .flag(ResearchFlag.HIDDEN)
                .aspectTriggers(Aspect.ARMOR)
                .pages(page("ARMORFORTRESS", 1), page("ARMORFORTRESS", 2),
                        ResearchPage.infusionCrafting(Thaumcraft.id("fortress_helmet")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("fortress_chestplate")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("fortress_leggings")))
                .build());
        register(ResearchEntry.builder("HELMGOGGLES", ARTIFICE)
                .tags(List.of(Aspect.SENSES, Aspect.AURA, Aspect.ARMOR))
                .position(-9, 7)
                .complexity(2)
                .icon(new ItemStack(TCItems.GOGGLES.get()))
                .parents("ARMORFORTRESS")
                .hiddenParents("GOGGLES")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("HELMGOGGLES", 1), ResearchPage.infusionCrafting(Thaumcraft.id("helm_goggles")))
                .build());
        register(ResearchEntry.builder("MASKGRINNINGDEVIL", ARTIFICE)
                .tags(List.of(Aspect.HEAL, Aspect.MIND, Aspect.ARMOR))
                .position(-10, 8)
                .complexity(2)
                .icon(Thaumcraft.id("textures/misc/r_mask0.png"))
                .parents("ARMORFORTRESS")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("MASKGRINNINGDEVIL", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("mask_grinning_devil")))
                .build());
        register(ResearchEntry.builder("MASKANGRYGHOST", ARTIFICE)
                .tags(List.of(Aspect.ENTROPY, Aspect.DEATH, Aspect.ARMOR))
                .position(-10, 9)
                .complexity(2)
                .icon(Thaumcraft.id("textures/misc/r_mask1.png"))
                .parents("ARMORFORTRESS")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .warp(1)
                .pages(page("MASKANGRYGHOST", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("mask_angry_ghost")))
                .build());
        register(ResearchEntry.builder("MASKSIPPINGFIEND", ARTIFICE)
                .tags(List.of(Aspect.UNDEAD, Aspect.LIFE, Aspect.ARMOR))
                .position(-10, 10)
                .complexity(2)
                .icon(Thaumcraft.id("textures/misc/r_mask2.png"))
                .parents("ARMORFORTRESS")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .warp(1)
                .pages(page("MASKSIPPINGFIEND", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("mask_sipping_fiend")))
                .build());
        register(ResearchEntry.builder("NODESTABILIZER", THAUMATURGY)
                .tags(List.of(Aspect.AURA, Aspect.ORDER, Aspect.ENERGY))
                .position(-7, -4)
                .complexity(1)
                .icon(new ItemStack(TCItems.NODE_STABILIZER.get()))
                .parents("NODEPRESERVE")
                .pages(page("NODESTABILIZER", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("node_stabilizer")),
                        page("NODESTABILIZER", 2))
                .build());
        register(ResearchEntry.builder("NODESTABILIZERADV", THAUMATURGY)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.ORDER, Aspect.ENERGY))
                .position(-8, -3)
                .complexity(2)
                .icon(new ItemStack(TCItems.ADVANCED_NODE_STABILIZER.get()))
                .parents("NODESTABILIZER")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("NODESTABILIZERADV", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("advanced_node_stabilizer")))
                .build());
        register(ResearchEntry.builder("WANDPED", THAUMATURGY)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.EXCHANGE, Aspect.ENERGY))
                .position(-9, -6)
                .complexity(2)
                .icon(new ItemStack(TCItems.WAND_RECHARGE_PEDESTAL.get()))
                .parents("INFUSION", "NODEPRESERVE", "NODESTABILIZER")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("WANDPED", 1), ResearchPage.infusionCrafting(Thaumcraft.id("wand_recharge_pedestal")))
                .build());
        register(ResearchEntry.builder("VISAMULET", THAUMATURGY)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.ENERGY, Aspect.VOID))
                .position(-9, -8)
                .complexity(2)
                .icon(new ItemStack(TCItems.VIS_AMULET.get()))
                .parents("WANDPED")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("VISAMULET", 1), ResearchPage.infusionCrafting(Thaumcraft.id("vis_amulet")),
                        page("VISAMULET", 2))
                .build());
        register(ResearchEntry.builder("WANDPEDFOC", THAUMATURGY)
                .tags(List.of(Aspect.AURA, Aspect.MAGIC, Aspect.EXCHANGE, Aspect.ENERGY, Aspect.TOOL))
                .position(-10, -7)
                .complexity(3)
                .icon(new ItemStack(TCItems.COMPOUND_RECHARGE_FOCUS.get()))
                .parents("WANDPED")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("WANDPEDFOC", 1), ResearchPage.infusionCrafting(Thaumcraft.id("compound_recharge_focus")))
                .build());
        register(ResearchEntry.builder("CAP_gold", THAUMATURGY)
                .tags(List.of(Aspect.METAL, Aspect.GREED, Aspect.TOOL))
                .position(3, 2)
                .complexity(1)
                .icon(new ItemStack(TCItems.GOLD_WAND_CAP.get()))
                .parents("BASICTHAUMATURGY")
                .pages(page("CAP_gold", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("gold_wand_cap")))
                .build());
        register(ResearchEntry.builder("CAP_thaumium", THAUMATURGY)
                .tags(List.of(Aspect.METAL, Aspect.MAGIC, Aspect.TOOL, Aspect.AURA))
                .position(5, 4)
                .complexity(2)
                .icon(new ItemStack(TCItems.THAUMIUM_WAND_CAP.get()))
                .parents("CAP_gold", "INFUSION")
                .pages(page("CAP_thaumium", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("thaumium_wand_cap_inert")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("thaumium_wand_cap_infusion")))
                .build());
        register(ResearchEntry.builder("CAP_silver", THAUMATURGY)
                .tags(List.of(Aspect.METAL, Aspect.GREED, Aspect.TOOL, Aspect.AURA))
                .position(5, 1)
                .complexity(1)
                .icon(new ItemStack(TCItems.SILVER_WAND_CAP.get()))
                .parents("CAP_gold", "INFUSION")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("CAP_silver", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("silver_wand_cap_inert")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("silver_wand_cap_infusion")))
                .build());
        register(ResearchEntry.builder("ROD_wood", THAUMATURGY)
                .flags(ResearchFlag.AUTO_UNLOCK, ResearchFlag.STUB)
                .build());
        register(ResearchEntry.builder("ROD_greatwood", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.TREE, Aspect.MAGIC))
                .position(-5, 2)
                .complexity(1)
                .icon(new ItemStack(TCItems.GREATWOOD_WAND_ROD.get()))
                .parents("BASICTHAUMATURGY")
                .pages(page("ROD_greatwood", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("greatwood_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_reed", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.AIR, Aspect.PLANT, Aspect.MAGIC))
                .position(-5, -1)
                .complexity(2)
                .icon(new ItemStack(TCItems.REED_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_reed", 1), ResearchPage.infusionCrafting(Thaumcraft.id("reed_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_blaze", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.FIRE, Aspect.ENERGY, Aspect.MAGIC))
                .position(-7, 0)
                .complexity(2)
                .icon(new ItemStack(TCItems.BLAZE_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_blaze", 1), ResearchPage.infusionCrafting(Thaumcraft.id("blaze_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_obsidian", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.EARTH, Aspect.FIRE, Aspect.MAGIC))
                .position(-8, 2)
                .complexity(2)
                .icon(new ItemStack(TCItems.OBSIDIAN_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_obsidian", 1), ResearchPage.infusionCrafting(Thaumcraft.id("obsidian_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_ice", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.COLD, Aspect.WATER, Aspect.MAGIC))
                .position(-7, 4)
                .complexity(2)
                .icon(new ItemStack(TCItems.ICE_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_ice", 1), ResearchPage.infusionCrafting(Thaumcraft.id("ice_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_quartz", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.ORDER, Aspect.CRYSTAL, Aspect.MAGIC))
                .position(-5, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.QUARTZ_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_quartz", 1), ResearchPage.infusionCrafting(Thaumcraft.id("quartz_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_bone", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.ENTROPY, Aspect.UNDEAD, Aspect.MAGIC))
                .position(-3, 0)
                .complexity(2)
                .icon(new ItemStack(TCItems.BONE_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_bone", 1), ResearchPage.infusionCrafting(Thaumcraft.id("bone_wand_rod")))
                .warp(1)
                .build());
        register(ResearchEntry.builder("ROD_silverwood", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.TREE, Aspect.MAGIC))
                .position(-2, 5)
                .complexity(3)
                .icon(new ItemStack(TCItems.SILVERWOOD_WAND_ROD.get()))
                .parents("ROD_greatwood", "INFUSION")
                .pages(page("ROD_silverwood", 1), ResearchPage.infusionCrafting(Thaumcraft.id("silverwood_wand_rod")))
                .build());
        register(ResearchEntry.builder("ROD_greatwood_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.TREE, Aspect.MAGIC))
                .position(-1, 7)
                .complexity(1)
                .icon(new ItemStack(TCItems.GREATWOOD_STAFF_CORE.get()))
                .parents("ROD_silverwood")
                .pages(page("ROD_greatwood_staff", 1), page("ROD_greatwood_staff", 2),
                        ResearchPage.arcaneCrafting(Thaumcraft.id("greatwood_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_reed_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.AIR, Aspect.PLANT, Aspect.MAGIC))
                .position(-5, -2)
                .complexity(2)
                .icon(new ItemStack(TCItems.REED_STAFF_CORE.get()))
                .parents("ROD_reed")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_reed_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("reed_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_blaze_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.FIRE, Aspect.ENERGY, Aspect.MAGIC))
                .position(-8, -1)
                .complexity(2)
                .icon(new ItemStack(TCItems.BLAZE_STAFF_CORE.get()))
                .parents("ROD_blaze")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_blaze_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("blaze_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_obsidian_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.EARTH, Aspect.FIRE, Aspect.MAGIC))
                .position(-9, 2)
                .complexity(2)
                .icon(new ItemStack(TCItems.OBSIDIAN_STAFF_CORE.get()))
                .parents("ROD_obsidian")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_obsidian_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("obsidian_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_ice_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.COLD, Aspect.WATER, Aspect.MAGIC))
                .position(-8, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.ICE_STAFF_CORE.get()))
                .parents("ROD_ice")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_ice_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("ice_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_quartz_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.ORDER, Aspect.CRYSTAL, Aspect.MAGIC))
                .position(-4, 6)
                .complexity(2)
                .icon(new ItemStack(TCItems.QUARTZ_STAFF_CORE.get()))
                .parents("ROD_quartz")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_quartz_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("quartz_staff_core")))
                .build());
        register(ResearchEntry.builder("ROD_bone_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.ENTROPY, Aspect.UNDEAD, Aspect.MAGIC))
                .position(-2, -1)
                .complexity(2)
                .icon(new ItemStack(TCItems.BONE_STAFF_CORE.get()))
                .parents("ROD_bone")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_bone_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("bone_staff_core")))
                .warp(1)
                .build());
        register(ResearchEntry.builder("ROD_silverwood_staff", THAUMATURGY)
                .tags(List.of(Aspect.TOOL, Aspect.TREE, Aspect.MAGIC))
                .position(-1, 5)
                .complexity(3)
                .icon(new ItemStack(TCItems.SILVERWOOD_STAFF_CORE.get()))
                .parents("ROD_silverwood")
                .hiddenParents("ROD_greatwood_staff")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ROD_silverwood_staff", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("silverwood_staff_core")))
                .build());
        register(ResearchEntry.builder("JARBRAIN", ARTIFICE)
                .tags(List.of(Aspect.HUNGER, Aspect.MIND, Aspect.UNDEAD, Aspect.GREED))
                .position(-5, 9)
                .complexity(2)
                .icon(new ItemStack(TCItems.BRAIN_IN_A_JAR.get()))
                .parents("INFUSION")
                .flags(ResearchFlag.HIDDEN)
                .itemTriggers(new ItemStack(TCItems.ZOMBIE_BRAIN.get()))
                .pages(page("JARBRAIN", 1), ResearchPage.infusionCrafting(Thaumcraft.id("brain_in_a_jar_infusion")))
                .warp(3)
                .build());
        register(ResearchEntry.builder("ELDRITCHMINOR", ELDRITCH)
                .position(1, 0)
                .complexity(0)
                .icon(miscIcon("r_eldritchminor"))
                .flags(ResearchFlag.HIDDEN, ResearchFlag.ROUND, ResearchFlag.SPECIAL)
                .pages(page("ELDRITCHMINOR", 1))
                .build());
        register(ResearchEntry.builder("ELDRITCHMAJOR", ELDRITCH)
                .tags(List.of(Aspect.ELDRITCH, Aspect.MIND, Aspect.DARKNESS))
                .position(2, -1)
                .complexity(2)
                .icon(miscIcon("r_eldritch"))
                .parents("ELDRITCHMINOR")
                .flags(ResearchFlag.HIDDEN, ResearchFlag.SPECIAL)
                .pages(page("ELDRITCHMAJOR", 1), page("ELDRITCHMAJOR", 2))
                .warp(3)
                .build());
        register(ResearchEntry.builder("VOIDMETAL", ELDRITCH)
                .tags(List.of(Aspect.VOID, Aspect.METAL, Aspect.ELDRITCH, Aspect.MAGIC))
                .position(3, -2)
                .complexity(2)
                .icon(new ItemStack(TCItems.VOID_INGOT.get()))
                .parents("ELDRITCHMINOR", "THAUMIUM")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("VOIDMETAL", 1), page("VOIDMETAL", 2))
                .warp(1)
                .build());
        register(ResearchEntry.builder("CAP_void", ELDRITCH)
                .tags(List.of(Aspect.VOID, Aspect.ELDRITCH, Aspect.TOOL, Aspect.MAGIC, Aspect.AURA))
                .position(5, -1)
                .complexity(3)
                .icon(new ItemStack(TCItems.VOID_WAND_CAP.get()))
                .parents("CAP_thaumium", "VOIDMETAL")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("CAP_void", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("void_wand_cap_inert")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("void_wand_cap_infusion")))
                .warp(1)
                .build());
        register(ResearchEntry.builder("ARMORVOIDFORTRESS", ELDRITCH)
                .tags(List.of(Aspect.ARMOR, Aspect.ELDRITCH, Aspect.CLOTH, Aspect.DARKNESS, Aspect.VOID))
                .position(0, -3)
                .complexity(3)
                .icon(new ItemStack(TCItems.VOID_ROBE_HELMET.get()))
                .parents("VOIDMETAL", "ENCHFABRIC", "ELDRITCHMAJOR")
                .flags(ResearchFlag.SECONDARY, ResearchFlag.CONCEALED)
                .pages(page("ARMORVOIDFORTRESS", 1),
                        ResearchPage.infusionCrafting(Thaumcraft.id("void_robe_helmet")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("void_robe_chestplate")),
                        ResearchPage.infusionCrafting(Thaumcraft.id("void_robe_leggings")))
                .build());
        register(ResearchEntry.builder("FOCUSPRIMAL", ELDRITCH)
                .tags(List.of(Aspect.AIR, Aspect.WATER, Aspect.FIRE, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY,
                        Aspect.MAGIC))
                .position(4, 1)
                .complexity(2)
                .icon(new ItemStack(TCItems.FOCUS_PRIMAL.get()))
                .parents("ELDRITCHMINOR")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("FOCUSPRIMAL", 1), ResearchPage.arcaneCrafting(Thaumcraft.id("focus_primal")))
                .warp(2)
                .build());
        register(ResearchEntry.builder("PRIMPEARL", ELDRITCH)
                .tags(List.of(Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY))
                .position(0, 4)
                .complexity(1)
                .icon(new ItemStack(TCItems.PRIMORDIAL_PEARL.get()))
                .parents("ELDRITCHMINOR")
                .flags(ResearchFlag.LOST, ResearchFlag.SECONDARY, ResearchFlag.SPECIAL)
                .itemTriggers(new ItemStack(TCItems.PRIMORDIAL_PEARL.get()))
                .pages(page("PRIMPEARL", 1), page("PRIMPEARL", 2))
                .build());
        register(ResearchEntry.builder("PRIMALCRUSHER", ELDRITCH)
                .tags(List.of(Aspect.MINE, Aspect.TOOL, Aspect.ENTROPY, Aspect.VOID, Aspect.WEAPON,
                        Aspect.ELDRITCH, Aspect.GREED))
                .position(2, 5)
                .complexity(2)
                .icon(new ItemStack(TCItems.PRIMAL_CRUSHER.get()))
                .parents("PRIMPEARL")
                .hiddenParents("VOIDMETAL", "ELEMENTALPICK", "ELEMENTALSHOVEL")
                .flag(ResearchFlag.CONCEALED)
                .pages(page("PRIMALCRUSHER", 1), ResearchPage.infusionCrafting(Thaumcraft.id("primal_crusher")),
                        page("PRIMALCRUSHER", 2))
                .build());
        register(ResearchEntry.builder("SANITYCHECK", ELDRITCH)
                .tags(List.of(Aspect.MIND, Aspect.ELDRITCH, Aspect.SENSES))
                .position(2, 2)
                .complexity(1)
                .icon(new ItemStack(TCItems.SANITY_CHECKER.get()))
                .parents("ELDRITCHMINOR")
                .pages(page("SANITYCHECK", 1), ResearchPage.infusionCrafting(Thaumcraft.id("sanity_checker")))
                .build());
        register(ResearchEntry.builder("ROD_primal_staff", ELDRITCH)
                .tags(List.of(Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY,
                        Aspect.TOOL, Aspect.MAGIC))
                .position(6, 2)
                .complexity(3)
                .icon(new ItemStack(TCItems.PRIMAL_STAFF_CORE.get()))
                .parents("FOCUSPRIMAL")
                .hiddenParents("ROD_silverwood_staff", "ROD_bone_staff", "ROD_greatwood_staff", "ROD_blaze_staff",
                        "ROD_reed_staff", "ROD_obsidian_staff", "ROD_quartz_staff", "ROD_ice_staff")
                .flags(ResearchFlag.HIDDEN)
                .itemTriggers(new ItemStack(TCItems.FOCUS_PRIMAL.get()))
                .pages(page("ROD_primal_staff", 1), ResearchPage.infusionCrafting(Thaumcraft.id("primal_staff_core")))
                .warp(3)
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

    private static ResearchPage infusionAltarConstruct() {
        AspectList aspects = new AspectList()
                .add(Aspect.FIRE, 25)
                .add(Aspect.EARTH, 25)
                .add(Aspect.ORDER, 25)
                .add(Aspect.AIR, 25)
                .add(Aspect.ENTROPY, 25)
                .add(Aspect.WATER, 25);
        return compound(aspects, 3, 3, 3, List.of(
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                stack(TCBlocks.RUNIC_MATRIX.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE.get()),
                stack(TCBlocks.ARCANE_STONE_BRICKS.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE_BRICKS.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_PEDESTAL.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE_BRICKS.get()),
                ItemStack.EMPTY,
                stack(TCBlocks.ARCANE_STONE_BRICKS.get())));
    }

    private static ResearchPage nodeJarConstruct() {
        AspectList aspects = new AspectList()
                .add(Aspect.FIRE, 70)
                .add(Aspect.EARTH, 70)
                .add(Aspect.AIR, 70)
                .add(Aspect.WATER, 70)
                .add(Aspect.ORDER, 70)
                .add(Aspect.ENTROPY, 70);
        return compound(aspects, 3, 4, 3, List.of(
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.OAK_SLAB),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                new ItemStack(TCItems.AURA_NODE.get()),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS),
                stack(Blocks.GLASS)));
    }

    private static ResearchPage compound(AspectList aspects, int width, int height, int depth, List<ItemStack> stacks) {
        return ResearchPage.compoundCrafting(new ResearchPage.CompoundCrafting(aspects, width, height, depth, stacks));
    }

    private static ItemStack stack(net.minecraft.world.level.ItemLike item) {
        return new ItemStack(item);
    }

    private static ResourceLocation miscIcon(String name) {
        return Thaumcraft.id("textures/misc/" + name + ".png");
    }
}
