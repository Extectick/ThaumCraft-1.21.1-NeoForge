package thaumcraft.common.crafting;

import net.minecraft.world.item.crafting.CraftingInput;
import thaumcraft.api.aspects.PrimalVisStorage;

public interface DynamicArcaneRecipe {
    PrimalVisStorage getVisCost(CraftingInput input);
}
