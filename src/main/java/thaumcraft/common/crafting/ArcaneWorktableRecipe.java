package thaumcraft.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.registry.TCRecipeSerializers;
import thaumcraft.common.registry.TCRecipeTypes;

public class ArcaneWorktableRecipe implements Recipe<CraftingInput> {
    private final String group;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;
    private final PrimalVisStorage visCost;

    public ArcaneWorktableRecipe(String group, ShapedRecipePattern pattern, ItemStack result, PrimalVisStorage visCost) {
        this.group = group;
        this.pattern = pattern;
        this.result = result;
        this.visCost = visCost;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.pattern.width() && height >= this.pattern.height();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializers.ARCANE_WORKTABLE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipeTypes.ARCANE_WORKTABLE.get();
    }

    public PrimalVisStorage getVisCost() {
        return this.visCost;
    }

    public static class Serializer implements RecipeSerializer<ArcaneWorktableRecipe> {
        public static final MapCodec<ArcaneWorktableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ArcaneWorktableRecipe::getGroup),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                PrimalVisStorage.CODEC.optionalFieldOf("vis", PrimalVisStorage.EMPTY)
                        .forGetter(ArcaneWorktableRecipe::getVisCost))
                .apply(instance, ArcaneWorktableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ArcaneWorktableRecipe> STREAM_CODEC = StreamCodec.of(
                ArcaneWorktableRecipe.Serializer::toNetwork, ArcaneWorktableRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<ArcaneWorktableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArcaneWorktableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ArcaneWorktableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            PrimalVisStorage visCost = PrimalVisStorage.STREAM_CODEC.decode(buffer);
            return new ArcaneWorktableRecipe(group, pattern, result, visCost);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ArcaneWorktableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            PrimalVisStorage.STREAM_CODEC.encode(buffer, recipe.visCost);
        }
    }
}
