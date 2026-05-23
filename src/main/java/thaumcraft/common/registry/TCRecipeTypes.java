package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;

public final class TCRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ArcaneWorktableRecipe>> ARCANE_WORKTABLE =
            REGISTRY.register("arcane_worktable", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID, "arcane_worktable")));

    private TCRecipeTypes() {
    }
}
