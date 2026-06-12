package thaumictinkerer.common.registry;

import thaumictinkerer.common.registry.TTItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.common.registry.TCItems;
import thaumictinkerer.ThaumicTinkerer;

public class TTTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThaumicTinkerer.MODID);

    public static final java.util.function.Supplier<CreativeModeTab> THAUMIC_TINKERER_TAB = REGISTRY.register("thaumictinkerer",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.thaumictinkerer"))
                    .icon(() -> new ItemStack(TTItems.DARK_QUARTZ.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(TTItems.DARK_QUARTZ.get());
                        output.accept(TTItems.ENDER_SHARD.get());
                        output.accept(TTItems.NETHER_SHARD.get());
                        output.accept(TTItems.ICHOR.get());
                        output.accept(TTItems.ICHOR_CLOTH.get());
                        output.accept(TTItems.ICHORIUM.get());
                        output.accept(TTItems.ICHOR_NUGGET.get());

                        output.accept(TTItems.SHARE_BOOK.get());
                        output.accept(TTItems.SPELL_CLOTH.get());
                        output.accept(TTItems.BLOOD_SWORD.get());
                        output.accept(TTItems.ICHOR_FOCUS_POUCH.get());
                        output.accept(TTItems.BRIGHT_NITOR.get());
                        output.accept(TTItems.ICHOR_CAP.get());
                        output.accept(TTItems.ICHOR_ROD.get());

                        output.accept(TTItems.DEFLECT_FOCUS.get());
                        output.accept(TTItems.DISLOCATION_FOCUS.get());
                        output.accept(TTItems.ENDER_CHEST_FOCUS.get());
                        output.accept(TTItems.FLIGHT_FOCUS.get());
                        output.accept(TTItems.HEAL_FOCUS.get());
                        output.accept(TTItems.SMELT_FOCUS.get());
                        output.accept(TTItems.TELEKINESIS_FOCUS.get());
                        output.accept(TTItems.XP_DRAIN_FOCUS.get());

                        output.accept(TTItems.BLOCK_TALISMAN.get());
                        output.accept(TTItems.CLEANSING_TALISMAN.get());
                        output.accept(TTItems.XP_TALISMAN.get());

                        output.accept(TTItems.ICHOR_PICKAXE.get());
                        output.accept(TTItems.ICHOR_SHOVEL.get());
                        output.accept(TTItems.ICHOR_AXE.get());
                        output.accept(TTItems.ICHOR_SWORD.get());
                        output.accept(TTItems.ICHOR_HELMET.get());
                        output.accept(TTItems.ICHOR_CHESTPLATE.get());
                        output.accept(TTItems.ICHOR_LEGGINGS.get());
                        output.accept(TTItems.ICHOR_BOOTS.get());

                        output.accept(TTItems.ADVANCED_ICHOR_PICKAXE.get());
                        output.accept(TTItems.ADVANCED_ICHOR_SHOVEL.get());
                        output.accept(TTItems.ADVANCED_ICHOR_AXE.get());
                        output.accept(TTItems.ADVANCED_ICHOR_SWORD.get());
                        output.accept(TTItems.ADVANCED_ICHOR_HELMET.get());
                        output.accept(TTItems.ADVANCED_ICHOR_CHESTPLATE.get());
                        output.accept(TTItems.ADVANCED_ICHOR_LEGGINGS.get());
                        output.accept(TTItems.ADVANCED_ICHOR_BOOTS.get());

                        output.accept(TTItems.DARK_QUARTZ_BLOCK.get());

                        output.accept(TTItems.AIR_SEED.get());
                        output.accept(TTItems.FIRE_SEED.get());
                        output.accept(TTItems.WATER_SEED.get());
                        output.accept(TTItems.EARTH_SEED.get());
                        output.accept(TTItems.ORDER_SEED.get());
                        output.accept(TTItems.ENTROPY_SEED.get());
                        output.accept(TTItems.COMPOUND_SEED.get());

                        output.accept(TTItems.AIR_FRUIT.get());
                        output.accept(TTItems.FIRE_FRUIT.get());
                        output.accept(TTItems.WATER_FRUIT.get());
                        output.accept(TTItems.EARTH_FRUIT.get());
                        output.accept(TTItems.ORDER_FRUIT.get());
                        output.accept(TTItems.ENTROPY_FRUIT.get());

                        output.accept(TTItems.AER_MOB_ASPECT.get());
                        output.accept(TTItems.AER_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.AER_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.ALIENIS_MOB_ASPECT.get());
                        output.accept(TTItems.ALIENIS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.ALIENIS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.AQUA_MOB_ASPECT.get());
                        output.accept(TTItems.AQUA_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.AQUA_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.BESTIA_MOB_ASPECT.get());
                        output.accept(TTItems.BESTIA_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.BESTIA_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.CORPUS_MOB_ASPECT.get());
                        output.accept(TTItems.CORPUS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.CORPUS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.EXANIMIS_MOB_ASPECT.get());
                        output.accept(TTItems.EXANIMIS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.EXANIMIS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.HUMANUS_MOB_ASPECT.get());
                        output.accept(TTItems.HUMANUS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.HUMANUS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.IGNIS_MOB_ASPECT.get());
                        output.accept(TTItems.IGNIS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.IGNIS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.ITER_MOB_ASPECT.get());
                        output.accept(TTItems.ITER_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.ITER_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.LIMUS_MOB_ASPECT.get());
                        output.accept(TTItems.LIMUS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.LIMUS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.MESSIS_MOB_ASPECT.get());
                        output.accept(TTItems.MESSIS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.MESSIS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.METALLUM_MOB_ASPECT.get());
                        output.accept(TTItems.METALLUM_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.METALLUM_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.PANNUS_MOB_ASPECT.get());
                        output.accept(TTItems.PANNUS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.PANNUS_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.PRAECANTATIO_MOB_ASPECT.get());
                        output.accept(TTItems.PRAECANTATIO_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.PRAECANTATIO_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.TERRA_MOB_ASPECT.get());
                        output.accept(TTItems.TERRA_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.TERRA_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.VENENUM_MOB_ASPECT.get());
                        output.accept(TTItems.VENENUM_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.VENENUM_CONDENSED_MOB_ASPECT.get());

                        output.accept(TTItems.VOLATUS_MOB_ASPECT.get());
                        output.accept(TTItems.VOLATUS_INFUSED_MOB_ASPECT.get());
                        output.accept(TTItems.VOLATUS_CONDENSED_MOB_ASPECT.get());
                    })
                    .build());
}



