package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.crafting.WandAssemblyRecipe;

public final class TCRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER,
            Thaumcraft.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ArcaneWorktableRecipe>> ARCANE_WORKTABLE =
            REGISTRY.register("arcane_worktable", ArcaneWorktableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WandAssemblyRecipe>> WAND_ASSEMBLY =
            REGISTRY.register("wand_assembly", WandAssemblyRecipe.Serializer::new);

    private TCRecipeSerializers() {
    }
}
