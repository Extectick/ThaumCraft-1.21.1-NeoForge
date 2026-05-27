package thaumcraft.client.tooltip;

import java.util.List;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public record AspectTooltipComponent(List<Entry> entries) implements TooltipComponent {
    public AspectTooltipComponent {
        entries = List.copyOf(entries);
    }

    public static AspectTooltipComponent from(AspectList aspects) {
        return new AspectTooltipComponent(aspects.getAspectsSortedAmount().stream()
                .map(aspect -> new Entry(aspect, aspects.getAmount(aspect)))
                .toList());
    }

    public record Entry(Aspect aspect, int amount) {
    }
}
