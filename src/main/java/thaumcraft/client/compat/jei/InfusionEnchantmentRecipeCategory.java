package thaumcraft.client.compat.jei;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.registry.TCBlocks;

public class InfusionEnchantmentRecipeCategory implements IRecipeCategory<RecipeHolder<InfusionEnchantmentRecipe>> {
    static final int WIDTH = 190;
    static final int HEIGHT = 190;

    private static final ResourceLocation RESEARCH_BACK = Thaumcraft.id("textures/gui/gui_researchback.png");
    private static final int CENTER_X = 80;
    private static final int CENTER_Y = 74;
    private static final int OUTPUT_X = 152;
    private static final int OUTPUT_Y = 66;
    private static final int ESSENTIA_X = 22;
    private static final int ESSENTIA_Y = 146;
    private static final int ESSENTIA_SIZE = 20;

    private final IDrawable background;
    private final IDrawable icon;

    public InfusionEnchantmentRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemLike(TCBlocks.RUNIC_MATRIX.get());
    }

    @Override
    public RecipeType<RecipeHolder<InfusionEnchantmentRecipe>> getRecipeType() {
        return ThaumcraftJeiPlugin.INFUSION_ENCHANTMENT;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumcraft.infusion_enchantment");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InfusionEnchantmentRecipe> holder,
            IFocusGroup focuses) {
        InfusionEnchantmentRecipe recipe = holder.value();
        NonNullList<Ingredient> components = recipe.getComponents();
        for (int i = 0; i < components.size(); i++) {
            Ingredient component = components.get(i);
            if (component.isEmpty()) {
                continue;
            }
            SlotPos pos = componentSlot(i, components.size());
            builder.addInputSlot(pos.x(), pos.y()).addIngredients(component);
        }

        builder.addOutputSlot(OUTPUT_X, OUTPUT_Y)
                .addItemStack(new ItemStack(Items.ENCHANTED_BOOK))
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(recipe.getEnchantment().value().description()));
    }

    @Override
    public void draw(RecipeHolder<InfusionEnchantmentRecipe> holder, IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics, double mouseX, double mouseY) {
        drawPanel(guiGraphics);
        drawComponentSlots(holder.value(), guiGraphics);
        Font font = Minecraft.getInstance().font;
        Component instability = Component.translatable("jei.thaumcraft.instability", holder.value().getInstability());
        drawCenteredString(guiGraphics, font, instability, 95, 10, 0xFFE6D6F1);
        Component xp = Component.translatable("jei.thaumcraft.xp", holder.value().getRecipeXp());
        drawCenteredString(guiGraphics, font, xp, 95, 22, 0xFFE6D6F1);
        Component enchantment = holder.value().getEnchantment().value().description();
        drawCenteredString(guiGraphics, font, enchantment, 95, 36, 0xFFE6D6F1);
        guiGraphics.drawString(font, Component.translatable("jei.thaumcraft.essentia"), 18, 131, 0xFFE6D6F1, false);
        renderEssentia(holder.value().getEssentia(), guiGraphics);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<InfusionEnchantmentRecipe> holder,
            IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        AspectList essentia = holder.value().getEssentia();
        List<Aspect> aspects = essentia.getAspectsSorted();
        for (int i = 0; i < aspects.size(); i++) {
            int x = ESSENTIA_X + i * 30;
            int y = ESSENTIA_Y;
            if (mouseX >= x && mouseX < x + ESSENTIA_SIZE && mouseY >= y && mouseY < y + ESSENTIA_SIZE) {
                Aspect aspect = aspects.get(i);
                tooltip.add(JeiAspectRenderer.aspectName(aspect));
                tooltip.add(Component.translatable("jei.thaumcraft.essentia_amount", essentia.getAmount(aspect)));
                return;
            }
        }
    }

    private static void renderEssentia(AspectList essentia, GuiGraphics guiGraphics) {
        List<Aspect> aspects = essentia.getAspectsSorted();
        for (int i = 0; i < aspects.size(); i++) {
            Aspect aspect = aspects.get(i);
            JeiAspectRenderer.renderAspect(guiGraphics, aspect, essentia.getAmount(aspect), ESSENTIA_X + i * 30,
                    ESSENTIA_Y, ESSENTIA_SIZE);
        }
    }

    private static SlotPos componentSlot(int index, int count) {
        if (count <= 0) {
            return new SlotPos(CENTER_X, CENTER_Y);
        }
        double angle = -Math.PI / 2.0D + (Math.PI * 2.0D * index / count);
        int x = CENTER_X + (int) Math.round(Math.cos(angle) * 45.0D);
        int y = CENTER_Y + (int) Math.round(Math.sin(angle) * 34.0D);
        return new SlotPos(x, y);
    }

    private static void drawPanel(GuiGraphics guiGraphics) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(RESEARCH_BACK, 0, 0, 112, 72, WIDTH, HEIGHT, 512, 512);
        RenderSystem.disableBlend();

        drawPanelBox(guiGraphics, 5, 0, 185, 178, 0xC20C0614, 0xFF6A4B79, 0xFF2B152F);
        drawPanelBox(guiGraphics, 12, 46, 145, 122, 0x8A1A0B24, 0xAA9A74AA, 0x558B5FA0);
        drawPanelBox(guiGraphics, 12, 140, 178, 172, 0xB00F0718, 0xAA9A74AA, 0x449700C0);

        guiGraphics.fill(CENTER_X - 44, CENTER_Y + 7, CENTER_X - 20, CENTER_Y + 9, 0x66C47BE7);
        guiGraphics.fill(CENTER_X + 20, CENTER_Y + 7, CENTER_X + 41, CENTER_Y + 9, 0x66C47BE7);
        guiGraphics.fill(CENTER_X + 7, CENTER_Y - 32, CENTER_X + 9, CENTER_Y - 20, 0x66C47BE7);
        guiGraphics.fill(CENTER_X + 7, CENTER_Y + 20, CENTER_X + 9, CENTER_Y + 32, 0x66C47BE7);
        drawCraftSlot(guiGraphics, OUTPUT_X - 5, OUTPUT_Y - 5, 0xC0D8B35A, 0x80E7C47B);
    }

    private static void drawComponentSlots(InfusionEnchantmentRecipe recipe, GuiGraphics guiGraphics) {
        NonNullList<Ingredient> components = recipe.getComponents();
        for (int i = 0; i < components.size(); i++) {
            if (!components.get(i).isEmpty()) {
                SlotPos pos = componentSlot(i, components.size());
                drawCraftSlot(guiGraphics, pos.x() - 5, pos.y() - 5, 0xAA8B5FA0, 0x558B5FA0);
            }
        }
    }

    private static void drawPanelBox(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int fill, int border,
            int innerLine) {
        guiGraphics.fill(x1, y1, x2, y2, fill);
        guiGraphics.fill(x1, y1, x2, y1 + 1, border);
        guiGraphics.fill(x1, y2 - 1, x2, y2, border);
        guiGraphics.fill(x1, y1, x1 + 1, y2, border);
        guiGraphics.fill(x2 - 1, y1, x2, y2, border);
        guiGraphics.fill(x1 + 3, y1 + 3, x2 - 3, y1 + 4, innerLine);
        guiGraphics.fill(x1 + 3, y2 - 4, x2 - 3, y2 - 3, innerLine);
    }

    private static void drawCraftSlot(GuiGraphics guiGraphics, int x, int y, int border, int glow) {
        guiGraphics.fill(x - 2, y - 2, x + 28, y + 28, 0x33000000);
        guiGraphics.fill(x, y, x + 26, y + 26, glow);
        guiGraphics.fill(x + 2, y + 2, x + 24, y + 24, 0xD016071F);
        guiGraphics.fill(x + 2, y + 2, x + 24, y + 3, border);
        guiGraphics.fill(x + 2, y + 23, x + 24, y + 24, border);
        guiGraphics.fill(x + 2, y + 2, x + 3, y + 24, border);
        guiGraphics.fill(x + 23, y + 2, x + 24, y + 24, border);
        guiGraphics.fill(x + 5, y + 5, x + 21, y + 21, 0xB024112A);
    }

    private static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int centerX, int y,
            int color) {
        guiGraphics.drawString(font, text, centerX - font.width(text) / 2, y, color, false);
    }

    private record SlotPos(int x, int y) {
    }
}
