package thaumcraft.common.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

public sealed interface ResearchTrigger permits ResearchTrigger.ItemTrigger, ResearchTrigger.EntityTrigger,
        ResearchTrigger.AspectTrigger {
    record ItemTrigger(ItemStack stack) implements ResearchTrigger {
        public ItemTrigger {
            stack = stack.copy();
        }
    }

    record EntityTrigger(ResourceLocation entityType) implements ResearchTrigger {
    }

    record AspectTrigger(Aspect aspect) implements ResearchTrigger {
    }
}
