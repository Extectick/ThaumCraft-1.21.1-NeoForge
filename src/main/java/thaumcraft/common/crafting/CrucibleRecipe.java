package thaumcraft.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCRecipeSerializers;
import thaumcraft.common.registry.TCRecipeTypes;

public class CrucibleRecipe implements Recipe<CrucibleRecipe.Input> {
    private final String group;
    private final String research;
    private final Ingredient catalyst;
    private final AspectList aspects;
    private final ItemStack result;

    public CrucibleRecipe(String group, String research, Ingredient catalyst, AspectList aspects, ItemStack result) {
        this.group = group;
        this.research = research;
        this.catalyst = catalyst;
        this.aspects = aspects.copy();
        this.result = result;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.matches(input.aspects(), input.catalyst());
    }

    public boolean matches(AspectList containedAspects, ItemStack catalystStack) {
        if (!this.catalyst.test(catalystStack) || containedAspects == null) {
            return false;
        }
        for (Aspect aspect : this.aspects.getAspects()) {
            if (containedAspects.getAmount(aspect) < this.aspects.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    public AspectList removeMatching(AspectList containedAspects) {
        AspectList remaining = containedAspects.copy();
        for (Aspect aspect : this.aspects.getAspects()) {
            remaining.remove(aspect, this.aspects.getAmount(aspect));
        }
        return remaining;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.catalyst);
        return ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializers.CRUCIBLE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipeTypes.CRUCIBLE.get();
    }

    public String getResearch() {
        return this.research;
    }

    public Ingredient getCatalyst() {
        return this.catalyst;
    }

    public AspectList getAspects() {
        return this.aspects.copy();
    }

    public int matchWeight() {
        return this.aspects.size();
    }

    public record Input(ItemStack catalyst, AspectList aspects) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? this.catalyst : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(CrucibleRecipe::getGroup),
                Codec.STRING.optionalFieldOf("research", "").forGetter(CrucibleRecipe::getResearch),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(CrucibleRecipe::getCatalyst),
                AspectList.CODEC.fieldOf("aspects").forGetter(CrucibleRecipe::getAspects),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
                .apply(instance, CrucibleRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.of(
                CrucibleRecipe.Serializer::toNetwork, CrucibleRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CrucibleRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            AspectList aspects = AspectList.STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new CrucibleRecipe(group, research, catalyst, aspects, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CrucibleRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(recipe.research);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
            AspectList.STREAM_CODEC.encode(buffer, recipe.aspects);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}
