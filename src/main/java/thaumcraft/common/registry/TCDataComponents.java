package thaumcraft.common.registry;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.research.ResearchNoteData;

public final class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Thaumcraft.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PrimalVisStorage>> WAND_VIS = REGISTRY.register("wand_vis",
            () -> DataComponentType.<PrimalVisStorage>builder()
                    .persistent(PrimalVisStorage.CODEC)
                    .networkSynchronized(PrimalVisStorage.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EssentiaStorage>> ESSENTIA = REGISTRY.register("essentia",
            () -> DataComponentType.<EssentiaStorage>builder()
                    .persistent(EssentiaStorage.CODEC)
                    .networkSynchronized(EssentiaStorage.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Aspect>> JAR_FILTER = REGISTRY.register("jar_filter",
            () -> DataComponentType.<Aspect>builder()
                    .persistent(Aspect.CODEC)
                    .networkSynchronized(Aspect.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> WAND_FOCUS = REGISTRY.register("wand_focus",
            () -> DataComponentType.<ItemStack>builder()
                    .persistent(ItemStack.OPTIONAL_CODEC)
                    .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FOCUS_FRUGAL = REGISTRY.register("focus_frugal",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WAND_ROD = REGISTRY.register("wand_rod",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WAND_CAP = REGISTRY.register("wand_cap",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WAND_SCEPTRE = REGISTRY.register("wand_sceptre",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FocusPouchContents>> FOCUS_POUCH_CONTENTS = REGISTRY.register("focus_pouch_contents",
            () -> DataComponentType.<FocusPouchContents>builder()
                    .persistent(FocusPouchContents.CODEC)
                    .networkSynchronized(FocusPouchContents.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResearchNoteData>> RESEARCH_NOTE = REGISTRY.register("research_note",
            () -> DataComponentType.<ResearchNoteData>builder()
                    .persistent(ResearchNoteData.CODEC)
                    .networkSynchronized(ResearchNoteData.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<NodeJarData>> NODE_JAR_DATA =
            REGISTRY.register("node_jar_data",
                    () -> DataComponentType.<NodeJarData>builder()
                            .persistent(NodeJarData.CODEC)
                            .networkSynchronized(NodeJarData.STREAM_CODEC)
                            .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORTRESS_MASK = REGISTRY.register("fortress_mask",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FORTRESS_GOGGLES = REGISTRY.register("fortress_goggles",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    private TCDataComponents() {
    }
}
