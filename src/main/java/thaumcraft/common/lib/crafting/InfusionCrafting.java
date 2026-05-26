package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCRecipeTypes;
import thaumcraft.common.registry.TCSoundEvents;

public final class InfusionCrafting {
    private InfusionCrafting() {
    }

    public static InteractionResult tryStart(Level level, BlockPos pos, Player player) {
        if (!level.getBlockState(pos).is(TCBlocks.RUNIC_MATRIX.get())) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof RunicMatrixBlockEntity matrix)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!matrix.isActive()) {
            return InteractionResult.PASS;
        }

        if (matrix.isCrafting()) {
            return InteractionResult.CONSUME;
        }

        if (!matrix.refreshSurroundingsNow()) {
            matrix.setActive(false);
            return InteractionResult.CONSUME;
        }

        if (!(level.getBlockEntity(pos.below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)) {
            matrix.setActive(false);
            return InteractionResult.CONSUME;
        }

        ItemStack catalyst = centerPedestal.getStoredItem();
        if (catalyst.isEmpty()) {
            return InteractionResult.CONSUME;
        }

        List<ItemStack> components = collectComponents(level, matrix.getPedestals());
        if (components.isEmpty()) {
            return InteractionResult.CONSUME;
        }

        InfusionRecipe.Input input = new InfusionRecipe.Input(catalyst.copy(), components);
        Optional<RecipeHolder<InfusionRecipe>> recipe = level.getRecipeManager()
                .getAllRecipesFor(TCRecipeTypes.INFUSION.get())
                .stream()
                .filter(holder -> holder.value().matches(input, level))
                .findFirst();

        if (recipe.isEmpty()) {
            if (player != null) {
                player.displayClientMessage(Component.literal("No matching infusion recipe"), true);
            }
            return InteractionResult.CONSUME;
        }

        matrix.startCrafting(player, recipe.get(), input);
        level.playSound(null, pos, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        return InteractionResult.CONSUME;
    }

    private static List<ItemStack> collectComponents(Level level, List<BlockPos> pedestals) {
        List<ItemStack> components = new ArrayList<>();
        for (BlockPos pedestalPos : pedestals) {
            if (level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal) {
                ItemStack stack = pedestal.getStoredItem();
                if (!stack.isEmpty()) {
                    components.add(stack.copy());
                }
            }
        }
        return components;
    }
}
