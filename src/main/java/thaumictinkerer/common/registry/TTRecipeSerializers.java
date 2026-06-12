package thaumictinkerer.common.registry;


import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.recipes.SpellClothRecipe;

public class TTRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThaumicTinkerer.MODID);

    public static final java.util.function.Supplier<RecipeSerializer<SpellClothRecipe>> SPELL_CLOTH_RECIPE = REGISTRY.register("spell_cloth", () -> new SimpleCraftingRecipeSerializer<>(SpellClothRecipe::new));
}




