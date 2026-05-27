package thaumcraft.common.crafting;

import java.util.ArrayList;
import java.util.List;

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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCRecipeSerializers;
import thaumcraft.common.registry.TCRecipeTypes;

public class InfusionRecipe implements Recipe<InfusionRecipe.Input> {
    private final String group;
    private final String research;
    private final Ingredient catalyst;
    private final NonNullList<Ingredient> components;
    private final ItemStack result;
    private final int instability;
    private final AspectList essentia;

    public InfusionRecipe(String group, String research, Ingredient catalyst, List<Ingredient> components,
            ItemStack result, int instability, AspectList essentia) {
        this.group = group;
        this.research = research;
        this.catalyst = catalyst;
        this.components = NonNullList.copyOf(components);
        this.result = result;
        this.instability = Math.max(0, instability);
        this.essentia = essentia.copy();
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!this.catalyst.test(input.catalyst())) {
            return false;
        }

        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack component : input.components()) {
            if (!component.isEmpty()) {
                remaining.add(component.copy());
            }
        }

        for (Ingredient component : this.components) {
            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (component.test(remaining.get(i))) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return remaining.isEmpty();
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
        ingredients.addAll(this.components);
        return ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializers.INFUSION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipeTypes.INFUSION.get();
    }

    public String getResearch() {
        return this.research;
    }

    public Ingredient getCatalyst() {
        return this.catalyst;
    }

    public NonNullList<Ingredient> getComponents() {
        return this.components;
    }

    public int getInstability() {
        return this.instability;
    }

    public AspectList getEssentia() {
        return this.essentia.copy();
    }

    public record Input(ItemStack catalyst, List<ItemStack> components) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            if (index == 0) {
                return this.catalyst;
            }
            int componentIndex = index - 1;
            return componentIndex >= 0 && componentIndex < this.components.size()
                    ? this.components.get(componentIndex)
                    : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1 + this.components.size();
        }
    }

    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(InfusionRecipe::getGroup),
                Codec.STRING.optionalFieldOf("research", "").forGetter(InfusionRecipe::getResearch),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(InfusionRecipe::getCatalyst),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("components").forGetter(InfusionRecipe::getComponents),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(InfusionRecipe::getInstability),
                AspectList.CODEC.optionalFieldOf("essentia", AspectList.EMPTY)
                        .forGetter(InfusionRecipe::getEssentia))
                .apply(instance, InfusionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(
                InfusionRecipe.Serializer::toNetwork, InfusionRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<InfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static InfusionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int componentCount = buffer.readVarInt();
            List<Ingredient> components = new ArrayList<>(componentCount);
            for (int i = 0; i < componentCount; i++) {
                components.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int instability = buffer.readVarInt();
            AspectList essentia = AspectList.STREAM_CODEC.decode(buffer);
            return new InfusionRecipe(group, research, catalyst, components, result, instability, essentia);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, InfusionRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(recipe.research);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
            buffer.writeVarInt(recipe.components.size());
            for (Ingredient component : recipe.components) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, component);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeVarInt(recipe.instability);
            AspectList.STREAM_CODEC.encode(buffer, recipe.essentia);
        }
    }
}
