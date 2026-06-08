package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.AuraNodeFeature;
import thaumcraft.common.world.ThaumcraftOreFeature;

public final class TCFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY =
            DeferredRegister.create(Registries.FEATURE, Thaumcraft.MODID);

    public static final DeferredHolder<Feature<?>, ThaumcraftOreFeature> ORE_GENERATION =
            REGISTRY.register("ore_generation",
                    () -> new ThaumcraftOreFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, AuraNodeFeature> AURA_NODE_GENERATION =
            REGISTRY.register("aura_node_generation",
                    () -> new AuraNodeFeature(NoneFeatureConfiguration.CODEC));

    private TCFeatures() {
    }
}
