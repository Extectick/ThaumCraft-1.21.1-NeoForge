package thaumcraft.common.research;

import java.util.List;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

public record ResearchPage(PageType type, String textKey, Optional<String> concealedResearch,
        Optional<ResourceLocation> image, List<Aspect> aspects, List<ResourceLocation> recipeIds,
        Optional<ItemStack> recipeOutput) {
    public ResearchPage {
        concealedResearch = concealedResearch == null ? Optional.empty() : concealedResearch;
        image = image == null ? Optional.empty() : image;
        aspects = List.copyOf(aspects);
        recipeIds = List.copyOf(recipeIds);
        recipeOutput = recipeOutput == null ? Optional.empty() : recipeOutput.map(ItemStack::copy);
    }

    public static ResearchPage text(String textKey) {
        return new ResearchPage(PageType.TEXT, textKey, Optional.empty(), Optional.empty(), List.of(), List.of(),
                Optional.empty());
    }

    public static ResearchPage concealedText(String requiredResearch, String textKey) {
        return new ResearchPage(PageType.TEXT_CONCEALED, textKey, Optional.of(requiredResearch), Optional.empty(),
                List.of(), List.of(), Optional.empty());
    }

    public static ResearchPage image(ResourceLocation image, String captionKey) {
        return new ResearchPage(PageType.IMAGE, captionKey, Optional.empty(), Optional.of(image), List.of(), List.of(),
                Optional.empty());
    }

    public static ResearchPage aspects(List<Aspect> aspects) {
        return new ResearchPage(PageType.ASPECTS, "", Optional.empty(), Optional.empty(), aspects, List.of(),
                Optional.empty());
    }

    public static ResearchPage normalCrafting(ResourceLocation recipeId) {
        return recipe(PageType.NORMAL_CRAFTING, recipeId);
    }

    public static ResearchPage arcaneCrafting(ResourceLocation recipeId) {
        return recipe(PageType.ARCANE_CRAFTING, recipeId);
    }

    public static ResearchPage crucibleCrafting(ResourceLocation recipeId) {
        return recipe(PageType.CRUCIBLE_CRAFTING, recipeId);
    }

    public static ResearchPage infusionCrafting(ResourceLocation recipeId) {
        return recipe(PageType.INFUSION_CRAFTING, recipeId);
    }

    public static ResearchPage infusionEnchantment(ResourceLocation recipeId) {
        return recipe(PageType.INFUSION_ENCHANTMENT, recipeId);
    }

    public static ResearchPage smelting(ResourceLocation recipeId) {
        return recipe(PageType.SMELTING, recipeId);
    }

    public static ResearchPage compoundCrafting(List<ResourceLocation> recipeIds) {
        return new ResearchPage(PageType.COMPOUND_CRAFTING, "", Optional.empty(), Optional.empty(), List.of(),
                recipeIds, Optional.empty());
    }

    public static ResearchPage recipe(ResourceLocation recipeId) {
        return normalCrafting(recipeId);
    }

    public static ResearchPage textAndRecipe(String textKey, ResourceLocation recipeId) {
        return new ResearchPage(PageType.TEXT_AND_RECIPE, textKey, Optional.empty(), Optional.empty(), List.of(),
                List.of(recipeId), Optional.empty());
    }

    private static ResearchPage recipe(PageType type, ResourceLocation recipeId) {
        return new ResearchPage(type, "", Optional.empty(), Optional.empty(), List.of(), List.of(recipeId),
                Optional.empty());
    }

    public boolean isConcealedFor(ResearchKnowledgeData knowledge) {
        return this.concealedResearch.isPresent() && !knowledge.isComplete(this.concealedResearch.get());
    }

    public enum PageType {
        TEXT,
        TEXT_CONCEALED,
        IMAGE,
        CRUCIBLE_CRAFTING,
        ARCANE_CRAFTING,
        ASPECTS,
        NORMAL_CRAFTING,
        INFUSION_CRAFTING,
        COMPOUND_CRAFTING,
        INFUSION_ENCHANTMENT,
        SMELTING,
        TEXT_AND_RECIPE
    }
}
