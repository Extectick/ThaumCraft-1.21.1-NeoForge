package thaumcraft.common.registry;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.SimpleJarBlock;
import thaumcraft.common.blocks.SimpleMirrorBlock;
import thaumcraft.common.blocks.SimplePlantBlock;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.common.blocks.SimpleTubeBlock;

public final class TCBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final DeferredBlock<Block> CINNABAR_ORE = ore("cinnabar_ore");
    public static final DeferredBlock<Block> INFUSED_AIR_ORE = ore("infused_air_ore");
    public static final DeferredBlock<Block> INFUSED_FIRE_ORE = ore("infused_fire_ore");
    public static final DeferredBlock<Block> INFUSED_WATER_ORE = ore("infused_water_ore");
    public static final DeferredBlock<Block> INFUSED_EARTH_ORE = ore("infused_earth_ore");
    public static final DeferredBlock<Block> INFUSED_ORDER_ORE = ore("infused_order_ore");
    public static final DeferredBlock<Block> INFUSED_ENTROPY_ORE = ore("infused_entropy_ore");
    public static final DeferredBlock<Block> AMBER_ORE = ore("amber_ore");
    public static final DeferredBlock<Block> AMBER_BLOCK = REGISTRY.registerSimpleBlock("amber_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(1.5F, 5.0F)
                    .noOcclusion());
    public static final DeferredBlock<Block> AMBER_BRICKS = REGISTRY.registerSimpleBlock("amber_bricks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(1.5F, 5.0F)
                    .noOcclusion());
    public static final DeferredBlock<Block> THAUMIUM_BLOCK = metal("thaumium_block");
    public static final DeferredBlock<Block> TALLOW_BLOCK = REGISTRY.registerSimpleBlock("tallow_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)
                    .strength(0.6F, 1.0F));
    public static final DeferredBlock<Block> ARCANE_STONE = stone("arcane_stone");
    public static final DeferredBlock<Block> ARCANE_STONE_BRICKS = stone("arcane_stone_bricks");
    public static final DeferredBlock<Block> ANCIENT_STONE = stone("ancient_stone");
    public static final DeferredBlock<Block> ANCIENT_ROCK = stone("ancient_rock");
    public static final DeferredBlock<Block> CRUSTED_STONE = stone("crusted_stone");
    public static final DeferredBlock<RotatedPillarBlock> GREATWOOD_LOG = log("greatwood_log");
    public static final DeferredBlock<RotatedPillarBlock> SILVERWOOD_LOG = log("silverwood_log");
    public static final DeferredBlock<Block> ARCANE_WOOD = planks("arcane_wood");
    public static final DeferredBlock<Block> GREATWOOD_PLANKS = planks("greatwood_planks");
    public static final DeferredBlock<Block> SILVERWOOD_PLANKS = planks("silverwood_planks");
    public static final DeferredBlock<LeavesBlock> GREATWOOD_LEAVES = leaves("greatwood_leaves", 0);
    public static final DeferredBlock<LeavesBlock> SILVERWOOD_LEAVES = leaves("silverwood_leaves", 7);
    public static final DeferredBlock<SimplePlantBlock> GREATWOOD_SAPLING = plant("greatwood_sapling", 0);
    public static final DeferredBlock<SimplePlantBlock> SILVERWOOD_SAPLING = plant("silverwood_sapling", 8);
    public static final DeferredBlock<SimplePlantBlock> SHIMMERLEAF = plant("shimmerleaf", 8);
    public static final DeferredBlock<SimplePlantBlock> CINDERPEARL = plant("cinderpearl", 8);
    public static final DeferredBlock<SimplePlantBlock> VISHROOM = plant("vishroom", 8);
    public static final DeferredBlock<SimpleTableBlock> TABLE = table("table");
    public static final DeferredBlock<SimpleTableBlock> RESEARCH_TABLE = table("research_table");
    public static final DeferredBlock<SimpleTableBlock> DECONSTRUCTION_TABLE = table("deconstruction_table");
    public static final DeferredBlock<SimpleTableBlock> ARCANE_WORKTABLE = table("arcane_worktable");
    public static final DeferredBlock<Block> ALCHEMICAL_FURNACE = stoneDevice("alchemical_furnace");
    public static final DeferredBlock<Block> ARCANE_PEDESTAL = stoneDevice("arcane_pedestal");
    public static final DeferredBlock<Block> WAND_RECHARGE_PEDESTAL = stoneDevice("wand_recharge_pedestal");
    public static final DeferredBlock<Block> COMPOUND_RECHARGE_FOCUS = stoneDevice("compound_recharge_focus");
    public static final DeferredBlock<Block> ARCANE_SPA = stoneDevice("arcane_spa");
    public static final DeferredBlock<Block> RUNIC_MATRIX = stoneDevice("runic_matrix");
    public static final DeferredBlock<Block> INFUSION_PILLAR = stoneDevice("infusion_pillar");
    public static final DeferredBlock<Block> NODE_STABILIZER = stoneDevice("node_stabilizer");
    public static final DeferredBlock<Block> ADVANCED_NODE_STABILIZER = stoneDevice("advanced_node_stabilizer");
    public static final DeferredBlock<Block> NODE_TRANSDUCER = stoneDevice("node_transducer");
    public static final DeferredBlock<Block> FOCAL_MANIPULATOR = stoneDevice("focal_manipulator");
    public static final DeferredBlock<Block> FLUX_SCRUBBER = stoneDevice("flux_scrubber");
    public static final DeferredBlock<Block> CRUCIBLE = metalDevice("crucible");
    public static final DeferredBlock<Block> ARCANE_ALEMBIC = metalDevice("arcane_alembic");
    public static final DeferredBlock<Block> VIS_CHARGE_RELAY = metalDevice("vis_charge_relay");
    public static final DeferredBlock<Block> ADVANCED_ALCHEMICAL_CONSTRUCT = metalDevice("advanced_alchemical_construct");
    public static final DeferredBlock<Block> ITEM_GRATE = metalDevice("item_grate");
    public static final DeferredBlock<Block> ARCANE_LAMP = metalDevice("arcane_lamp");
    public static final DeferredBlock<Block> LAMP_OF_GROWTH = metalDevice("lamp_of_growth");
    public static final DeferredBlock<Block> ALCHEMICAL_CONSTRUCT = metalDevice("alchemical_construct");
    public static final DeferredBlock<Block> THAUMATORIUM = metalDevice("thaumatorium");
    public static final DeferredBlock<Block> MNEMONIC_MATRIX = metalDevice("mnemonic_matrix");
    public static final DeferredBlock<Block> LAMP_OF_FERTILITY = metalDevice("lamp_of_fertility");
    public static final DeferredBlock<Block> VIS_RELAY = metalDevice("vis_relay");
    public static final DeferredBlock<SimpleTubeBlock> ESSENTIA_TUBE = tube("essentia_tube");
    public static final DeferredBlock<SimpleTubeBlock> ESSENTIA_VALVE = tube("essentia_valve");
    public static final DeferredBlock<SimpleTubeBlock> ALCHEMICAL_CENTRIFUGE = tube("alchemical_centrifuge");
    public static final DeferredBlock<SimpleTubeBlock> FILTERED_ESSENTIA_TUBE = tube("filtered_essentia_tube");
    public static final DeferredBlock<SimpleTubeBlock> ESSENTIA_BUFFER = tube("essentia_buffer");
    public static final DeferredBlock<SimpleTubeBlock> RESTRICTED_ESSENTIA_TUBE = tube("restricted_essentia_tube");
    public static final DeferredBlock<SimpleTubeBlock> DIRECTIONAL_ESSENTIA_TUBE = tube("directional_essentia_tube");
    public static final DeferredBlock<SimpleTubeBlock> ESSENTIA_CRYSTALLIZER = tube("essentia_crystallizer");
    public static final DeferredBlock<SimpleJarBlock> WARDED_JAR = jar("warded_jar");
    public static final DeferredBlock<SimpleJarBlock> BRAIN_IN_A_JAR = jar("brain_in_a_jar");
    public static final DeferredBlock<SimpleJarBlock> NODE_IN_A_JAR = jar("node_in_a_jar");
    public static final DeferredBlock<SimpleJarBlock> VOID_JAR = jar("void_jar");
    public static final DeferredBlock<SimpleMirrorBlock> MAGIC_MIRROR = mirror("magic_mirror");
    public static final DeferredBlock<SimpleMirrorBlock> ESSENTIA_MIRROR = mirror("essentia_mirror");
    public static final DeferredBlock<StairBlock> ARCANE_STONE_STAIRS = stairs("arcane_stone_stairs", ARCANE_STONE);
    public static final DeferredBlock<StairBlock> GREATWOOD_STAIRS = stairs("greatwood_stairs", GREATWOOD_PLANKS);
    public static final DeferredBlock<StairBlock> SILVERWOOD_STAIRS = stairs("silverwood_stairs", SILVERWOOD_PLANKS);
    public static final DeferredBlock<SlabBlock> ARCANE_STONE_SLAB = slab("arcane_stone_slab", ARCANE_STONE);
    public static final DeferredBlock<SlabBlock> GREATWOOD_SLAB = slab("greatwood_slab", GREATWOOD_PLANKS);
    public static final DeferredBlock<SlabBlock> SILVERWOOD_SLAB = slab("silverwood_slab", SILVERWOOD_PLANKS);
    public static final DeferredBlock<CandleBlock> WHITE_TALLOW_CANDLE = candle("white_tallow_candle");
    public static final DeferredBlock<CandleBlock> ORANGE_TALLOW_CANDLE = candle("orange_tallow_candle");
    public static final DeferredBlock<CandleBlock> MAGENTA_TALLOW_CANDLE = candle("magenta_tallow_candle");
    public static final DeferredBlock<CandleBlock> LIGHT_BLUE_TALLOW_CANDLE = candle("light_blue_tallow_candle");
    public static final DeferredBlock<CandleBlock> YELLOW_TALLOW_CANDLE = candle("yellow_tallow_candle");
    public static final DeferredBlock<CandleBlock> LIME_TALLOW_CANDLE = candle("lime_tallow_candle");
    public static final DeferredBlock<CandleBlock> PINK_TALLOW_CANDLE = candle("pink_tallow_candle");
    public static final DeferredBlock<CandleBlock> GRAY_TALLOW_CANDLE = candle("gray_tallow_candle");
    public static final DeferredBlock<CandleBlock> LIGHT_GRAY_TALLOW_CANDLE = candle("light_gray_tallow_candle");
    public static final DeferredBlock<CandleBlock> CYAN_TALLOW_CANDLE = candle("cyan_tallow_candle");
    public static final DeferredBlock<CandleBlock> PURPLE_TALLOW_CANDLE = candle("purple_tallow_candle");
    public static final DeferredBlock<CandleBlock> BLUE_TALLOW_CANDLE = candle("blue_tallow_candle");
    public static final DeferredBlock<CandleBlock> BROWN_TALLOW_CANDLE = candle("brown_tallow_candle");
    public static final DeferredBlock<CandleBlock> GREEN_TALLOW_CANDLE = candle("green_tallow_candle");
    public static final DeferredBlock<CandleBlock> RED_TALLOW_CANDLE = candle("red_tallow_candle");
    public static final DeferredBlock<CandleBlock> BLACK_TALLOW_CANDLE = candle("black_tallow_candle");

    public static final List<DeferredBlock<? extends Block>> SIMPLE_BLOCKS = List.of(
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
            BLACK_TALLOW_CANDLE);

    private static DeferredBlock<Block> ore(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(1.5F, 5.0F)
                .requiresCorrectToolForDrops());
    }

    private static DeferredBlock<Block> stone(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(2.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }

    private static DeferredBlock<Block> metal(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(4.0F, 20.0F)
                .requiresCorrectToolForDrops());
    }

    private static DeferredBlock<RotatedPillarBlock> log(String name) {
        return REGISTRY.register(name, () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                .strength(2.5F)));
    }

    private static DeferredBlock<Block> planks(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.0F, 3.0F));
    }

    private static DeferredBlock<LeavesBlock> leaves(String name, int lightLevel) {
        return REGISTRY.register(name, () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                .lightLevel(state -> lightLevel)));
    }

    private static DeferredBlock<SimplePlantBlock> plant(String name, int lightLevel) {
        return REGISTRY.register(name, () -> new SimplePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)
                .lightLevel(state -> lightLevel)));
    }

    private static DeferredBlock<SimpleTableBlock> table(String name) {
        return REGISTRY.register(name, () -> new SimpleTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.5F, 3.0F)
                .noOcclusion()));
    }

    private static DeferredBlock<Block> stoneDevice(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(3.0F, 25.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    private static DeferredBlock<Block> metalDevice(String name) {
        return REGISTRY.registerSimpleBlock(name, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(3.0F, 17.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    private static DeferredBlock<SimpleTubeBlock> tube(String name) {
        return REGISTRY.register(name, () -> new SimpleTubeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.6F, 1.0F)
                .noOcclusion()));
    }

    private static DeferredBlock<SimpleJarBlock> jar(String name) {
        return REGISTRY.register(name, () -> new SimpleJarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 1.0F)
                .noOcclusion()));
    }

    private static DeferredBlock<SimpleMirrorBlock> mirror(String name) {
        return REGISTRY.register(name, () -> new SimpleMirrorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(1.0F, 5.0F)
                .noOcclusion()));
    }

    private static DeferredBlock<CandleBlock> candle(String name) {
        return REGISTRY.register(name, () -> new CandleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE)
                .lightLevel(CandleBlock.LIGHT_EMISSION)));
    }

    private static DeferredBlock<StairBlock> stairs(String name, DeferredBlock<? extends Block> base) {
        return REGISTRY.register(name, () -> new StairBlock(base.get().defaultBlockState(),
                BlockBehaviour.Properties.ofFullCopy(base.get())));
    }

    private static DeferredBlock<SlabBlock> slab(String name, DeferredBlock<? extends Block> base) {
        return REGISTRY.register(name, () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())));
    }

    private TCBlocks() {
    }
}
