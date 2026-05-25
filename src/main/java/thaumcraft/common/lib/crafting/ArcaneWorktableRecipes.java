package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.NonNullList;
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
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandVisHelper;
import thaumcraft.common.registry.TCRecipeTypes;

public final class ArcaneWorktableRecipes {
    private ArcaneWorktableRecipes() {
    }

    public static boolean tryCraft(Level level, ArcaneWorktableBlockEntity worktable, ItemStack wand, Player player) {
        WorktableInput worktableInput = createInput(worktable);
        Optional<RecipeHolder<ArcaneWorktableRecipe>> recipeHolder = findRecipe(level, worktableInput.input());
        return recipeHolder.isPresent() && tryCraft(level, worktableInput.input(), worktable, wand, player,
                recipeHolder.get().value());
    }

    public static Optional<RecipeHolder<ArcaneWorktableRecipe>> findRecipe(Level level, CraftingInput input) {
        return level.getRecipeManager().getRecipeFor(TCRecipeTypes.ARCANE_WORKTABLE.get(), input, level);
    }

    public static Optional<RecipeHolder<CraftingRecipe>> findVanillaRecipe(Level level, CraftingInput input) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
    }

    public static boolean tryCraft(Level level, CraftingInput input, Container worktable, ItemStack wand, Player player,
            ArcaneWorktableRecipe recipe) {
        if (!tryConsumeArcaneCraft(level, input, worktable, wand, player, recipe)) {
            return false;
        }

        ItemStack result = recipe.assemble(input, level.registryAccess());
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
        return true;
    }

    public static boolean tryConsumeArcaneCraft(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        WorktableInput worktableInput = createInput(worktable);
        if (!worktableInput.input().equals(input)) {
            return false;
        }

        Optional<RecipeHolder<ArcaneWorktableRecipe>> recipeHolder = level.getRecipeManager()
                .getRecipeFor(TCRecipeTypes.ARCANE_WORKTABLE.get(), worktableInput.input(), level);
        if (recipeHolder.isEmpty() || recipeHolder.get().value() != recipe || !hasPrimalCost(wand, player, recipe)) {
            return false;
        }

        consumePrimalCost(wand, player, recipe);
        consumeIngredients(worktable, worktableInput, recipe, player);
        worktable.setChanged();
        return true;
    }

    public static boolean tryConsumeVanillaCraft(Level level, CraftingInput input, Container worktable, Player player,
            CraftingRecipe recipe) {
        WorktableInput worktableInput = createInput(worktable);
        if (!worktableInput.input().equals(input)) {
            return false;
        }

        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = findVanillaRecipe(level, worktableInput.input());
        if (recipeHolder.isEmpty() || recipeHolder.get().value() != recipe) {
            return false;
        }

        consumeIngredients(worktable, worktableInput, recipe, player);
        worktable.setChanged();
        return true;
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
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (!WandVisHelper.hasEnoughVis(wand, aspect, effectivePrimalCost(wand, player, recipe, aspect))) {
                return false;
            }
        }
        return true;
    }

    public static int effectivePrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe, Aspect aspect) {
        int cost = recipe.getVisCost().get(aspect);
        if (cost <= 0) {
            return 0;
        }
        float modifier = 1.0F;
        if (wand.getItem() instanceof WandCastingItem castingItem) {
            modifier = castingItem.getConsumptionModifier(wand, player, aspect, true);
        }
        return Math.max(0, (int)(cost * modifier));
    }

    private static void consumePrimalCost(ItemStack wand, Player player, ArcaneWorktableRecipe recipe) {
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            WandVisHelper.consumeVis(wand, aspect, effectivePrimalCost(wand, player, recipe, aspect));
        }
    }

    private static void consumeIngredients(Container worktable, WorktableInput worktableInput,
            net.minecraft.world.item.crafting.Recipe<CraftingInput> recipe, Player player) {
        CraftingInput input = worktableInput.input();
        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(input);
        for (int y = 0; y < input.height(); y++) {
            for (int x = 0; x < input.width(); x++) {
                int inputSlot = x + y * input.width();
                int worktableSlot = x + worktableInput.left() + (y + worktableInput.top()) * 3;
                worktable.removeItem(worktableSlot, 1);
                ItemStack remaining = remainingItems.get(inputSlot);
                if (!remaining.isEmpty()) {
                    ItemStack current = worktable.getItem(worktableSlot);
                    if (current.isEmpty()) {
                        worktable.setItem(worktableSlot, remaining);
                    } else if (!player.getInventory().add(remaining)) {
                        player.drop(remaining, false);
                    }
                }
            }
        }
    }

    public record WorktableInput(CraftingInput input, int left, int top) {
    }
}
