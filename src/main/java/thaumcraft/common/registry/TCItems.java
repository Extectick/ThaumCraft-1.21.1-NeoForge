package thaumcraft.common.registry;

import java.util.List;

import thaumcraft.common.items.equipment.*;
import net.minecraft.world.item.ArmorItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.Thaumcraft;
import thaumcraft.common.curios.TCSlots;
import thaumcraft.common.items.JarBlockItem;
import thaumcraft.common.items.NodeJarBlockItem;
import thaumcraft.common.items.AuraNodeItem;
import thaumcraft.common.items.EssenceItem;
import thaumcraft.common.items.HandMirrorItem;
import thaumcraft.common.items.ScribingToolsItem;
import thaumcraft.common.items.ResearchNotesItem;
import thaumcraft.common.items.SanityCheckerItem;
import thaumcraft.common.items.SinisterLodestoneItem;
import thaumcraft.common.items.CrimsonRitesItem;
import thaumcraft.common.items.PrimordialPearlItem;
import thaumcraft.common.items.ThaumometerItem;
import thaumcraft.common.items.ThaumonomiconItem;
import thaumcraft.common.items.EssentiaPhialItem;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.curios.HoverGirdleItem;
import thaumcraft.common.items.curios.RunicCurioItem;
import thaumcraft.common.items.curios.ThaumcraftCurioItem;
import thaumcraft.common.items.curios.VisAmuletItem;
import thaumcraft.common.items.curios.VisDiscountCurioItem;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandPartItem;

public final class TCItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final DeferredItem<BlockItem> CINNABAR_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.CINNABAR_ORE);
    public static final DeferredItem<BlockItem> INFUSED_AIR_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_AIR_ORE);
    public static final DeferredItem<BlockItem> INFUSED_FIRE_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_FIRE_ORE);
    public static final DeferredItem<BlockItem> INFUSED_WATER_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_WATER_ORE);
    public static final DeferredItem<BlockItem> INFUSED_EARTH_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_EARTH_ORE);
    public static final DeferredItem<BlockItem> INFUSED_ORDER_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_ORDER_ORE);
    public static final DeferredItem<BlockItem> INFUSED_ENTROPY_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSED_ENTROPY_ORE);
    public static final DeferredItem<BlockItem> AMBER_ORE = REGISTRY.registerSimpleBlockItem(TCBlocks.AMBER_ORE);
    public static final DeferredItem<BlockItem> AMBER_BLOCK = REGISTRY.registerSimpleBlockItem(TCBlocks.AMBER_BLOCK);
    public static final DeferredItem<BlockItem> AMBER_BRICKS = REGISTRY.registerSimpleBlockItem(TCBlocks.AMBER_BRICKS);
    public static final DeferredItem<BlockItem> THAUMIUM_BLOCK = REGISTRY.registerSimpleBlockItem(TCBlocks.THAUMIUM_BLOCK);
    public static final DeferredItem<BlockItem> TALLOW_BLOCK = REGISTRY.registerSimpleBlockItem(TCBlocks.TALLOW_BLOCK);
    public static final DeferredItem<BlockItem> ARCANE_STONE = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_STONE);
    public static final DeferredItem<BlockItem> ARCANE_STONE_BRICKS = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_STONE_BRICKS);
    public static final DeferredItem<BlockItem> ANCIENT_STONE = REGISTRY.registerSimpleBlockItem(TCBlocks.ANCIENT_STONE);
    public static final DeferredItem<BlockItem> ANCIENT_ROCK = REGISTRY.registerSimpleBlockItem(TCBlocks.ANCIENT_ROCK);
    public static final DeferredItem<BlockItem> CRUSTED_STONE = REGISTRY.registerSimpleBlockItem(TCBlocks.CRUSTED_STONE);
    public static final DeferredItem<BlockItem> OBSIDIAN_TILE = REGISTRY.registerSimpleBlockItem(TCBlocks.OBSIDIAN_TILE);
    public static final DeferredItem<BlockItem> PAVING_STONE_TRAVEL = REGISTRY.registerSimpleBlockItem(TCBlocks.PAVING_STONE_TRAVEL);
    public static final DeferredItem<BlockItem> PAVING_STONE_WARDING = REGISTRY.registerSimpleBlockItem(TCBlocks.PAVING_STONE_WARDING);
    public static final DeferredItem<BlockItem> WARDING_BARRIER = REGISTRY.registerSimpleBlockItem(TCBlocks.WARDING_BARRIER);
    public static final DeferredItem<BlockItem> HUNGRY_CHEST = REGISTRY.registerSimpleBlockItem(TCBlocks.HUNGRY_CHEST);
    public static final DeferredItem<BlockItem> GREATWOOD_LOG = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_LOG);
    public static final DeferredItem<BlockItem> SILVERWOOD_LOG = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_LOG);
    public static final DeferredItem<BlockItem> ARCANE_WOOD = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_WOOD);
    public static final DeferredItem<BlockItem> GREATWOOD_PLANKS = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_PLANKS);
    public static final DeferredItem<BlockItem> SILVERWOOD_PLANKS = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_PLANKS);
    public static final DeferredItem<BlockItem> GREATWOOD_LEAVES = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_LEAVES);
    public static final DeferredItem<BlockItem> SILVERWOOD_LEAVES = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_LEAVES);
    public static final DeferredItem<BlockItem> GREATWOOD_SAPLING = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_SAPLING);
    public static final DeferredItem<BlockItem> SILVERWOOD_SAPLING = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_SAPLING);
    public static final DeferredItem<BlockItem> SHIMMERLEAF = REGISTRY.registerSimpleBlockItem(TCBlocks.SHIMMERLEAF);
    public static final DeferredItem<BlockItem> CINDERPEARL = REGISTRY.registerSimpleBlockItem(TCBlocks.CINDERPEARL);
    public static final DeferredItem<BlockItem> VISHROOM = REGISTRY.registerSimpleBlockItem(TCBlocks.VISHROOM);
    public static final DeferredItem<BlockItem> TABLE = REGISTRY.registerSimpleBlockItem(TCBlocks.TABLE);
    public static final DeferredItem<BlockItem> RESEARCH_TABLE = REGISTRY.registerSimpleBlockItem(TCBlocks.RESEARCH_TABLE);
    public static final DeferredItem<BlockItem> DECONSTRUCTION_TABLE = REGISTRY.registerSimpleBlockItem(TCBlocks.DECONSTRUCTION_TABLE);
    public static final DeferredItem<BlockItem> ARCANE_WORKTABLE = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_WORKTABLE);
    public static final DeferredItem<BlockItem> ALCHEMICAL_FURNACE = REGISTRY.registerSimpleBlockItem(TCBlocks.ALCHEMICAL_FURNACE);
    public static final DeferredItem<BlockItem> ARCANE_PEDESTAL = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_PEDESTAL);
    public static final DeferredItem<BlockItem> WAND_RECHARGE_PEDESTAL = REGISTRY.registerSimpleBlockItem(TCBlocks.WAND_RECHARGE_PEDESTAL);
    public static final DeferredItem<BlockItem> COMPOUND_RECHARGE_FOCUS = REGISTRY.registerSimpleBlockItem(TCBlocks.COMPOUND_RECHARGE_FOCUS);
    public static final DeferredItem<BlockItem> ARCANE_SPA = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_SPA);
    public static final DeferredItem<BlockItem> RUNIC_MATRIX = REGISTRY.registerSimpleBlockItem(TCBlocks.RUNIC_MATRIX);
    public static final DeferredItem<AuraNodeItem> AURA_NODE = REGISTRY.registerItem("aura_node",
            properties -> new AuraNodeItem(TCBlocks.AURA_NODE.get(), properties));
    public static final DeferredItem<BlockItem> NODE_STABILIZER = REGISTRY.registerSimpleBlockItem(TCBlocks.NODE_STABILIZER);
    public static final DeferredItem<BlockItem> ADVANCED_NODE_STABILIZER = REGISTRY.registerSimpleBlockItem(TCBlocks.ADVANCED_NODE_STABILIZER);
    public static final DeferredItem<BlockItem> NODE_TRANSDUCER = REGISTRY.registerSimpleBlockItem(TCBlocks.NODE_TRANSDUCER);
    public static final DeferredItem<BlockItem> FOCAL_MANIPULATOR = REGISTRY.registerSimpleBlockItem(TCBlocks.FOCAL_MANIPULATOR);
    public static final DeferredItem<BlockItem> FLUX_SCRUBBER = REGISTRY.registerSimpleBlockItem(TCBlocks.FLUX_SCRUBBER);
    public static final DeferredItem<BlockItem> FLUX_GOO = REGISTRY.registerSimpleBlockItem(TCBlocks.FLUX_GOO);
    public static final DeferredItem<BlockItem> FLUX_GAS = REGISTRY.registerSimpleBlockItem(TCBlocks.FLUX_GAS);
    public static final DeferredItem<BlockItem> CRUCIBLE = REGISTRY.registerSimpleBlockItem(TCBlocks.CRUCIBLE);
    public static final DeferredItem<BlockItem> ARCANE_ALEMBIC = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_ALEMBIC);
    public static final DeferredItem<BlockItem> VIS_CHARGE_RELAY = REGISTRY.registerSimpleBlockItem(TCBlocks.VIS_CHARGE_RELAY);
    public static final DeferredItem<BlockItem> ADVANCED_ALCHEMICAL_CONSTRUCT = REGISTRY.registerSimpleBlockItem(TCBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT);
    public static final DeferredItem<BlockItem> ITEM_GRATE = REGISTRY.registerSimpleBlockItem(TCBlocks.ITEM_GRATE);
    public static final DeferredItem<BlockItem> ARCANE_LAMP = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_LAMP);
    public static final DeferredItem<BlockItem> LAMP_OF_GROWTH = REGISTRY.registerSimpleBlockItem(TCBlocks.LAMP_OF_GROWTH);
    public static final DeferredItem<BlockItem> ALCHEMICAL_CONSTRUCT = REGISTRY.registerSimpleBlockItem(TCBlocks.ALCHEMICAL_CONSTRUCT);
    public static final DeferredItem<BlockItem> THAUMATORIUM = REGISTRY.registerSimpleBlockItem(TCBlocks.THAUMATORIUM);
    public static final DeferredItem<BlockItem> MNEMONIC_MATRIX = REGISTRY.registerSimpleBlockItem(TCBlocks.MNEMONIC_MATRIX);
    public static final DeferredItem<BlockItem> LAMP_OF_FERTILITY = REGISTRY.registerSimpleBlockItem(TCBlocks.LAMP_OF_FERTILITY);
    public static final DeferredItem<BlockItem> VIS_RELAY = REGISTRY.registerSimpleBlockItem(TCBlocks.VIS_RELAY);
    public static final DeferredItem<BlockItem> ESSENTIA_TUBE = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_TUBE);
    public static final DeferredItem<BlockItem> ESSENTIA_VALVE = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_VALVE);
    public static final DeferredItem<BlockItem> ALCHEMICAL_CENTRIFUGE = REGISTRY.registerSimpleBlockItem(TCBlocks.ALCHEMICAL_CENTRIFUGE);
    public static final DeferredItem<BlockItem> FILTERED_ESSENTIA_TUBE = REGISTRY.registerSimpleBlockItem(TCBlocks.FILTERED_ESSENTIA_TUBE);
    public static final DeferredItem<BlockItem> ESSENTIA_BUFFER = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_BUFFER);
    public static final DeferredItem<BlockItem> RESTRICTED_ESSENTIA_TUBE = REGISTRY.registerSimpleBlockItem(TCBlocks.RESTRICTED_ESSENTIA_TUBE);
    public static final DeferredItem<BlockItem> DIRECTIONAL_ESSENTIA_TUBE = REGISTRY.registerSimpleBlockItem(TCBlocks.DIRECTIONAL_ESSENTIA_TUBE);
    public static final DeferredItem<BlockItem> ESSENTIA_CRYSTALLIZER = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_CRYSTALLIZER);
    public static final DeferredItem<JarBlockItem> WARDED_JAR = REGISTRY.registerItem("warded_jar",
            properties -> new JarBlockItem(TCBlocks.WARDED_JAR.get(), properties));
    public static final DeferredItem<BlockItem> BRAIN_IN_A_JAR = REGISTRY.registerSimpleBlockItem(TCBlocks.BRAIN_IN_A_JAR);
    public static final DeferredItem<NodeJarBlockItem> NODE_IN_A_JAR = REGISTRY.registerItem("node_in_a_jar",
            properties -> new NodeJarBlockItem(TCBlocks.NODE_IN_A_JAR.get(), properties));
    public static final DeferredItem<JarBlockItem> VOID_JAR = REGISTRY.registerItem("void_jar",
            properties -> new JarBlockItem(TCBlocks.VOID_JAR.get(), properties));
    public static final DeferredItem<BlockItem> MAGIC_MIRROR = REGISTRY.registerSimpleBlockItem(TCBlocks.MAGIC_MIRROR);
    public static final DeferredItem<BlockItem> ESSENTIA_MIRROR = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_MIRROR);
    public static final DeferredItem<HandMirrorItem> HAND_MIRROR = REGISTRY.register("hand_mirror", HandMirrorItem::new);
    public static final DeferredItem<BlockItem> ARCANE_STONE_STAIRS = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_STONE_STAIRS);
    public static final DeferredItem<BlockItem> GREATWOOD_STAIRS = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_STAIRS);
    public static final DeferredItem<BlockItem> SILVERWOOD_STAIRS = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_STAIRS);
    public static final DeferredItem<BlockItem> ARCANE_STONE_SLAB = REGISTRY.registerSimpleBlockItem(TCBlocks.ARCANE_STONE_SLAB);
    public static final DeferredItem<BlockItem> GREATWOOD_SLAB = REGISTRY.registerSimpleBlockItem(TCBlocks.GREATWOOD_SLAB);
    public static final DeferredItem<BlockItem> SILVERWOOD_SLAB = REGISTRY.registerSimpleBlockItem(TCBlocks.SILVERWOOD_SLAB);
    public static final DeferredItem<BlockItem> WHITE_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.WHITE_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> ORANGE_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.ORANGE_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> MAGENTA_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.MAGENTA_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> LIGHT_BLUE_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.LIGHT_BLUE_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> YELLOW_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.YELLOW_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> LIME_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.LIME_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> PINK_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.PINK_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> GRAY_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.GRAY_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> LIGHT_GRAY_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.LIGHT_GRAY_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> CYAN_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.CYAN_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> PURPLE_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.PURPLE_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> BLUE_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.BLUE_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> BROWN_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.BROWN_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> GREEN_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.GREEN_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> RED_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.RED_TALLOW_CANDLE);
    public static final DeferredItem<BlockItem> BLACK_TALLOW_CANDLE = REGISTRY.registerSimpleBlockItem(TCBlocks.BLACK_TALLOW_CANDLE);
    public static final DeferredItem<WandPartItem> IRON_WAND_CAP = wandCap("iron_wand_cap", "iron");
    public static final DeferredItem<WandPartItem> COPPER_WAND_CAP = wandCap("copper_wand_cap", "copper");
    public static final DeferredItem<WandPartItem> GOLD_WAND_CAP = wandCap("gold_wand_cap", "gold");
    public static final DeferredItem<WandPartItem> SILVER_WAND_CAP = wandCap("silver_wand_cap", "silver");
    public static final DeferredItem<WandPartItem> THAUMIUM_WAND_CAP = wandCap("thaumium_wand_cap", "thaumium");
    public static final DeferredItem<WandPartItem> VOID_WAND_CAP = wandCap("void_wand_cap", "void");
    public static final DeferredItem<Item> SILVER_WAND_CAP_INERT = simple("silver_wand_cap_inert");
    public static final DeferredItem<Item> THAUMIUM_WAND_CAP_INERT = simple("thaumium_wand_cap_inert");
    public static final DeferredItem<Item> VOID_WAND_CAP_INERT = simple("void_wand_cap_inert");
    public static final DeferredItem<WandPartItem> GREATWOOD_WAND_ROD = wandRod("greatwood_wand_rod", "greatwood");
    public static final DeferredItem<WandPartItem> OBSIDIAN_WAND_ROD = wandRod("obsidian_wand_rod", "obsidian");
    public static final DeferredItem<WandPartItem> SILVERWOOD_WAND_ROD = wandRod("silverwood_wand_rod", "silverwood");
    public static final DeferredItem<WandPartItem> ICE_WAND_ROD = wandRod("ice_wand_rod", "ice");
    public static final DeferredItem<WandPartItem> QUARTZ_WAND_ROD = wandRod("quartz_wand_rod", "quartz");
    public static final DeferredItem<WandPartItem> REED_WAND_ROD = wandRod("reed_wand_rod", "reed");
    public static final DeferredItem<WandPartItem> BLAZE_WAND_ROD = wandRod("blaze_wand_rod", "blaze");
    public static final DeferredItem<WandPartItem> BONE_WAND_ROD = wandRod("bone_wand_rod", "bone");
    public static final DeferredItem<WandPartItem> GREATWOOD_STAFF_CORE = wandRod("greatwood_staff_core", "greatwood_staff");
    public static final DeferredItem<WandPartItem> OBSIDIAN_STAFF_CORE = wandRod("obsidian_staff_core", "obsidian_staff");
    public static final DeferredItem<WandPartItem> SILVERWOOD_STAFF_CORE = wandRod("silverwood_staff_core", "silverwood_staff");
    public static final DeferredItem<WandPartItem> ICE_STAFF_CORE = wandRod("ice_staff_core", "ice_staff");
    public static final DeferredItem<WandPartItem> QUARTZ_STAFF_CORE = wandRod("quartz_staff_core", "quartz_staff");
    public static final DeferredItem<WandPartItem> REED_STAFF_CORE = wandRod("reed_staff_core", "reed_staff");
    public static final DeferredItem<WandPartItem> BLAZE_STAFF_CORE = wandRod("blaze_staff_core", "blaze_staff");
    public static final DeferredItem<WandPartItem> BONE_STAFF_CORE = wandRod("bone_staff_core", "bone_staff");
    public static final DeferredItem<WandPartItem> PRIMAL_STAFF_CORE = wandRod("primal_staff_core", "primal_staff");
    public static final DeferredItem<Item> WAND_CASTING = REGISTRY.registerItem("wand_casting", WandCastingItem::new);
    public static final DeferredItem<Item> THAUMONOMICON = REGISTRY.registerItem("thaumonomicon", ThaumonomiconItem::new);
    public static final DeferredItem<Item> THAUMOMETER = REGISTRY.registerItem("thaumometer", ThaumometerItem::new);
    public static final DeferredItem<Item> ALUMENTUM = simple("alumentum");
    public static final DeferredItem<Item> NITOR = simple("nitor");
    public static final DeferredItem<Item> THAUMIUM_INGOT = simple("thaumium_ingot");
    public static final DeferredItem<Item> QUICKSILVER = simple("quicksilver");
    public static final DeferredItem<Item> MAGIC_TALLOW = simple("magic_tallow");
    public static final DeferredItem<Item> AMBER = simple("amber");
    public static final DeferredItem<Item> ENCHANTED_FABRIC = simple("enchanted_fabric");
    public static final DeferredItem<Item> VIS_FILTER = simple("vis_filter");
    public static final DeferredItem<Item> KNOWLEDGE_FRAGMENT = simple("knowledge_fragment");
    public static final DeferredItem<Item> MIRRORED_GLASS = simple("mirrored_glass");
    public static final DeferredItem<Item> TAINTED_GOO = simple("tainted_goo");
    public static final DeferredItem<Item> TAINT_TENDRIL = simple("taint_tendril");
    public static final DeferredItem<Item> JAR_LABEL = simple("jar_label");
    public static final DeferredItem<Item> SALIS_MUNDUS = simple("salis_mundus");
    public static final DeferredItem<Item> VOID_INGOT = simple("void_ingot");
    public static final DeferredItem<Item> VOID_SEED = simple("void_seed");
    public static final DeferredItem<Item> GOLD_COIN = simple("gold_coin");
    public static final DeferredItem<Item> ZOMBIE_BRAIN = simple("zombie_brain", food(4, 0.2F));
    public static final DeferredItem<Item> AIR_SHARD = simple("air_shard");
    public static final DeferredItem<Item> FIRE_SHARD = simple("fire_shard");
    public static final DeferredItem<Item> WATER_SHARD = simple("water_shard");
    public static final DeferredItem<Item> EARTH_SHARD = simple("earth_shard");
    public static final DeferredItem<Item> ORDER_SHARD = simple("order_shard");
    public static final DeferredItem<Item> ENTROPY_SHARD = simple("entropy_shard");
    public static final DeferredItem<Item> BALANCED_SHARD = simple("balanced_shard");
    public static final DeferredItem<Item> IRON_NUGGET = simple("iron_nugget");
    public static final DeferredItem<Item> COPPER_NUGGET = simple("copper_nugget");
    public static final DeferredItem<Item> TIN_NUGGET = simple("tin_nugget");
    public static final DeferredItem<Item> SILVER_NUGGET = simple("silver_nugget");
    public static final DeferredItem<Item> LEAD_NUGGET = simple("lead_nugget");
    public static final DeferredItem<Item> QUICKSILVER_DROP = simple("quicksilver_drop");
    public static final DeferredItem<Item> THAUMIUM_NUGGET = simple("thaumium_nugget");
    public static final DeferredItem<Item> VOID_NUGGET = simple("void_nugget");
    public static final DeferredItem<Item> NATIVE_IRON_CLUSTER = simple("native_iron_cluster");
    public static final DeferredItem<Item> NATIVE_COPPER_CLUSTER = simple("native_copper_cluster");
    public static final DeferredItem<Item> NATIVE_TIN_CLUSTER = simple("native_tin_cluster");
    public static final DeferredItem<Item> NATIVE_SILVER_CLUSTER = simple("native_silver_cluster");
    public static final DeferredItem<Item> NATIVE_LEAD_CLUSTER = simple("native_lead_cluster");
    public static final DeferredItem<Item> NATIVE_CINNABAR_CLUSTER = simple("native_cinnabar_cluster");
    public static final DeferredItem<Item> NATIVE_GOLD_CLUSTER = simple("native_gold_cluster");
    public static final DeferredItem<Item> CHICKEN_NUGGET = simple("chicken_nugget", food(1, 0.3F));
    public static final DeferredItem<Item> BEEF_NUGGET = simple("beef_nugget", food(1, 0.3F));
    public static final DeferredItem<Item> PORK_NUGGET = simple("pork_nugget", food(1, 0.3F));
    public static final DeferredItem<Item> FISH_NUGGET = simple("fish_nugget", food(1, 0.3F));
    public static final DeferredItem<Item> TRIPLE_MEAT_TREAT = simple("triple_meat_treat", food(6, 0.8F));
    public static final DeferredItem<Item> TAINT_SLIME = simple("taint_slime");
    public static final DeferredItem<Item> BATH_SALTS = simple("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = simple("primal_charm");
    public static final DeferredItem<CrimsonRitesItem> CRIMSON_RITES = REGISTRY.registerItem("crimson_rites", CrimsonRitesItem::new);
    public static final DeferredItem<PrimordialPearlItem> PRIMORDIAL_PEARL = REGISTRY.registerItem("primordial_pearl", PrimordialPearlItem::new);
    public static final DeferredItem<Item> IRON_ARCANE_KEY = simple("iron_arcane_key");
    public static final DeferredItem<Item> GOLD_ARCANE_KEY = simple("gold_arcane_key");
    public static final DeferredItem<Item> SCRIBING_TOOLS = REGISTRY.registerItem("scribing_tools", ScribingToolsItem::new);
    public static final DeferredItem<Item> RESEARCH_NOTES = REGISTRY.registerItem("research_notes", ResearchNotesItem::new);
    public static final DeferredItem<Item> ETHEREAL_ESSENCE = REGISTRY.registerItem("ethereal_essence", EssenceItem::new);
    public static final DeferredItem<Item> CRYSTALLIZED_ESSENCE = REGISTRY.registerItem("crystallized_essence", EssenceItem::new);
    public static final DeferredItem<Item> GLASS_PHIAL = simple("glass_phial");
    public static final DeferredItem<Item> ESSENTIA_PHIAL = REGISTRY.registerItem("essentia_phial", EssentiaPhialItem::new);
    public static final DeferredItem<Item> MANA_BEAN = simple("mana_bean", food(1, 0.5F, true));
    public static final DeferredItem<Item> RUNIC_RING_LESSER = REGISTRY.registerItem("runic_ring_lesser",
            properties -> new RunicCurioItem(TCSlots.RING, 1, properties));
    public static final DeferredItem<Item> RUNIC_RING = REGISTRY.registerItem("runic_ring",
            properties -> new RunicCurioItem(TCSlots.RING, 5, properties));
    public static final DeferredItem<Item> RUNIC_RING_CHARGED = REGISTRY.registerItem("runic_ring_charged",
            properties -> new RunicCurioItem(TCSlots.RING, 4, properties));
    public static final DeferredItem<Item> RUNIC_RING_REGEN = REGISTRY.registerItem("runic_ring_regen",
            properties -> new RunicCurioItem(TCSlots.RING, 4, properties));
    public static final DeferredItem<Item> RUNIC_AMULET = REGISTRY.registerItem("runic_amulet",
            properties -> new RunicCurioItem(TCSlots.NECKLACE, 8, properties));
    public static final DeferredItem<Item> RUNIC_AMULET_EMERGENCY = REGISTRY.registerItem("runic_amulet_emergency",
            properties -> new RunicCurioItem(TCSlots.NECKLACE, 7, properties));
    public static final DeferredItem<Item> RUNIC_GIRDLE = REGISTRY.registerItem("runic_girdle",
            properties -> new RunicCurioItem(TCSlots.BELT, 10, properties));
    public static final DeferredItem<Item> RUNIC_GIRDLE_KINETIC = REGISTRY.registerItem("runic_girdle_kinetic",
            properties -> new RunicCurioItem(TCSlots.BELT, 9, properties));
    public static final DeferredItem<Item> VIS_AMULET_LESSER = REGISTRY.registerItem("vis_amulet_lesser",
            properties -> new VisAmuletItem(2500, properties));
    public static final DeferredItem<Item> VIS_AMULET = REGISTRY.registerItem("vis_amulet",
            properties -> new VisAmuletItem(25000, properties));
    public static final DeferredItem<Item> HOVER_GIRDLE = REGISTRY.registerItem("hover_girdle", HoverGirdleItem::new);
    public static final DeferredItem<Item> BAUBLE_AMULET = REGISTRY.registerItem("bauble_amulet",
            properties -> new ThaumcraftCurioItem(TCSlots.NECKLACE, properties));
    public static final DeferredItem<Item> BAUBLE_RING = REGISTRY.registerItem("bauble_ring",
            properties -> new ThaumcraftCurioItem(TCSlots.RING, properties));
    public static final DeferredItem<Item> BAUBLE_BELT = REGISTRY.registerItem("bauble_belt",
            properties -> new ThaumcraftCurioItem(TCSlots.BELT, properties));
    public static final DeferredItem<Item> BAUBLE_RING_IRON = REGISTRY.registerItem("bauble_ring_iron",
            properties -> new ThaumcraftCurioItem(TCSlots.RING, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_AIR = REGISTRY.registerItem("vis_discount_ring_air",
            properties -> new VisDiscountCurioItem(Aspect.AIR, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_FIRE = REGISTRY.registerItem("vis_discount_ring_fire",
            properties -> new VisDiscountCurioItem(Aspect.FIRE, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_WATER = REGISTRY.registerItem("vis_discount_ring_water",
            properties -> new VisDiscountCurioItem(Aspect.WATER, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_EARTH = REGISTRY.registerItem("vis_discount_ring_earth",
            properties -> new VisDiscountCurioItem(Aspect.EARTH, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_ORDER = REGISTRY.registerItem("vis_discount_ring_order",
            properties -> new VisDiscountCurioItem(Aspect.ORDER, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_ENTROPY = REGISTRY.registerItem("vis_discount_ring_entropy",
            properties -> new VisDiscountCurioItem(Aspect.ENTROPY, 1, properties));
    public static final DeferredItem<Item> FOCUS_POUCH = REGISTRY.registerItem("focus_pouch", FocusPouchCurioItem::new);
    public static final DeferredItem<ItemFocusBasic> FOCUS_FIRE = focus("focus_fire",
            new PrimalVisStorage(0, 10, 0, 0, 0, 0), 0xE55004, 0, true);
    public static final DeferredItem<ItemFocusBasic> FOCUS_FROST = focus("focus_frost",
            new PrimalVisStorage(0, 2, 5, 0, 0, 2), 0x4F69CC, 200, false);
    public static final DeferredItem<ItemFocusBasic> FOCUS_SHOCK = focus("focus_shock",
            new PrimalVisStorage(25, 0, 0, 0, 0, 0), 0x9FADBF, 250, false);
    public static final DeferredItem<ItemFocusBasic> FOCUS_EXCAVATION = focus("focus_excavation",
            new PrimalVisStorage(0, 0, 0, 15, 0, 0), 0x9A7B4F, 0, true);
    public static final DeferredItem<ItemFocusBasic> FOCUS_PORTABLE_HOLE = focus("focus_portable_hole",
            new PrimalVisStorage(10, 0, 0, 0, 0, 10), 0x444466, 0, false,
            "focus_portable_hole_depth", null, ItemFocusBasic.WandFocusAnimation.CHARGE);
    public static final DeferredItem<ItemFocusBasic> FOCUS_WARDING = focus("focus_warding",
            new PrimalVisStorage(0, 0, 10, 25, 25, 0), 0x6B6BD6, 0, false,
            "focus_warding_depth", "focus_warding_orn", ItemFocusBasic.WandFocusAnimation.CHARGE);
    public static final DeferredItem<ItemFocusBasic> FOCUS_PRIMAL = focus("focus_primal",
            new PrimalVisStorage(50, 50, 50, 50, 50, 50), 0xFFFFFF, 500, false,
            "focus_primal_depth", null, ItemFocusBasic.WandFocusAnimation.CHARGE);
    public static final DeferredItem<ItemFocusBasic> FOCUS_PECH = focus("focus_pech",
            new PrimalVisStorage(0, 0, 10, 10, 0, 10), 0x665038, 500, false,
            "focus_pech_depth", null, ItemFocusBasic.WandFocusAnimation.CHARGE);
    public static final DeferredItem<ItemFocusBasic> FOCUS_HELLBAT = focus("focus_hellbat",
            new PrimalVisStorage(100, 200, 0, 0, 0, 100), 0xB02010, 1000, false,
            null, "focus_hellbat_orn", ItemFocusBasic.WandFocusAnimation.WAVE);
    public static final DeferredItem<ItemFocusBasic> FOCUS_TRADE = focus("focus_trade",
            new PrimalVisStorage(0, 0, 0, 5, 5, 5), 0xC8A050, 0, false,
            null, "focus_trade_orn", ItemFocusBasic.WandFocusAnimation.CHARGE);

    public static final DeferredItem<ThaumiumSwordItem> THAUMIUM_SWORD = REGISTRY.register("thaumium_sword", ThaumiumSwordItem::new);
    public static final DeferredItem<ThaumiumShovelItem> THAUMIUM_SHOVEL = REGISTRY.register("thaumium_shovel", ThaumiumShovelItem::new);
    public static final DeferredItem<ThaumiumPickaxeItem> THAUMIUM_PICKAXE = REGISTRY.register("thaumium_pickaxe", ThaumiumPickaxeItem::new);
    public static final DeferredItem<ThaumiumAxeItem> THAUMIUM_AXE = REGISTRY.register("thaumium_axe", ThaumiumAxeItem::new);
    public static final DeferredItem<ThaumiumHoeItem> THAUMIUM_HOE = REGISTRY.register("thaumium_hoe", ThaumiumHoeItem::new);

    public static final DeferredItem<VoidSwordItem> VOID_SWORD = REGISTRY.register("void_sword", VoidSwordItem::new);
    public static final DeferredItem<VoidShovelItem> VOID_SHOVEL = REGISTRY.register("void_shovel", VoidShovelItem::new);
    public static final DeferredItem<VoidPickaxeItem> VOID_PICKAXE = REGISTRY.register("void_pickaxe", VoidPickaxeItem::new);
    public static final DeferredItem<VoidAxeItem> VOID_AXE = REGISTRY.register("void_axe", VoidAxeItem::new);
    public static final DeferredItem<VoidHoeItem> VOID_HOE = REGISTRY.register("void_hoe", VoidHoeItem::new);

    public static final DeferredItem<ElementalShovelItem> ELEMENTAL_SHOVEL = REGISTRY.register("elemental_shovel", ElementalShovelItem::new);
    public static final DeferredItem<ElementalPickaxeItem> ELEMENTAL_PICKAXE = REGISTRY.register("elemental_pickaxe", ElementalPickaxeItem::new);
    public static final DeferredItem<ElementalAxeItem> ELEMENTAL_AXE = REGISTRY.register("elemental_axe", ElementalAxeItem::new);
    public static final DeferredItem<ElementalHoeItem> ELEMENTAL_HOE = REGISTRY.register("elemental_hoe", ElementalHoeItem::new);
    public static final DeferredItem<ElementalSwordItem> ELEMENTAL_SWORD = REGISTRY.register("elemental_sword", ElementalSwordItem::new);

    public static final DeferredItem<CrimsonBladeItem> CRIMSON_BLADE = REGISTRY.register("crimson_blade", CrimsonBladeItem::new);
    public static final DeferredItem<PrimalCrusherItem> PRIMAL_CRUSHER = REGISTRY.register("primal_crusher", PrimalCrusherItem::new);
    public static final DeferredItem<SanityCheckerItem> SANITY_CHECKER = REGISTRY.register("sanity_checker", SanityCheckerItem::new);
    public static final DeferredItem<SinisterLodestoneItem> SINISTER_LODESTONE = REGISTRY.register("sinister_lodestone", SinisterLodestoneItem::new);

    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_HELMET = REGISTRY.register("thaumium_helmet", () -> new ThaumiumArmorItem(ArmorItem.Type.HELMET, TCArmorMaterials.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_CHESTPLATE = REGISTRY.register("thaumium_chestplate", () -> new ThaumiumArmorItem(ArmorItem.Type.CHESTPLATE, TCArmorMaterials.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_LEGGINGS = REGISTRY.register("thaumium_leggings", () -> new ThaumiumArmorItem(ArmorItem.Type.LEGGINGS, TCArmorMaterials.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_BOOTS = REGISTRY.register("thaumium_boots", () -> new ThaumiumArmorItem(ArmorItem.Type.BOOTS, TCArmorMaterials.THAUMIUM));

    public static final DeferredItem<VoidArmorItem> VOID_HELMET = REGISTRY.register("void_helmet", () -> new VoidArmorItem(ArmorItem.Type.HELMET, TCArmorMaterials.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_CHESTPLATE = REGISTRY.register("void_chestplate", () -> new VoidArmorItem(ArmorItem.Type.CHESTPLATE, TCArmorMaterials.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_LEGGINGS = REGISTRY.register("void_leggings", () -> new VoidArmorItem(ArmorItem.Type.LEGGINGS, TCArmorMaterials.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_BOOTS = REGISTRY.register("void_boots", () -> new VoidArmorItem(ArmorItem.Type.BOOTS, TCArmorMaterials.VOID));

    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_HELMET = REGISTRY.register("void_robe_helmet", () -> new VoidRobeArmorItem(TCArmorMaterials.VOID_ROBE, ArmorItem.Type.HELMET));
    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_CHESTPLATE = REGISTRY.register("void_robe_chestplate", () -> new VoidRobeArmorItem(TCArmorMaterials.VOID_ROBE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_LEGGINGS = REGISTRY.register("void_robe_leggings", () -> new VoidRobeArmorItem(TCArmorMaterials.VOID_ROBE, ArmorItem.Type.LEGGINGS));

    public static final DeferredItem<FortressArmorItem> FORTRESS_HELMET = REGISTRY.register("fortress_helmet", () -> new FortressArmorItem(ArmorItem.Type.HELMET, TCArmorMaterials.FORTRESS));
    public static final DeferredItem<FortressArmorItem> FORTRESS_CHESTPLATE = REGISTRY.register("fortress_chestplate", () -> new FortressArmorItem(ArmorItem.Type.CHESTPLATE, TCArmorMaterials.FORTRESS));
    public static final DeferredItem<FortressArmorItem> FORTRESS_LEGGINGS = REGISTRY.register("fortress_leggings", () -> new FortressArmorItem(ArmorItem.Type.LEGGINGS, TCArmorMaterials.FORTRESS));

    public static final DeferredItem<BootsTravellerItem> BOOTS_TRAVELLER = REGISTRY.register("boots_traveller", () -> new BootsTravellerItem(ArmorItem.Type.BOOTS, TCArmorMaterials.TRAVELLER));
    public static final DeferredItem<GogglesItem> GOGGLES = REGISTRY.register("goggles", GogglesItem::new);

    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_HELMET = REGISTRY.register("crimson_robe_helmet", () -> new CultistRobeArmorItem(TCArmorMaterials.CULTIST_CLOTH, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_CHESTPLATE = REGISTRY.register("crimson_robe_chestplate", () -> new CultistRobeArmorItem(TCArmorMaterials.CULTIST_CLOTH, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_LEGGINGS = REGISTRY.register("crimson_robe_leggings", () -> new CultistRobeArmorItem(TCArmorMaterials.CULTIST_CLOTH, ArmorItem.Type.LEGGINGS));

    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_HELMET = REGISTRY.register("crimson_plate_helmet", () -> new CultistPlateArmorItem(TCArmorMaterials.CULTIST_PLATE, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_CHESTPLATE = REGISTRY.register("crimson_plate_chestplate", () -> new CultistPlateArmorItem(TCArmorMaterials.CULTIST_PLATE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_LEGGINGS = REGISTRY.register("crimson_plate_leggings", () -> new CultistPlateArmorItem(TCArmorMaterials.CULTIST_PLATE, ArmorItem.Type.LEGGINGS));

    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_HELMET = REGISTRY.register("crimson_leader_helmet", () -> new CultistLeaderArmorItem(TCArmorMaterials.CULTIST_LEADER, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_CHESTPLATE = REGISTRY.register("crimson_leader_chestplate", () -> new CultistLeaderArmorItem(TCArmorMaterials.CULTIST_LEADER, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_LEGGINGS = REGISTRY.register("crimson_leader_leggings", () -> new CultistLeaderArmorItem(TCArmorMaterials.CULTIST_LEADER, ArmorItem.Type.LEGGINGS));

    public static final DeferredItem<CultistBootsItem> CRIMSON_BOOTS = REGISTRY.register("crimson_boots", () -> new CultistBootsItem(TCArmorMaterials.CULTIST_CLOTH, ArmorItem.Type.BOOTS));

    public static final DeferredItem<RobeArmorItem> ROBE_CHESTPLATE = REGISTRY.register("robe_chestplate", () -> new RobeArmorItem(ArmorItem.Type.CHESTPLATE, TCArmorMaterials.ROBE));
    public static final DeferredItem<RobeArmorItem> ROBE_LEGGINGS = REGISTRY.register("robe_leggings", () -> new RobeArmorItem(ArmorItem.Type.LEGGINGS, TCArmorMaterials.ROBE));
    public static final DeferredItem<RobeArmorItem> ROBE_BOOTS = REGISTRY.register("robe_boots", () -> new RobeArmorItem(ArmorItem.Type.BOOTS, TCArmorMaterials.ROBE));

    public static final List<DeferredItem<? extends Item>> SIMPLE_ITEMS = List.of(
            CINNABAR_ORE,
            INFUSED_AIR_ORE,
            INFUSED_FIRE_ORE,
            INFUSED_WATER_ORE,
            INFUSED_EARTH_ORE,
            INFUSED_ORDER_ORE,
            INFUSED_ENTROPY_ORE,
            AMBER_ORE,
            AMBER_BLOCK,
            AMBER_BRICKS,
            THAUMIUM_BLOCK,
            TALLOW_BLOCK,
            ARCANE_STONE,
            ARCANE_STONE_BRICKS,
            ANCIENT_STONE,
            ANCIENT_ROCK,
            CRUSTED_STONE,
            GREATWOOD_LOG,
            SILVERWOOD_LOG,
            ARCANE_WOOD,
            GREATWOOD_PLANKS,
            SILVERWOOD_PLANKS,
            GREATWOOD_LEAVES,
            SILVERWOOD_LEAVES,
            GREATWOOD_SAPLING,
            SILVERWOOD_SAPLING,
            SHIMMERLEAF,
            CINDERPEARL,
            VISHROOM,
            TABLE,
            RESEARCH_TABLE,
            DECONSTRUCTION_TABLE,
            ARCANE_WORKTABLE,
            ALCHEMICAL_FURNACE,
            ARCANE_PEDESTAL,
            WAND_RECHARGE_PEDESTAL,
            COMPOUND_RECHARGE_FOCUS,
            ARCANE_SPA,
            RUNIC_MATRIX,
            AURA_NODE,
            NODE_STABILIZER,
            ADVANCED_NODE_STABILIZER,
            NODE_TRANSDUCER,
            FOCAL_MANIPULATOR,
            FLUX_SCRUBBER,
            CRUCIBLE,
            ARCANE_ALEMBIC,
            VIS_CHARGE_RELAY,
            ADVANCED_ALCHEMICAL_CONSTRUCT,
            ITEM_GRATE,
            ARCANE_LAMP,
            LAMP_OF_GROWTH,
            ALCHEMICAL_CONSTRUCT,
            THAUMATORIUM,
            MNEMONIC_MATRIX,
            LAMP_OF_FERTILITY,
            VIS_RELAY,
            ESSENTIA_TUBE,
            ESSENTIA_VALVE,
            ALCHEMICAL_CENTRIFUGE,
            FILTERED_ESSENTIA_TUBE,
            ESSENTIA_BUFFER,
            RESTRICTED_ESSENTIA_TUBE,
            DIRECTIONAL_ESSENTIA_TUBE,
            ESSENTIA_CRYSTALLIZER,
            WARDED_JAR,
            BRAIN_IN_A_JAR,
            NODE_IN_A_JAR,
            VOID_JAR,
            MAGIC_MIRROR,
            ESSENTIA_MIRROR,
            HAND_MIRROR,
            ARCANE_STONE_STAIRS,
            GREATWOOD_STAIRS,
            SILVERWOOD_STAIRS,
            ARCANE_STONE_SLAB,
            GREATWOOD_SLAB,
            SILVERWOOD_SLAB,
            WHITE_TALLOW_CANDLE,
            ORANGE_TALLOW_CANDLE,
            MAGENTA_TALLOW_CANDLE,
            LIGHT_BLUE_TALLOW_CANDLE,
            YELLOW_TALLOW_CANDLE,
            LIME_TALLOW_CANDLE,
            PINK_TALLOW_CANDLE,
            GRAY_TALLOW_CANDLE,
            LIGHT_GRAY_TALLOW_CANDLE,
            CYAN_TALLOW_CANDLE,
            PURPLE_TALLOW_CANDLE,
            BLUE_TALLOW_CANDLE,
            BROWN_TALLOW_CANDLE,
            GREEN_TALLOW_CANDLE,
            RED_TALLOW_CANDLE,
            BLACK_TALLOW_CANDLE,
            IRON_WAND_CAP,
            COPPER_WAND_CAP,
            GOLD_WAND_CAP,
            SILVER_WAND_CAP_INERT,
            SILVER_WAND_CAP,
            THAUMIUM_WAND_CAP_INERT,
            THAUMIUM_WAND_CAP,
            VOID_WAND_CAP_INERT,
            VOID_WAND_CAP,
            GREATWOOD_WAND_ROD,
            OBSIDIAN_WAND_ROD,
            SILVERWOOD_WAND_ROD,
            ICE_WAND_ROD,
            QUARTZ_WAND_ROD,
            REED_WAND_ROD,
            BLAZE_WAND_ROD,
            BONE_WAND_ROD,
            GREATWOOD_STAFF_CORE,
            OBSIDIAN_STAFF_CORE,
            SILVERWOOD_STAFF_CORE,
            ICE_STAFF_CORE,
            QUARTZ_STAFF_CORE,
            REED_STAFF_CORE,
            BLAZE_STAFF_CORE,
            BONE_STAFF_CORE,
            PRIMAL_STAFF_CORE,
            WAND_CASTING,
            THAUMONOMICON,
            THAUMOMETER,
            ALUMENTUM,
            NITOR,
            THAUMIUM_INGOT,
            QUICKSILVER,
            MAGIC_TALLOW,
            AMBER,
            ENCHANTED_FABRIC,
            VIS_FILTER,
            KNOWLEDGE_FRAGMENT,
            MIRRORED_GLASS,
            TAINTED_GOO,
            TAINT_TENDRIL,
            JAR_LABEL,
            SALIS_MUNDUS,
            PRIMAL_CHARM,
            VOID_INGOT,
            VOID_SEED,
            GOLD_COIN,
            ZOMBIE_BRAIN,
            AIR_SHARD,
            FIRE_SHARD,
            WATER_SHARD,
            EARTH_SHARD,
            ORDER_SHARD,
            ENTROPY_SHARD,
            BALANCED_SHARD,
            IRON_NUGGET,
            COPPER_NUGGET,
            TIN_NUGGET,
            SILVER_NUGGET,
            LEAD_NUGGET,
            QUICKSILVER_DROP,
            THAUMIUM_NUGGET,
            VOID_NUGGET,
            NATIVE_IRON_CLUSTER,
            NATIVE_COPPER_CLUSTER,
            NATIVE_TIN_CLUSTER,
            NATIVE_SILVER_CLUSTER,
            NATIVE_LEAD_CLUSTER,
            NATIVE_CINNABAR_CLUSTER,
            NATIVE_GOLD_CLUSTER,
            CHICKEN_NUGGET,
            BEEF_NUGGET,
            PORK_NUGGET,
            FISH_NUGGET,
            TRIPLE_MEAT_TREAT,
            TAINT_SLIME,
            BATH_SALTS,
            PRIMAL_CHARM,
            CRIMSON_RITES,
            PRIMORDIAL_PEARL,
            IRON_ARCANE_KEY,
            GOLD_ARCANE_KEY,
            SCRIBING_TOOLS,
            RESEARCH_NOTES,
            ETHEREAL_ESSENCE,
            CRYSTALLIZED_ESSENCE,
            GLASS_PHIAL,
            ESSENTIA_PHIAL,
            MANA_BEAN,
            THAUMIUM_SWORD, THAUMIUM_SHOVEL, THAUMIUM_PICKAXE, THAUMIUM_AXE, THAUMIUM_HOE,
            VOID_SWORD, VOID_SHOVEL, VOID_PICKAXE, VOID_AXE, VOID_HOE,
            ELEMENTAL_SHOVEL, ELEMENTAL_PICKAXE, ELEMENTAL_AXE, ELEMENTAL_HOE, ELEMENTAL_SWORD,
            CRIMSON_BLADE, PRIMAL_CRUSHER, SANITY_CHECKER, SINISTER_LODESTONE,
            THAUMIUM_HELMET, THAUMIUM_CHESTPLATE, THAUMIUM_LEGGINGS, THAUMIUM_BOOTS,
            VOID_HELMET, VOID_CHESTPLATE, VOID_LEGGINGS, VOID_BOOTS,
            VOID_ROBE_HELMET, VOID_ROBE_CHESTPLATE, VOID_ROBE_LEGGINGS,
            FORTRESS_HELMET, FORTRESS_CHESTPLATE, FORTRESS_LEGGINGS,
            BOOTS_TRAVELLER, GOGGLES,
            CRIMSON_ROBE_HELMET, CRIMSON_ROBE_CHESTPLATE, CRIMSON_ROBE_LEGGINGS,
            CRIMSON_PLATE_HELMET, CRIMSON_PLATE_CHESTPLATE, CRIMSON_PLATE_LEGGINGS,
            CRIMSON_LEADER_HELMET, CRIMSON_LEADER_CHESTPLATE, CRIMSON_LEADER_LEGGINGS,
            CRIMSON_BOOTS,
            ROBE_CHESTPLATE, ROBE_LEGGINGS, ROBE_BOOTS);

    public static final List<DeferredItem<? extends Item>> FOCUS_ITEMS = List.of(
            FOCUS_FIRE,
            FOCUS_FROST,
            FOCUS_SHOCK,
            FOCUS_EXCAVATION,
            FOCUS_PORTABLE_HOLE,
            FOCUS_WARDING,
            FOCUS_PRIMAL,
            FOCUS_PECH,
            FOCUS_HELLBAT,
            FOCUS_TRADE);

    private static DeferredItem<Item> simple(String name) {
        return REGISTRY.registerSimpleItem(name, new Item.Properties());
    }

    private static DeferredItem<Item> simple(String name, Item.Properties properties) {
        return REGISTRY.registerSimpleItem(name, properties);
    }

    private static DeferredItem<WandPartItem> wandCap(String name, String tag) {
        return REGISTRY.registerItem(name, properties -> new WandPartItem(properties, WandPartItem.Kind.CAP, tag));
    }

    private static DeferredItem<WandPartItem> wandRod(String name, String tag) {
        return REGISTRY.registerItem(name, properties -> new WandPartItem(properties, WandPartItem.Kind.ROD, tag));
    }

    private static DeferredItem<ItemFocusBasic> focus(String name, PrimalVisStorage visCost, int color,
            int activationCooldown, boolean costPerTick) {
        return REGISTRY.registerItem(name,
                properties -> new ItemFocusBasic(visCost, color, activationCooldown, costPerTick, properties));
    }

    private static DeferredItem<ItemFocusBasic> focus(String name, PrimalVisStorage visCost, int color,
            int activationCooldown, boolean costPerTick, String depthLayer, String ornament,
            ItemFocusBasic.WandFocusAnimation animation) {
        return REGISTRY.registerItem(name, properties -> new ItemFocusBasic(visCost, color, activationCooldown,
                costPerTick, itemTexture(depthLayer), itemTexture(ornament), animation, properties));
    }

    private static ResourceLocation itemTexture(String name) {
        return name == null ? null : Thaumcraft.id("textures/item/" + name + ".png");
    }

    private static Item.Properties food(int nutrition, float saturationModifier) {
        return food(nutrition, saturationModifier, false);
    }

    private static Item.Properties food(int nutrition, float saturationModifier, boolean alwaysEdible) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier);
        if (alwaysEdible) {
            builder.alwaysEdible();
        }
        return new Item.Properties().food(builder.build());
    }

    private TCItems() {
    }
}

