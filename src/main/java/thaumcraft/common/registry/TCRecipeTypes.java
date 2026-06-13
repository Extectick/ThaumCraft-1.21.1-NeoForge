package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.crafting.CrucibleRecipe;
import thaumcraft.common.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.crafting.InfusionRecipe;

public final class TCRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ArcaneWorktableRecipe>> ARCANE_WORKTABLE =
            REGISTRY.register("arcane_worktable", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID, "arcane_worktable")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<InfusionRecipe>> INFUSION =
            REGISTRY.register("infusion", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID, "infusion")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<InfusionEnchantmentRecipe>> INFUSION_ENCHANTMENT =
            REGISTRY.register("infusion_enchantment", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID, "infusion_enchantment")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleRecipe>> CRUCIBLE =
            REGISTRY.register("crucible", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID, "crucible")));

    private TCRecipeTypes() {
    }
}
