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
    }
}
