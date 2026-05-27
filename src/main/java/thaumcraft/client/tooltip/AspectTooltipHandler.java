package thaumcraft.client.tooltip;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;

public final class AspectTooltipHandler {
    private AspectTooltipHandler() {
    }

    public static void registerFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AspectTooltipComponent.class, ClientAspectTooltipComponent::new);
    }

    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        if (!Screen.hasShiftDown() || event.getItemStack().isEmpty()) {
            return;
        }

        AspectList aspects = ObjectAspectRegistry.getObjectTagsWithBonus(event.getItemStack());
        if (aspects.isEmpty()) {
            return;
        }

        event.getTooltipElements().add(Either.right(AspectTooltipComponent.from(aspects)));
    }
}
