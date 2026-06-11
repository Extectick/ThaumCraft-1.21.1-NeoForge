package thaumcraft.common.registry;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.AlchemicalFurnaceBlock;
import thaumcraft.common.blocks.ArcaneAlembicBlock;
import thaumcraft.common.blocks.ArcaneLampBlock;
import thaumcraft.common.blocks.ArcanePedestalBlock;
import thaumcraft.common.blocks.ArcaneWorktableBlock;
import thaumcraft.common.blocks.AuraNodeBlock;
import thaumcraft.common.blocks.BrainInAJarBlock;
import thaumcraft.common.blocks.CrucibleBlock;
import thaumcraft.common.blocks.EssentiaTubeBlock;
import thaumcraft.common.blocks.EssentiaTubeBlock.TubeMode;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.blocks.InfusionPillarBlock;
import thaumcraft.common.blocks.MagicSaplingBlock;
import thaumcraft.common.blocks.MagicSaplingBlock.TreeKind;
import thaumcraft.common.blocks.NodeJarBlock;
import thaumcraft.common.blocks.NodeStabilizerBlock;
import thaumcraft.common.blocks.ResearchTableBlock;
import thaumcraft.common.blocks.RunicMatrixBlock;
import thaumcraft.common.blocks.SimpleDirectionalBlock;
import thaumcraft.common.blocks.SimpleJarBlock;
import thaumcraft.common.blocks.SimpleMirrorBlock;
import thaumcraft.common.blocks.SimplePlantBlock;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.common.blocks.SimpleTubeBlock;
import thaumcraft.common.blocks.SilverwoodKnotBlock;
import thaumcraft.common.blocks.ThaumcraftOreBlock;
import thaumcraft.common.blocks.ThaumcraftOreBlock.OreType;
import thaumcraft.common.blocks.WardedJarBlock;
import thaumcraft.common.blocks.HungryChestBlock;
import thaumcraft.common.blocks.PavingStoneOfTravelBlock;
import thaumcraft.common.blocks.PavingStoneOfWardingBlock;
import thaumcraft.common.blocks.WardingBarrierBlock;

public final class TCBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Thaumcraft.MODID);
    private static final SoundType JAR_SOUND = new DeferredSoundType(1.0F, 1.0F,
            TCSoundEvents.JAR, TCSoundEvents.JAR, TCSoundEvents.JAR, TCSoundEvents.JAR, TCSoundEvents.JAR);

    public static final DeferredBlock<ThaumcraftOreBlock> CINNABAR_ORE = ore("cinnabar_ore", OreType.CINNABAR);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_AIR_ORE = glowingOre("infused_air_ore", OreType.AIR);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_FIRE_ORE = glowingOre("infused_fire_ore", OreType.FIRE);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_WATER_ORE = glowingOre("infused_water_ore", OreType.WATER);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_EARTH_ORE = glowingOre("infused_earth_ore", OreType.EARTH);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_ORDER_ORE = glowingOre("infused_order_ore", OreType.ORDER);
    public static final DeferredBlock<ThaumcraftOreBlock> INFUSED_ENTROPY_ORE = glowingOre("infused_entropy_ore", OreType.ENTROPY);
    public static final DeferredBlock<ThaumcraftOreBlock> AMBER_ORE = ore("amber_ore", OreType.AMBER);
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
    public static final DeferredBlock<Block> OBSIDIAN_TILE = REGISTRY.registerSimpleBlock("obsidian_tile",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN));
    public static final DeferredBlock<PavingStoneOfTravelBlock> PAVING_STONE_TRAVEL = REGISTRY.register("paving_stone_travel",
            () -> new PavingStoneOfTravelBlock(stoneDeviceProperties()));
    public static final DeferredBlock<PavingStoneOfWardingBlock> PAVING_STONE_WARDING = REGISTRY.register("paving_stone_warding",
            () -> new PavingStoneOfWardingBlock(stoneDeviceProperties()));
    public static final DeferredBlock<WardingBarrierBlock> WARDING_BARRIER = REGISTRY.register("warding_barrier",
            () -> new WardingBarrierBlock(BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<HungryChestBlock> HUNGRY_CHEST = REGISTRY.register("hungry_chest",
            () -> new HungryChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)));
    public static final DeferredBlock<RotatedPillarBlock> GREATWOOD_LOG = log("greatwood_log");
    public static final DeferredBlock<RotatedPillarBlock> SILVERWOOD_LOG = log("silverwood_log");
    public static final DeferredBlock<SilverwoodKnotBlock> SILVERWOOD_KNOT = REGISTRY.register("silverwood_knot",
            () -> new SilverwoodKnotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                    .strength(2.5F)
                    .lightLevel(state -> 7)));
    public static final DeferredBlock<Block> ARCANE_WOOD = planks("arcane_wood");
    public static final DeferredBlock<Block> GREATWOOD_PLANKS = planks("greatwood_planks");
    public static final DeferredBlock<Block> SILVERWOOD_PLANKS = planks("silverwood_planks");
    public static final DeferredBlock<LeavesBlock> GREATWOOD_LEAVES = leaves("greatwood_leaves", 0);
    public static final DeferredBlock<LeavesBlock> SILVERWOOD_LEAVES = leaves("silverwood_leaves", 7);
    public static final DeferredBlock<MagicSaplingBlock> GREATWOOD_SAPLING = magicSapling("greatwood_sapling", 0, TreeKind.GREATWOOD);
    public static final DeferredBlock<MagicSaplingBlock> SILVERWOOD_SAPLING = magicSapling("silverwood_sapling", 8, TreeKind.SILVERWOOD);
    public static final DeferredBlock<SimplePlantBlock> SHIMMERLEAF = plant("shimmerleaf", 8);
    public static final DeferredBlock<SimplePlantBlock> CINDERPEARL = plant("cinderpearl", 8);
    public static final DeferredBlock<SimplePlantBlock> VISHROOM = plant("vishroom", 8);
    public static final DeferredBlock<SimpleTableBlock> TABLE = table("table");
    public static final DeferredBlock<ResearchTableBlock> RESEARCH_TABLE = REGISTRY.register("research_table",
            () -> new ResearchTableBlock(tableProperties()));
    public static final DeferredBlock<SimpleTableBlock> DECONSTRUCTION_TABLE = table("deconstruction_table");
    public static final DeferredBlock<ArcaneWorktableBlock> ARCANE_WORKTABLE = REGISTRY.register("arcane_worktable",
            () -> new ArcaneWorktableBlock(tableProperties()));
    public static final DeferredBlock<AlchemicalFurnaceBlock> ALCHEMICAL_FURNACE = REGISTRY.register("alchemical_furnace",
            () -> new AlchemicalFurnaceBlock(stoneDeviceProperties()
                    .lightLevel(state -> state.getValue(AlchemicalFurnaceBlock.LIT) ? 12 : 0)));
    public static final DeferredBlock<ArcanePedestalBlock> ARCANE_PEDESTAL = REGISTRY.register("arcane_pedestal",
            () -> new ArcanePedestalBlock(stoneDeviceProperties()));
    public static final DeferredBlock<Block> WAND_RECHARGE_PEDESTAL = stoneDevice("wand_recharge_pedestal");
    public static final DeferredBlock<Block> COMPOUND_RECHARGE_FOCUS = stoneDevice("compound_recharge_focus");
    public static final DeferredBlock<Block> ARCANE_SPA = stoneDevice("arcane_spa");
    public static final DeferredBlock<RunicMatrixBlock> RUNIC_MATRIX = REGISTRY.register("runic_matrix",
            () -> new RunicMatrixBlock(stoneDeviceProperties().lightLevel(state -> 10)));
    public static final DeferredBlock<AuraNodeBlock> AURA_NODE = REGISTRY.register("aura_node",
            () -> new AuraNodeBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 200.0F)
                    .lightLevel(state -> 8)
                    .sound(SoundType.EMPTY)
                    .noOcclusion()
                    .noCollission()));
    public static final DeferredBlock<InfusionPillarBlock> INFUSION_PILLAR = REGISTRY.register("infusion_pillar",
            () -> new InfusionPillarBlock(stoneDeviceProperties()));
    public static final DeferredBlock<NodeStabilizerBlock> NODE_STABILIZER = REGISTRY.register("node_stabilizer",
            () -> new NodeStabilizerBlock(stoneDeviceProperties(), false));
    public static final DeferredBlock<NodeStabilizerBlock> ADVANCED_NODE_STABILIZER =
            REGISTRY.register("advanced_node_stabilizer",
                    () -> new NodeStabilizerBlock(stoneDeviceProperties(), true));
    public static final DeferredBlock<Block> NODE_TRANSDUCER = stoneDevice("node_transducer");
    public static final DeferredBlock<Block> FOCAL_MANIPULATOR = stoneDevice("focal_manipulator");
    public static final DeferredBlock<Block> FLUX_SCRUBBER = stoneDevice("flux_scrubber");
    public static final DeferredBlock<FluxBlock> FLUX_GOO = REGISTRY.register("flux_goo",
            () -> new FluxBlock(fluxProperties(), false));
    public static final DeferredBlock<FluxBlock> FLUX_GAS = REGISTRY.register("flux_gas",
            () -> new FluxBlock(fluxProperties(), true));
    public static final DeferredBlock<CrucibleBlock> CRUCIBLE = REGISTRY.register("crucible",
            () -> new CrucibleBlock(metalDeviceProperties()));
    public static final DeferredBlock<ArcaneAlembicBlock> ARCANE_ALEMBIC = REGISTRY.register("arcane_alembic",
            () -> new ArcaneAlembicBlock(metalDeviceProperties()));
    public static final DeferredBlock<Block> VIS_CHARGE_RELAY = metalDevice("vis_charge_relay");
    public static final DeferredBlock<Block> ADVANCED_ALCHEMICAL_CONSTRUCT = metalDevice("advanced_alchemical_construct");
    public static final DeferredBlock<Block> ITEM_GRATE = metalDevice("item_grate");
    public static final DeferredBlock<ArcaneLampBlock> ARCANE_LAMP = REGISTRY.register("arcane_lamp",
            () -> new ArcaneLampBlock(metalDeviceProperties()));
    public static final DeferredBlock<ArcaneLampBlock> LAMP_OF_GROWTH = REGISTRY.register("arcane_lamp_growth",
            () -> new ArcaneLampBlock(metalDeviceProperties()));
    public static final DeferredBlock<Block> ALCHEMICAL_CONSTRUCT = metalDevice("alchemical_construct");
    public static final DeferredBlock<Block> THAUMATORIUM = metalDevice("thaumatorium");
    public static final DeferredBlock<Block> MNEMONIC_MATRIX = REGISTRY.register("mnemonic_matrix", () -> new SimpleDirectionalBlock(metalDeviceProperties()));
    public static final DeferredBlock<ArcaneLampBlock> LAMP_OF_FERTILITY = REGISTRY.register("arcane_lamp_fertility",
            () -> new ArcaneLampBlock(metalDeviceProperties()));
    public static final DeferredBlock<Block> VIS_RELAY = REGISTRY.register("vis_relay", () -> new SimpleDirectionalBlock(metalDeviceProperties()));
    public static final DeferredBlock<EssentiaTubeBlock> ESSENTIA_TUBE = REGISTRY.register("essentia_tube",
            () -> new EssentiaTubeBlock(tubeProperties()));
    public static final DeferredBlock<EssentiaTubeBlock> ESSENTIA_VALVE = tube("essentia_valve", TubeMode.VALVE);
    public static final DeferredBlock<SimpleTubeBlock> ALCHEMICAL_CENTRIFUGE = tube("alchemical_centrifuge");
    public static final DeferredBlock<EssentiaTubeBlock> FILTERED_ESSENTIA_TUBE = tube("filtered_essentia_tube",
            TubeMode.FILTERED);
    public static final DeferredBlock<EssentiaTubeBlock> ESSENTIA_BUFFER = tube("essentia_buffer", TubeMode.BUFFER);
    public static final DeferredBlock<EssentiaTubeBlock> RESTRICTED_ESSENTIA_TUBE = tube("restricted_essentia_tube",
            TubeMode.RESTRICTED);
    public static final DeferredBlock<EssentiaTubeBlock> DIRECTIONAL_ESSENTIA_TUBE = tube("directional_essentia_tube",
            TubeMode.DIRECTIONAL);
    public static final DeferredBlock<SimpleTubeBlock> ESSENTIA_CRYSTALLIZER = tube("essentia_crystallizer");
    public static final DeferredBlock<WardedJarBlock> WARDED_JAR = REGISTRY.register("warded_jar",
            () -> new WardedJarBlock(jarProperties()));
    public static final DeferredBlock<BrainInAJarBlock> BRAIN_IN_A_JAR = REGISTRY.register("brain_in_a_jar",
            () -> new BrainInAJarBlock(jarProperties()));
    public static final DeferredBlock<NodeJarBlock> NODE_IN_A_JAR = REGISTRY.register("node_in_a_jar",
            () -> new NodeJarBlock(jarProperties()));
    public static final DeferredBlock<WardedJarBlock> VOID_JAR = REGISTRY.register("void_jar",
            () -> new WardedJarBlock(jarProperties()));
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
            FLUX_GOO,
            FLUX_GAS,
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

    private static DeferredBlock<ThaumcraftOreBlock> ore(String name, OreType oreType) {
        return ore(name, oreType, 0);
    }

    private static DeferredBlock<ThaumcraftOreBlock> glowingOre(String name, OreType oreType) {
        return ore(name, oreType, 7);
    }

    private static DeferredBlock<ThaumcraftOreBlock> ore(String name, OreType oreType, int lightLevel) {
        return REGISTRY.register(name, () -> new ThaumcraftOreBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                        .strength(1.5F, 5.0F)
                        .requiresCorrectToolForDrops()
                        .lightLevel(state -> lightLevel)
                        .randomTicks(),
                oreType));
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
                .lightLevel(state -> lightLevel).offsetType(BlockBehaviour.OffsetType.NONE)));
    }

    private static DeferredBlock<MagicSaplingBlock> magicSapling(String name, int lightLevel, TreeKind treeKind) {
        return REGISTRY.register(name, () -> new MagicSaplingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)
                .lightLevel(state -> lightLevel).offsetType(BlockBehaviour.OffsetType.NONE), treeKind));
    }

    private static DeferredBlock<SimpleTableBlock> table(String name) {
        return REGISTRY.register(name, () -> new SimpleTableBlock(tableProperties()));
    }

    private static BlockBehaviour.Properties tableProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.5F, 3.0F)
                .noOcclusion();
    }

    private static DeferredBlock<Block> stoneDevice(String name) {
        return REGISTRY.registerSimpleBlock(name, stoneDeviceProperties());
    }

    private static BlockBehaviour.Properties stoneDeviceProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(3.0F, 25.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    private static DeferredBlock<Block> metalDevice(String name) {
        return REGISTRY.registerSimpleBlock(name, metalDeviceProperties());
    }

    private static BlockBehaviour.Properties metalDeviceProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(3.0F, 17.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    private static BlockBehaviour.Properties fluxProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .strength(100.0F)
                .noLootTable()
                .noCollission()
                .noOcclusion();
    }

    private static DeferredBlock<SimpleTubeBlock> tube(String name) {
        return REGISTRY.register(name, () -> new SimpleTubeBlock(tubeProperties()));
    }

    private static DeferredBlock<EssentiaTubeBlock> tube(String name, TubeMode mode) {
        return REGISTRY.register(name, () -> new EssentiaTubeBlock(tubeProperties(), mode));
    }

    private static BlockBehaviour.Properties tubeProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(0.5F, 5.0F)
                .noOcclusion();
    }

    private static DeferredBlock<SimpleJarBlock> jar(String name) {
        return REGISTRY.register(name, () -> new SimpleJarBlock(jarProperties()));
    }

    private static BlockBehaviour.Properties jarProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 1.0F)
                .sound(JAR_SOUND)
                .noOcclusion();
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
