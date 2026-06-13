package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.blockentities.ArcaneAlembicBlockEntity;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.blockentities.BrainInAJarBlockEntity;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.InfusionPillarBlockEntity;
import thaumcraft.common.blockentities.NodeJarBlockEntity;
import thaumcraft.common.blockentities.NodeStabilizerBlockEntity;
import thaumcraft.common.blockentities.NodeTransducerBlockEntity;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.blockentities.HungryChestBlockEntity;
import thaumcraft.common.blockentities.WardingStoneBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneWorktableBlockEntity>> ARCANE_WORKTABLE =
            REGISTRY.register("arcane_worktable", () -> BlockEntityType.Builder
                    .of(ArcaneWorktableBlockEntity::new, TCBlocks.ARCANE_WORKTABLE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemicalFurnaceBlockEntity>> ALCHEMICAL_FURNACE =
            REGISTRY.register("alchemical_furnace", () -> BlockEntityType.Builder
                    .of(AlchemicalFurnaceBlockEntity::new, TCBlocks.ALCHEMICAL_FURNACE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneAlembicBlockEntity>> ARCANE_ALEMBIC =
            REGISTRY.register("arcane_alembic", () -> BlockEntityType.Builder
                    .of(ArcaneAlembicBlockEntity::new, TCBlocks.ARCANE_ALEMBIC.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcanePedestalBlockEntity>> ARCANE_PEDESTAL =
            REGISTRY.register("arcane_pedestal", () -> BlockEntityType.Builder
                    .of(ArcanePedestalBlockEntity::new, TCBlocks.ARCANE_PEDESTAL.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicMatrixBlockEntity>> RUNIC_MATRIX =
            REGISTRY.register("runic_matrix", () -> BlockEntityType.Builder
                    .of(RunicMatrixBlockEntity::new, TCBlocks.RUNIC_MATRIX.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AuraNodeBlockEntity>> AURA_NODE =
            REGISTRY.register("aura_node", () -> BlockEntityType.Builder
                    .of(AuraNodeBlockEntity::new, TCBlocks.AURA_NODE.get(), TCBlocks.SILVERWOOD_KNOT.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NodeStabilizerBlockEntity>> NODE_STABILIZER =
            REGISTRY.register("node_stabilizer", () -> BlockEntityType.Builder
                    .of(NodeStabilizerBlockEntity::new,
                            TCBlocks.NODE_STABILIZER.get(), TCBlocks.ADVANCED_NODE_STABILIZER.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NodeTransducerBlockEntity>> NODE_TRANSDUCER =
            REGISTRY.register("node_transducer", () -> BlockEntityType.Builder
                    .of(NodeTransducerBlockEntity::new, TCBlocks.NODE_TRANSDUCER.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusionPillarBlockEntity>> INFUSION_PILLAR =
            REGISTRY.register("infusion_pillar", () -> BlockEntityType.Builder
                    .of(InfusionPillarBlockEntity::new, TCBlocks.INFUSION_PILLAR.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
            REGISTRY.register("research_table", () -> BlockEntityType.Builder
                    .of(ResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WardedJarBlockEntity>> WARDED_JAR =
            REGISTRY.register("warded_jar", () -> BlockEntityType.Builder
                    .of(WardedJarBlockEntity::new, TCBlocks.WARDED_JAR.get(), TCBlocks.VOID_JAR.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrainInAJarBlockEntity>> BRAIN_IN_A_JAR =
            REGISTRY.register("brain_in_a_jar", () -> BlockEntityType.Builder
                    .of(BrainInAJarBlockEntity::new, TCBlocks.BRAIN_IN_A_JAR.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NodeJarBlockEntity>> NODE_IN_A_JAR =
            REGISTRY.register("node_in_a_jar", () -> BlockEntityType.Builder
                    .of(NodeJarBlockEntity::new, TCBlocks.NODE_IN_A_JAR.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
            REGISTRY.register("crucible", () -> BlockEntityType.Builder
                    .of(CrucibleBlockEntity::new, TCBlocks.CRUCIBLE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EssentiaTubeBlockEntity>> ESSENTIA_TUBE =
            REGISTRY.register("essentia_tube", () -> BlockEntityType.Builder
                    .of(EssentiaTubeBlockEntity::new,
                            TCBlocks.ESSENTIA_TUBE.get(),
                            TCBlocks.ESSENTIA_VALVE.get(),
                            TCBlocks.FILTERED_ESSENTIA_TUBE.get(),
                            TCBlocks.ESSENTIA_BUFFER.get(),
                            TCBlocks.RESTRICTED_ESSENTIA_TUBE.get(),
                            TCBlocks.DIRECTIONAL_ESSENTIA_TUBE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HungryChestBlockEntity>> HUNGRY_CHEST =
            REGISTRY.register("hungry_chest", () -> BlockEntityType.Builder
                    .of(HungryChestBlockEntity::new, TCBlocks.HUNGRY_CHEST.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WardingStoneBlockEntity>> WARDING_STONE =
            REGISTRY.register("warding_stone", () -> BlockEntityType.Builder
                    .of(WardingStoneBlockEntity::new, TCBlocks.PAVING_STONE_WARDING.get())
                    .build(null));

    private TCBlockEntities() {
    }
}
