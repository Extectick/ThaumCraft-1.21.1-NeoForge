package thaumcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.lib.events.TCCommands;
import thaumcraft.common.lib.events.RunicShieldEvents;
import thaumcraft.common.network.TCNetwork;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCCreativeTabs;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCParticleTypes;
import thaumcraft.common.registry.TCRecipeSerializers;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.common.registry.TCSoundEvents;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();
    private final long bootstrapStart = System.nanoTime();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        logBootstrap("constructor start");
        TCBlocks.REGISTRY.register(modEventBus);
        TCItems.REGISTRY.register(modEventBus);
        TCBlockEntities.REGISTRY.register(modEventBus);
        TCEntityTypes.REGISTRY.register(modEventBus);
        TCMenuTypes.REGISTRY.register(modEventBus);
        TCRecipeTypes.REGISTRY.register(modEventBus);
        TCRecipeSerializers.REGISTRY.register(modEventBus);
        TCSoundEvents.REGISTRY.register(modEventBus);
        TCParticleTypes.REGISTRY.register(modEventBus);
        TCDataAttachments.REGISTRY.register(modEventBus);
        TCDataComponents.REGISTRY.register(modEventBus);
        TCCreativeTabs.REGISTRY.register(modEventBus);
        modEventBus.addListener(TCNetwork::registerPayloads);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::interModEnqueue);
        modEventBus.addListener(this::interModProcess);
        modEventBus.addListener(this::loadComplete);
        NeoForge.EVENT_BUS.addListener(ObjectAspectRegistry::registerReloadListener);
        NeoForge.EVENT_BUS.addListener(TCCommands::register);
        NeoForge.EVENT_BUS.register(new RunicShieldEvents());

        modContainer.registerConfig(ModConfig.Type.COMMON, ThaumcraftConfig.SPEC);
        logBootstrap("constructor complete");
        LOGGER.info("Thaumcraft NeoForge port bootstrap loaded");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        logBootstrap("FMLCommonSetupEvent");
    }

    private void interModEnqueue(InterModEnqueueEvent event) {
        logBootstrap("InterModEnqueueEvent");
    }

    private void interModProcess(InterModProcessEvent event) {
        logBootstrap("InterModProcessEvent");
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        logBootstrap("FMLLoadCompleteEvent");
    }

    private void logBootstrap(String marker) {
        long elapsedMs = (System.nanoTime() - this.bootstrapStart) / 1_000_000L;
        LOGGER.info("Thaumcraft startup timing: {} at {} ms", marker, elapsedMs);
    }
}
