package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import thaumcraft.common.registry.TCBlocks;

public final class AuraNodeHighlightHandler {
    private AuraNodeHighlightHandler() {
    }

    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        var level = Minecraft.getInstance().level;
        if (level != null && level.getBlockState(event.getTarget().getBlockPos()).is(TCBlocks.AURA_NODE.get())) {
            event.setCanceled(true);
        }
    }
}
