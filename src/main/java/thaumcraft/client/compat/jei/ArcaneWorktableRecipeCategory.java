package thaumcraft.client.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public class ArcaneWorktableRecipeCategory implements IRecipeCategory<RecipeHolder<ArcaneWorktableRecipe>> {
    static final int WIDTH = 190;
    static final int HEIGHT = 142;
    private static final ResourceLocation BACKGROUND = Thaumcraft.id("textures/gui/gui_arcaneworkbench.png");
    private static final int[][] ASPECT_LOCS = {
            { 72, 21 },
            { 24, 43 },
            { 24, 102 },
            { 72, 124 },
            { 120, 102 },
            { 120, 43 }
    };

    private final IDrawable background;
    private final IDrawable icon;

    public ArcaneWorktableRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, WIDTH, HEIGHT)
                .setTextureSize(256, 256)
                .build();
        this.icon = guiHelper.createDrawableItemLike(TCBlocks.ARCANE_WORKTABLE.get());
    }

    @Override
    public RecipeType<RecipeHolder<ArcaneWorktableRecipe>> getRecipeType() {
        return ThaumcraftJeiPlugin.ARCANE_WORKTABLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumcraft.arcane_worktable");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ArcaneWorktableRecipe> holder,
            IFocusGroup focuses) {
        ArcaneWorktableRecipe recipe = holder.value();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int left = 40;
        int top = 40;

        for (int row = 0; row < recipe.getHeight(); row++) {
            for (int column = 0; column < recipe.getWidth(); column++) {
                int index = column + row * recipe.getWidth();
                if (index >= ingredients.size()) {
                    continue;
                }
                Ingredient ingredient = ingredients.get(index);
                if (!ingredient.isEmpty()) {
                    builder.addInputSlot(left + column * 24, top + row * 24)
                            .addIngredients(ingredient);
                }
            }
        }

        builder.addInputSlot(160, 24)
                .addItemStack(new ItemStack(TCItems.WAND_CASTING.get()))
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.thaumcraft.wand_hint")));

        ItemStack result = resultFor(recipe);
        if (!result.isEmpty()) {
            builder.addOutputSlot(160, 64)
                    .addItemStack(result);
        }
    }

    @Override
    public void draw(RecipeHolder<ArcaneWorktableRecipe> holder, IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics, double mouseX, double mouseY) {
        renderVisCost(holder.value().getVisCost(), guiGraphics);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<ArcaneWorktableRecipe> holder,
            IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        PrimalVisStorage vis = holder.value().getVisCost();
        for (int i = 0; i < Aspect.getPrimalAspects().size(); i++) {
            Aspect aspect = Aspect.getPrimalAspects().get(i);
            int amount = vis.get(aspect);
            int x = ASPECT_LOCS[i][0] - 8;
            int y = ASPECT_LOCS[i][1] - 8;
            if (amount > 0 && mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                tooltip.add(JeiAspectRenderer.aspectName(aspect));
                tooltip.add(Component.translatable("jei.thaumcraft.vis_cost", amount / 100.0D));
                return;
            }
        }
    }

    private static void renderVisCost(PrimalVisStorage vis, GuiGraphics guiGraphics) {
        if (vis.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Component.translatable("jei.thaumcraft.no_vis_cost"), 52, 122, 0xFF404040,
                    false);
            return;
        }

        int index = 0;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int amount = vis.get(aspect);
            if (amount > 0) {
                JeiAspectRenderer.renderVisTag(guiGraphics, aspect, amount, ASPECT_LOCS[index][0] - 8,
                        ASPECT_LOCS[index][1] - 8);
            }
            index++;
        }
    }

    private static ItemStack resultFor(ArcaneWorktableRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? ItemStack.EMPTY : recipe.getResultItem(minecraft.level.registryAccess()).copy();
    }
}
