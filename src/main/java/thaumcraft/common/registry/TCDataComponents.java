package thaumcraft.common.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.items.wands.FocusPouchContents;

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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> WAND_FOCUS = REGISTRY.register("wand_focus",
            () -> DataComponentType.<ItemStack>builder()
                    .persistent(ItemStack.OPTIONAL_CODEC)
                    .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FocusPouchContents>> FOCUS_POUCH_CONTENTS = REGISTRY.register("focus_pouch_contents",
            () -> DataComponentType.<FocusPouchContents>builder()
                    .persistent(FocusPouchContents.CODEC)
                    .networkSynchronized(FocusPouchContents.STREAM_CODEC)
                    .build());

    private TCDataComponents() {
    }
}
