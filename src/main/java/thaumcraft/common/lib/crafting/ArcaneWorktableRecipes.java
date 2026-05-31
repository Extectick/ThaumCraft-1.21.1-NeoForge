package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.crafting.DynamicArcaneRecipe;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.common.util.ServerArcaneWorktableHooks;

public final class ArcaneWorktableRecipes {
    private ArcaneWorktableRecipes() {
    }

    public static boolean tryCraft(Level level, ArcaneWorktableBlockEntity worktable, ItemStack wand, Player player) {
        return ServerArcaneWorktableHooks.tryCraft(level, worktable, wand, player);
    }

    public static Optional<RecipeHolder<ArcaneWorktableRecipe>> findRecipe(Level level, CraftingInput input) {
        return level.getRecipeManager().getRecipeFor(TCRecipeTypes.ARCANE_WORKTABLE.get(), input, level);
    }

    public static Optional<RecipeHolder<CraftingRecipe>> findVanillaRecipe(Level level, CraftingInput input) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
    }

    public static boolean tryCraft(Level level, CraftingInput input, Container worktable, ItemStack wand, Player player,
            ArcaneWorktableRecipe recipe) {
        return ServerArcaneWorktableHooks.tryCraft(level, input, worktable, wand, player, recipe);
    }

    public static boolean tryConsumeArcaneCraft(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return ServerArcaneWorktableHooks.tryConsumeArcaneCraft(level, input, worktable, wand, player, recipe);
    }

    public static boolean tryConsumeVanillaCraft(Level level, CraftingInput input, Container worktable, Player player,
            CraftingRecipe recipe) {
        return ServerArcaneWorktableHooks.tryConsumeVanillaCraft(level, input, worktable, player, recipe);
    }

    public static WorktableInput createInput(Container worktable) {
        List<ItemStack> grid = new ArrayList<>(ArcaneWorktableBlockEntity.GRID_SIZE);
        for (int slot = 0; slot < ArcaneWorktableBlockEntity.GRID_SIZE; slot++) {
            grid.add(worktable.getItem(slot));
        }
        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(3, 3, grid);
        return new WorktableInput(positioned.input(), positioned.left(), positioned.top());
    }

    public static boolean hasPrimalCost(ItemStack wand, ArcaneWorktableRecipe recipe) {
        return hasPrimalCost(wand, null, recipe);
    }

    public static boolean hasPrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe) {
        return hasPrimalCost(wand, player, recipe, null);
    }

    public static boolean hasPrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe, CraftingInput input) {
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (!WandVisHelper.hasEnoughVis(wand, aspect, effectivePrimalCost(wand, player, recipe, input, aspect))) {
                return false;
            }
        }
        return true;
    }

    public static int effectivePrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe, Aspect aspect) {
        return effectivePrimalCost(wand, player, recipe, null, aspect);
    }

    public static int effectivePrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe,
            CraftingInput input, Aspect aspect) {
        int cost = getVisCost(recipe, input).get(aspect);
        if (cost <= 0) {
            return 0;
        }
        float modifier = 1.0F;
        if (wand.getItem() instanceof WandCastingItem castingItem) {
            modifier = castingItem.getConsumptionModifier(wand, player, aspect, true);
        }
        return Math.max(0, (int)(cost * modifier));
    }

    public static thaumcraft.api.aspects.PrimalVisStorage getVisCost(ArcaneWorktableRecipe recipe, CraftingInput input) {
        if (input != null && recipe instanceof DynamicArcaneRecipe dynamicRecipe) {
            return dynamicRecipe.getVisCost(input);
        }
        return recipe.getVisCost();
    }

    public record WorktableInput(CraftingInput input, int left, int top) {
    }
}
