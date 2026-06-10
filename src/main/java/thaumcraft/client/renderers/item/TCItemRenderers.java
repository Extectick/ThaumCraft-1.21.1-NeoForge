package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.items.equipment.FortressArmorItem;
import thaumcraft.common.items.equipment.CultistRobeArmorItem;
import thaumcraft.common.items.equipment.CultistPlateArmorItem;
import thaumcraft.common.items.equipment.CultistLeaderArmorItem;
import thaumcraft.common.items.equipment.VoidRobeArmorItem;
import thaumcraft.common.items.equipment.RobeArmorItem;

public final class TCItemRenderers {
    private TCItemRenderers() {
    }

    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final WandItemRenderer renderer = WandItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.WAND_CASTING.get());
        event.registerItem(new IClientItemExtensions() {
            private final ArcaneAlembicItemRenderer renderer = ArcaneAlembicItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.ARCANE_ALEMBIC.get());
        event.registerItem(new IClientItemExtensions() {
            private final ThaumometerItemRenderer renderer = ThaumometerItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.THAUMOMETER.get());
        event.registerItem(new IClientItemExtensions() {
            private final AuraNodeItemRenderer renderer = AuraNodeItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.AURA_NODE.get());
        event.registerItem(new IClientItemExtensions() {
            private final JarItemRenderer renderer = JarItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.WARDED_JAR.get(), TCItems.VOID_JAR.get(), TCItems.BRAIN_IN_A_JAR.get(), TCItems.NODE_IN_A_JAR.get());
        event.registerItem(new IClientItemExtensions() {
            private final NodeStabilizerItemRenderer renderer = NodeStabilizerItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.NODE_STABILIZER.get(), TCItems.ADVANCED_NODE_STABILIZER.get());

        event.registerItem(FortressArmorItem.getExtensions(), TCItems.FORTRESS_HELMET.get(), TCItems.FORTRESS_CHESTPLATE.get(), TCItems.FORTRESS_LEGGINGS.get());
        event.registerItem(CultistRobeArmorItem.getExtensions(), TCItems.CRIMSON_ROBE_HELMET.get(), TCItems.CRIMSON_ROBE_CHESTPLATE.get(), TCItems.CRIMSON_ROBE_LEGGINGS.get());
        event.registerItem(CultistPlateArmorItem.getExtensions(), TCItems.CRIMSON_PLATE_HELMET.get(), TCItems.CRIMSON_PLATE_CHESTPLATE.get(), TCItems.CRIMSON_PLATE_LEGGINGS.get());
        event.registerItem(CultistLeaderArmorItem.getExtensions(), TCItems.CRIMSON_LEADER_HELMET.get(), TCItems.CRIMSON_LEADER_CHESTPLATE.get(), TCItems.CRIMSON_LEADER_LEGGINGS.get());
        event.registerItem(VoidRobeArmorItem.getExtensions(), TCItems.VOID_ROBE_HELMET.get(), TCItems.VOID_ROBE_CHESTPLATE.get(), TCItems.VOID_ROBE_LEGGINGS.get());
    }
}
