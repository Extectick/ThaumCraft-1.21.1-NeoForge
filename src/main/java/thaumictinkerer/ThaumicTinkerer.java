package thaumictinkerer;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumictinkerer.common.registry.TTBlocks;
import thaumictinkerer.common.registry.TTItems;
import thaumictinkerer.common.registry.TTTabs;
import thaumictinkerer.common.items.equipment.TTArmorMaterials;
import thaumictinkerer.common.registry.TTDataComponents;
import thaumictinkerer.common.registry.TTRecipeSerializers;
import thaumictinkerer.common.registry.TTMenuTypes;

@Mod(ThaumicTinkerer.MODID)
public class ThaumicTinkerer {
    public static final String MODID = "thaumictinkerer";

    public ThaumicTinkerer(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
        
        TTBlocks.REGISTRY.register(modEventBus);
        TTItems.REGISTRY.register(modEventBus);
        TTTabs.REGISTRY.register(modEventBus);
        TTArmorMaterials.REGISTRAR.register(modEventBus);
        TTDataComponents.REGISTRY.register(modEventBus);
        TTRecipeSerializers.REGISTRY.register(modEventBus);
        TTMenuTypes.REGISTRY.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Setup code
    }
}

