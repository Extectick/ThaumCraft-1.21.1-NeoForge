package thaumictinkerer.client;


import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.client.gui.IchorPouchScreen;
import thaumictinkerer.client.models.KamiArmorModel;
import thaumictinkerer.common.registry.TTDataComponents;
import thaumictinkerer.common.registry.TTItems;
import thaumictinkerer.common.registry.TTMenuTypes;

@EventBusSubscriber(modid = ThaumicTinkerer.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TTClientSetup {

    public static final ModelLayerLocation KAMI_ARMOR_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "kami_armor"), "main");

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(KAMI_ARMOR_LAYER, KamiArmorModel::createLayer);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TTMenuTypes.ICHOR_POUCH.get(), IchorPouchScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            ItemProperties.register(TTItems.ADVANCED_ICHOR_AXE.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "mode"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TOOL_MODE, 0) / 2.0F);
            ItemProperties.register(TTItems.ADVANCED_ICHOR_PICKAXE.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "mode"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TOOL_MODE, 0) / 2.0F);
            ItemProperties.register(TTItems.ADVANCED_ICHOR_SHOVEL.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "mode"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TOOL_MODE, 0) / 2.0F);
            ItemProperties.register(TTItems.ADVANCED_ICHOR_SWORD.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "mode"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TOOL_MODE, 0) / 2.0F);

            ItemProperties.register(TTItems.BLOOD_SWORD.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "active"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) ? 1.0F : 0.0F);

            ItemProperties.register(TTItems.BLOCK_TALISMAN.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "active"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) ? 1.0F : 0.0F);
            ItemProperties.register(TTItems.CLEANSING_TALISMAN.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "active"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) ? 1.0F : 0.0F);
            ItemProperties.register(TTItems.XP_TALISMAN.get(), ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "active"),
                    (stack, level, entity, seed) -> stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) ? 1.0F : 0.0F);
        });
    }
}

