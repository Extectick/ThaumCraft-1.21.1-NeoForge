package thaumcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import thaumcraft.common.bootstrap.ThaumcraftCommonBootstrap;
import thaumcraft.common.bootstrap.ThaumcraftServerBootstrap;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        ThaumcraftCommonBootstrap.register(modEventBus, modContainer);
        ThaumcraftServerBootstrap.register(NeoForge.EVENT_BUS);
        LOGGER.info("Thaumcraft NeoForge port bootstrap loaded");
    }
}
