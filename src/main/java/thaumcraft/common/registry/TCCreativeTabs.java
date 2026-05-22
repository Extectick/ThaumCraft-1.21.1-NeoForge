package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thaumcraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THAUMCRAFT = REGISTRY.register("thaumcraft", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thaumcraft"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> TCItems.THAUMONOMICON.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(TCItems.THAUMONOMICON.get());
                output.accept(TCItems.RUNIC_RING_LESSER.get());
                output.accept(TCItems.RUNIC_RING.get());
                output.accept(TCItems.RUNIC_RING_CHARGED.get());
                output.accept(TCItems.RUNIC_RING_REGEN.get());
                output.accept(TCItems.RUNIC_AMULET.get());
                output.accept(TCItems.RUNIC_AMULET_EMERGENCY.get());
                output.accept(TCItems.RUNIC_GIRDLE.get());
                output.accept(TCItems.RUNIC_GIRDLE_KINETIC.get());
                output.accept(TCItems.VIS_AMULET_LESSER.get());
                output.accept(TCItems.VIS_AMULET.get());
                output.accept(TCItems.HOVER_GIRDLE.get());
                output.accept(TCItems.BAUBLE_AMULET.get());
                output.accept(TCItems.BAUBLE_RING.get());
                output.accept(TCItems.BAUBLE_BELT.get());
                output.accept(TCItems.BAUBLE_RING_IRON.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_AIR.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_FIRE.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_WATER.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_EARTH.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_ORDER.get());
                output.accept(TCItems.VIS_DISCOUNT_RING_ENTROPY.get());
                output.accept(TCItems.FOCUS_POUCH.get());
            })
            .build());

    private TCCreativeTabs() {
    }
}
