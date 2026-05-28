package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumcraft.common.registry.TCItems;

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
            private final JarItemRenderer renderer = JarItemRenderer.create();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        }, TCItems.WARDED_JAR.get(), TCItems.VOID_JAR.get(), TCItems.BRAIN_IN_A_JAR.get(), TCItems.NODE_IN_A_JAR.get());
    }
}
