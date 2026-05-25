package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneWorktableBlockEntity>> ARCANE_WORKTABLE =
            REGISTRY.register("arcane_worktable", () -> BlockEntityType.Builder
                    .of(ArcaneWorktableBlockEntity::new, TCBlocks.ARCANE_WORKTABLE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
            REGISTRY.register("research_table", () -> BlockEntityType.Builder
                    .of(ResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WardedJarBlockEntity>> WARDED_JAR =
            REGISTRY.register("warded_jar", () -> BlockEntityType.Builder
                    .of(WardedJarBlockEntity::new, TCBlocks.WARDED_JAR.get())
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

    private TCBlockEntities() {
    }
}
