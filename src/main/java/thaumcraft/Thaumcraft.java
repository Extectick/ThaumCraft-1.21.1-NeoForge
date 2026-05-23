package thaumcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.lib.events.RunicShieldEvents;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCCreativeTabs;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCParticleTypes;
import thaumcraft.common.registry.TCSoundEvents;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        TCBlocks.REGISTRY.register(modEventBus);
        TCItems.REGISTRY.register(modEventBus);
        TCBlockEntities.REGISTRY.register(modEventBus);
        TCEntityTypes.REGISTRY.register(modEventBus);
        TCMenuTypes.REGISTRY.register(modEventBus);
        TCSoundEvents.REGISTRY.register(modEventBus);
        TCParticleTypes.REGISTRY.register(modEventBus);
        TCDataComponents.REGISTRY.register(modEventBus);
        TCCreativeTabs.REGISTRY.register(modEventBus);
        NeoForge.EVENT_BUS.register(new RunicShieldEvents());

        modContainer.registerConfig(ModConfig.Type.COMMON, ThaumcraftConfig.SPEC);
        LOGGER.info("Thaumcraft NeoForge port bootstrap loaded");
    }
}
