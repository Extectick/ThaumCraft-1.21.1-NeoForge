package thaumcraft.client.compat.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.menus.ArcaneWorktableMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCRecipeTypes;

@JeiPlugin
public class ThaumcraftJeiPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<ArcaneWorktableRecipe>> ARCANE_WORKTABLE =
            RecipeType.createRecipeHolderType(Thaumcraft.id("arcane_worktable"));
    public static final RecipeType<RecipeHolder<InfusionRecipe>> INFUSION =
            RecipeType.createRecipeHolderType(Thaumcraft.id("infusion"));

    @Override
    public ResourceLocation getPluginUid() {
        return Thaumcraft.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ArcaneWorktableRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new InfusionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        RecipeManager recipeManager = minecraft.level.getRecipeManager();
        List<RecipeHolder<ArcaneWorktableRecipe>> arcaneRecipes =
                recipeManager.getAllRecipesFor(TCRecipeTypes.ARCANE_WORKTABLE.get());
        List<RecipeHolder<InfusionRecipe>> infusionRecipes =
                recipeManager.getAllRecipesFor(TCRecipeTypes.INFUSION.get());
        registration.addRecipes(ARCANE_WORKTABLE, arcaneRecipes);
        registration.addRecipes(INFUSION, infusionRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(ARCANE_WORKTABLE, TCBlocks.ARCANE_WORKTABLE.get());
        registration.addRecipeCatalysts(INFUSION, TCBlocks.RUNIC_MATRIX.get(), TCBlocks.ARCANE_PEDESTAL.get());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ArcaneWorktableMenu.class, TCMenuTypes.ARCANE_WORKTABLE.get(),
                ARCANE_WORKTABLE, ArcaneWorktableMenu.GRID_SLOT_START,
                ArcaneWorktableMenu.GRID_SLOT_END - ArcaneWorktableMenu.GRID_SLOT_START,
                ArcaneWorktableMenu.PLAYER_INV_SLOT_START,
                ArcaneWorktableMenu.HOTBAR_SLOT_END - ArcaneWorktableMenu.PLAYER_INV_SLOT_START);
    }
}
