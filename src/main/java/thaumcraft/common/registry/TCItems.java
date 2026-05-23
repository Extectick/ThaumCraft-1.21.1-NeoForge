package thaumcraft.common.registry;

import java.util.List;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.Thaumcraft;
import thaumcraft.common.curios.TCSlots;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.curios.HoverGirdleItem;
import thaumcraft.common.items.curios.RunicCurioItem;
import thaumcraft.common.items.curios.ThaumcraftCurioItem;
import thaumcraft.common.items.curios.VisAmuletItem;
import thaumcraft.common.items.curios.VisDiscountCurioItem;
import thaumcraft.common.items.wands.WandCastingItem;

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
    public static final DeferredItem<BlockItem> INFUSION_PILLAR = REGISTRY.registerSimpleBlockItem(TCBlocks.INFUSION_PILLAR);
    public static final DeferredItem<BlockItem> NODE_STABILIZER = REGISTRY.registerSimpleBlockItem(TCBlocks.NODE_STABILIZER);
    public static final DeferredItem<BlockItem> ADVANCED_NODE_STABILIZER = REGISTRY.registerSimpleBlockItem(TCBlocks.ADVANCED_NODE_STABILIZER);
    public static final DeferredItem<BlockItem> NODE_TRANSDUCER = REGISTRY.registerSimpleBlockItem(TCBlocks.NODE_TRANSDUCER);
    public static final DeferredItem<BlockItem> FOCAL_MANIPULATOR = REGISTRY.registerSimpleBlockItem(TCBlocks.FOCAL_MANIPULATOR);
    public static final DeferredItem<BlockItem> FLUX_SCRUBBER = REGISTRY.registerSimpleBlockItem(TCBlocks.FLUX_SCRUBBER);
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
    public static final DeferredItem<BlockItem> WARDED_JAR = REGISTRY.registerSimpleBlockItem(TCBlocks.WARDED_JAR);
    public static final DeferredItem<BlockItem> BRAIN_IN_A_JAR = REGISTRY.registerSimpleBlockItem(TCBlocks.BRAIN_IN_A_JAR);
    public static final DeferredItem<BlockItem> NODE_IN_A_JAR = REGISTRY.registerSimpleBlockItem(TCBlocks.NODE_IN_A_JAR);
    public static final DeferredItem<BlockItem> VOID_JAR = REGISTRY.registerSimpleBlockItem(TCBlocks.VOID_JAR);
    public static final DeferredItem<BlockItem> MAGIC_MIRROR = REGISTRY.registerSimpleBlockItem(TCBlocks.MAGIC_MIRROR);
    public static final DeferredItem<BlockItem> ESSENTIA_MIRROR = REGISTRY.registerSimpleBlockItem(TCBlocks.ESSENTIA_MIRROR);
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
    public static final DeferredItem<Item> IRON_WAND_CAP = simple("iron_wand_cap");
    public static final DeferredItem<Item> WAND_CASTING = REGISTRY.registerItem("wand_casting", WandCastingItem::new);
    public static final DeferredItem<Item> THAUMONOMICON = REGISTRY.registerSimpleItem("thaumonomicon", new Item.Properties());
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
    public static final DeferredItem<Item> PRIMAL_CHARM = simple("primal_charm");
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
    public static final DeferredItem<Item> IRON_ARCANE_KEY = simple("iron_arcane_key");
    public static final DeferredItem<Item> GOLD_ARCANE_KEY = simple("gold_arcane_key");
    public static final DeferredItem<Item> SCRIBING_TOOLS = simple("scribing_tools");
    public static final DeferredItem<Item> ETHEREAL_ESSENCE = simple("ethereal_essence");
    public static final DeferredItem<Item> CRYSTALLIZED_ESSENCE = simple("crystallized_essence");
    public static final DeferredItem<Item> GLASS_PHIAL = simple("glass_phial");
    public static final DeferredItem<Item> ESSENTIA_PHIAL = simple("essentia_phial");
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
            INFUSION_PILLAR,
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
            WAND_CASTING,
            THAUMONOMICON,
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
            IRON_ARCANE_KEY,
            GOLD_ARCANE_KEY,
            SCRIBING_TOOLS,
            ETHEREAL_ESSENCE,
            CRYSTALLIZED_ESSENCE,
            GLASS_PHIAL,
            ESSENTIA_PHIAL,
            MANA_BEAN);

    private static DeferredItem<Item> simple(String name) {
        return REGISTRY.registerSimpleItem(name, new Item.Properties());
    }

    private static DeferredItem<Item> simple(String name, Item.Properties properties) {
        return REGISTRY.registerSimpleItem(name, properties);
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
