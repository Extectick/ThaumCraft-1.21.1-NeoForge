package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ResearchNotesItem;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.research.ResearchRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.registry.TCDataComponents;

public final class TCCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thaumcraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THAUMCRAFT = REGISTRY.register("thaumcraft", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thaumcraft"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> TCItems.THAUMONOMICON.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                TCItems.SIMPLE_ITEMS.forEach(item -> {
                    if (item != TCItems.ETHEREAL_ESSENCE && item != TCItems.ESSENTIA_PHIAL) {
                        output.accept(item.get());
                    }
                });
                output.accept(WandVisHelper.fillAllVis(TCItems.WAND_CASTING.get().getDefaultInstance()));
                output.accept(WandVisHelper.fillAllVis(WandCastingItem.createVariant(TCItems.WAND_CASTING.get(),
                        WandCastingItem.ROD_WOOD, WandCastingItem.CAP_IRON, true)));
                output.accept(WandVisHelper.fillAllVis(WandCastingItem.createVariant(TCItems.WAND_CASTING.get(),
                        WandCastingItem.ROD_GREATWOOD_STAFF, WandCastingItem.CAP_IRON, false)));
                output.accept(WandVisHelper.fillAllVis(WandCastingItem.createVariant(TCItems.WAND_CASTING.get(),
                        WandCastingItem.ROD_SILVERWOOD, WandCastingItem.CAP_THAUMIUM, false)));
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
                TCItems.FOCUS_ITEMS.forEach(item -> output.accept(item.get()));
                output.accept(ResearchNotesItem.createUnknown());
                ResearchRegistry.entries().forEach(entry -> output.accept(ResearchNotesItem.create(entry.key())));

                for (Aspect aspect : Aspect.values()) {
                    ItemStack wisp = new ItemStack(TCItems.ETHEREAL_ESSENCE.get());
                    wisp.set(TCDataComponents.ESSENTIA, new EssentiaStorage(aspect, 2));
                    output.accept(wisp);

                    ItemStack phial = new ItemStack(TCItems.ESSENTIA_PHIAL.get());
                    phial.set(TCDataComponents.ESSENTIA, new EssentiaStorage(aspect, 8));
                    output.accept(phial);
                }
            })
            .build());

    private TCCreativeTabs() {
    }
}
