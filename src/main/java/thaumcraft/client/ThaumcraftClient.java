package thaumcraft.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.level.FoliageColor;
import thaumcraft.api.aspects.EssentiaStorage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.client.hud.FocusSelectorOverlay;
import thaumcraft.client.hud.ResearchNotificationOverlay;
import thaumcraft.client.hud.ThaumometerTargetOverlay;
import thaumcraft.client.hud.WandVisHudOverlay;
import thaumcraft.client.fx.EssentiaSourceFxHandler;
import thaumcraft.client.fx.InfusionSourceFxHandler;
import thaumcraft.client.input.TCKeyMappings;
import thaumcraft.client.renderers.block.ArcanePedestalRenderer;
import thaumcraft.client.renderers.block.ArcaneAlembicRenderer;
import thaumcraft.client.renderers.block.AuraNodeRenderer;
import thaumcraft.client.renderers.block.BrainInAJarRenderer;
import thaumcraft.client.renderers.block.CrucibleRenderer;
import thaumcraft.client.renderers.block.InfusionPillarRenderer;
import thaumcraft.client.renderers.block.NodeJarRenderer;
import thaumcraft.client.renderers.block.NodeStabilizerRenderer;
import thaumcraft.client.renderers.block.NodeTransducerRenderer;
import thaumcraft.client.renderers.block.RunicMatrixRenderer;
import thaumcraft.client.renderers.block.WardedJarRenderer;
import thaumcraft.client.renderers.block.HungryChestRenderer;
import thaumcraft.client.renderers.item.TCItemRenderers;
import thaumcraft.client.renderers.item.ThaumometerFirstPersonHandler;
import thaumcraft.client.screens.AlchemicalFurnaceScreen;
import thaumcraft.client.screens.ArcaneWorktableScreen;
import thaumcraft.client.screens.FocusPouchScreen;
import thaumcraft.client.screens.HandMirrorScreen;
import thaumcraft.client.screens.ResearchTableScreen;
import thaumcraft.client.screens.ThaumonomiconScreen;
import thaumcraft.client.tooltip.AspectTooltipHandler;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.research.ResearchNoteData;
import thaumcraft.common.util.ClientInteractionState;
import thaumcraft.common.util.ClientScreenHooks;

@Mod(value = Thaumcraft.MODID, dist = Dist.CLIENT)
public class ThaumcraftClient {
    private static final int SILVERWOOD_LEAF_COLOR = 0x88A3AA;
    private static final int WHITE = 0xF0F0F0;
    private static final int ORANGE = 0xEB8844;
    private static final int MAGENTA = 0xC34FC9;
    private static final int LIGHT_BLUE = 0x6689D3;
    private static final int YELLOW = 0xDEC236;
    private static final int LIME = 0x41CD34;
    private static final int PINK = 0xD88198;
    private static final int GRAY = 0x434343;
    private static final int LIGHT_GRAY = 0xA0A0A0;
    private static final int CYAN = 0x287697;
    private static final int PURPLE = 0x7B2FBE;
    private static final int BLUE = 0x253192;
    private static final int BROWN = 0x51301A;
    private static final int GREEN = 0x3B511A;
    private static final int RED = 0xB3312C;
    private static final int BLACK = 0x1E1B1B;

    public ThaumcraftClient(IEventBus modEventBus, ModContainer container) {
        ClientInteractionState.registerShiftDown(Screen::hasShiftDown);
        ClientScreenHooks.registerThaumonomiconOpener(ThaumonomiconScreen::open);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerGuiLayers);
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerClientExtensions);
        modEventBus.addListener(AspectTooltipHandler::registerFactories);
        modEventBus.addListener(this::registerBlockEntityRenderers);
        modEventBus.addListener(this::registerBlockColors);
        modEventBus.addListener(this::registerItemColors);
        NeoForge.EVENT_BUS.addListener(TCKeyMappings::onClientTick);
        NeoForge.EVENT_BUS.addListener(EssentiaSourceFxHandler::onClientTick);
        NeoForge.EVENT_BUS.addListener(InfusionSourceFxHandler::onClientTick);
        NeoForge.EVENT_BUS.addListener(AspectTooltipHandler::gatherComponents);
        NeoForge.EVENT_BUS.addListener(ThaumometerFirstPersonHandler::onRenderHand);
        NeoForge.EVENT_BUS.addListener(ThaumometerFirstPersonHandler::onInteractionKey);
        NeoForge.EVENT_BUS.addListener(AuraNodeHighlightHandler::onBlockHighlight);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.WHITE_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.ORANGE_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.MAGENTA_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.LIGHT_BLUE_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.YELLOW_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.LIME_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.PINK_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.GRAY_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.LIGHT_GRAY_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.CYAN_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.PURPLE_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.BLUE_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.BROWN_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.GREEN_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.RED_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.BLACK_TALLOW_CANDLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.WARDED_JAR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.BRAIN_IN_A_JAR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.NODE_IN_A_JAR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.VOID_JAR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.CRUCIBLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.FLUX_GOO.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.FLUX_GAS.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.MAGIC_MIRROR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(TCBlocks.ESSENTIA_MIRROR.get(), RenderType.translucent());
            ItemProperties.register(TCItems.RESEARCH_NOTES.get(), Thaumcraft.id("discovery"),
                    (stack, level, entity, seed) -> stack
                            .getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY).complete()
                                    ? 1.0F
                                    : 0.0F);
            ItemProperties.register(TCItems.SINISTER_LODESTONE.get(), Thaumcraft.id("active"),
                    (stack, level, entity, seed) -> entity != null
                            && SinisterLodestoneClientTracker.hasVisibleDarkNode(entity) ? 1.0F : 0.0F);
        });
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TCMenuTypes.ALCHEMICAL_FURNACE.get(), AlchemicalFurnaceScreen::new);
        event.register(TCMenuTypes.ARCANE_WORKTABLE.get(), ArcaneWorktableScreen::new);
        event.register(TCMenuTypes.RESEARCH_TABLE.get(), ResearchTableScreen::new);
        event.register(TCMenuTypes.FOCUS_POUCH.get(), FocusPouchScreen::new);
        event.register(TCMenuTypes.HAND_MIRROR.get(), HandMirrorScreen::new);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Thaumcraft.id("wand_vis"), (guiGraphics, deltaTracker) ->
                WandVisHudOverlay.render(guiGraphics));
        event.registerAbove(VanillaGuiLayers.HOTBAR, Thaumcraft.id("focus_selector"), (guiGraphics, deltaTracker) ->
                FocusSelectorOverlay.render(guiGraphics));
        event.registerAbove(VanillaGuiLayers.HOTBAR, Thaumcraft.id("research_notifications"), (guiGraphics, deltaTracker) ->
                ResearchNotificationOverlay.render(guiGraphics));
        event.registerAbove(VanillaGuiLayers.HOTBAR, Thaumcraft.id("thaumometer_target"), (guiGraphics, deltaTracker) ->
                ThaumometerTargetOverlay.render(guiGraphics));
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        TCKeyMappings.register(event);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        TCItemRenderers.register(event);
        event.registerBlock(AuraNodeClientExtensions.INSTANCE, TCBlocks.AURA_NODE, TCBlocks.SILVERWOOD_KNOT);
        event.registerBlock(InfusedOreClientExtensions.INSTANCE,
                TCBlocks.INFUSED_AIR_ORE, TCBlocks.INFUSED_FIRE_ORE, TCBlocks.INFUSED_WATER_ORE,
                TCBlocks.INFUSED_EARTH_ORE, TCBlocks.INFUSED_ORDER_ORE, TCBlocks.INFUSED_ENTROPY_ORE);
    }

    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TCBlockEntities.ARCANE_PEDESTAL.get(), ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.RUNIC_MATRIX.get(), RunicMatrixRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.AURA_NODE.get(), AuraNodeRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.NODE_STABILIZER.get(), NodeStabilizerRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.NODE_TRANSDUCER.get(), NodeTransducerRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.INFUSION_PILLAR.get(), InfusionPillarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.BRAIN_IN_A_JAR.get(), BrainInAJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.NODE_IN_A_JAR.get(), NodeJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.WARDED_JAR.get(), WardedJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.ARCANE_ALEMBIC.get(), ArcaneAlembicRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.CRUCIBLE.get(), CrucibleRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.HUNGRY_CHEST.get(), HungryChestRenderer::new);
        
        event.registerEntityRenderer(TCEntityTypes.FOLLOWING_ITEM.get(), ItemEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.SPECIAL_ITEM.get(), ItemEntityRenderer::new);
    }

    private void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> level != null && pos != null
                ? BiomeColors.getAverageFoliageColor(level, pos)
                : FoliageColor.getDefaultColor(), TCBlocks.GREATWOOD_LEAVES.get());
        event.register((state, level, pos, tintIndex) -> SILVERWOOD_LEAF_COLOR, TCBlocks.SILVERWOOD_LEAVES.get());
        event.register((state, level, pos, tintIndex) -> WHITE, TCBlocks.WHITE_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> ORANGE, TCBlocks.ORANGE_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> MAGENTA, TCBlocks.MAGENTA_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> LIGHT_BLUE, TCBlocks.LIGHT_BLUE_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> YELLOW, TCBlocks.YELLOW_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> LIME, TCBlocks.LIME_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> PINK, TCBlocks.PINK_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> GRAY, TCBlocks.GRAY_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> LIGHT_GRAY, TCBlocks.LIGHT_GRAY_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> CYAN, TCBlocks.CYAN_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> PURPLE, TCBlocks.PURPLE_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> BLUE, TCBlocks.BLUE_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> BROWN, TCBlocks.BROWN_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> GREEN, TCBlocks.GREEN_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> RED, TCBlocks.RED_TALLOW_CANDLE.get());
        event.register((state, level, pos, tintIndex) -> BLACK, TCBlocks.BLACK_TALLOW_CANDLE.get());
    }

    private void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) {
                return 0xFFFFFFFF;
            }
            ResearchNoteData data = stack.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
            int color = data.isEmpty() ? ResearchNoteData.EMPTY.color() : data.color();
            return 0xFF000000 | color;
        }, TCItems.RESEARCH_NOTES.get());
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(), TCItems.GREATWOOD_LEAVES.get());
        event.register((stack, tintIndex) -> SILVERWOOD_LEAF_COLOR, TCItems.SILVERWOOD_LEAVES.get());

        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) return 0xFFFFFFFF;
            EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
            return essentia.isEmpty() ? 0xFFFFFFFF : (0xFF000000 | essentia.aspect().getColor());
        }, TCItems.ESSENTIA_PHIAL.get());

        event.register((stack, tintIndex) -> {
            EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
            return essentia.isEmpty() ? 0xFFFFFFFF : (0xFF000000 | essentia.aspect().getColor());
        }, TCItems.ETHEREAL_ESSENCE.get(), TCItems.CRYSTALLIZED_ESSENCE.get());

        event.register((stack, tintIndex) -> WHITE, TCItems.WHITE_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> ORANGE, TCItems.ORANGE_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> MAGENTA, TCItems.MAGENTA_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> LIGHT_BLUE, TCItems.LIGHT_BLUE_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> YELLOW, TCItems.YELLOW_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> LIME, TCItems.LIME_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> PINK, TCItems.PINK_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> GRAY, TCItems.GRAY_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> LIGHT_GRAY, TCItems.LIGHT_GRAY_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> CYAN, TCItems.CYAN_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> PURPLE, TCItems.PURPLE_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> BLUE, TCItems.BLUE_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> BROWN, TCItems.BROWN_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> GREEN, TCItems.GREEN_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> RED, TCItems.RED_TALLOW_CANDLE.get());
        event.register((stack, tintIndex) -> BLACK, TCItems.BLACK_TALLOW_CANDLE.get());
    }
}
