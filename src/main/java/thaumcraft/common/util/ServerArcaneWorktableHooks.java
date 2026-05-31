package thaumcraft.common.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.services.ServerServices;

public final class ServerArcaneWorktableHooks {
    private ServerArcaneWorktableHooks() {
    }

    public static boolean tryCraft(Level level, ArcaneWorktableBlockEntity worktable, ItemStack wand, Player player) {
        return ServerServices.get().tryCraftArcaneWorktable(level, worktable, wand, player);
    }

    public static boolean tryCraft(Level level, CraftingInput input, Container worktable, ItemStack wand, Player player,
            ArcaneWorktableRecipe recipe) {
        return ServerServices.get().tryCraftArcaneWorktable(level, input, worktable, wand, player, recipe);
    }

    public static boolean tryConsumeArcaneCraft(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return ServerServices.get().tryConsumeArcaneCraft(level, input, worktable, wand, player, recipe);
    }

    public static boolean tryConsumeVanillaCraft(Level level, CraftingInput input, Container worktable, Player player,
            CraftingRecipe recipe) {
        return ServerServices.get().tryConsumeVanillaCraft(level, input, worktable, player, recipe);
    }
}
