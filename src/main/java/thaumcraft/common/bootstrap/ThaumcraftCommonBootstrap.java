package thaumcraft.common.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.network.TCNetwork;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCCreativeTabs;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCFeatures;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCParticleTypes;
import thaumcraft.common.registry.TCRecipeSerializers;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.items.equipment.TCArmorMaterials;

public final class ThaumcraftCommonBootstrap {
    private ThaumcraftCommonBootstrap() {
    }

    public static void register(IEventBus modEventBus, ModContainer modContainer) {
        TCBlocks.REGISTRY.register(modEventBus);
        TCItems.REGISTRY.register(modEventBus);
        TCArmorMaterials.REGISTRAR.register(modEventBus);
        TCBlockEntities.REGISTRY.register(modEventBus);
        TCEntityTypes.REGISTRY.register(modEventBus);
        TCFeatures.REGISTRY.register(modEventBus);
        TCMenuTypes.REGISTRY.register(modEventBus);
        TCRecipeTypes.REGISTRY.register(modEventBus);
        TCRecipeSerializers.REGISTRY.register(modEventBus);
        TCSoundEvents.REGISTRY.register(modEventBus);
        TCParticleTypes.REGISTRY.register(modEventBus);
        TCDataAttachments.REGISTRY.register(modEventBus);
        TCDataComponents.REGISTRY.register(modEventBus);
        TCCreativeTabs.REGISTRY.register(modEventBus);
        modEventBus.addListener(TCNetwork::registerPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, ThaumcraftConfig.SPEC);
    }
}
