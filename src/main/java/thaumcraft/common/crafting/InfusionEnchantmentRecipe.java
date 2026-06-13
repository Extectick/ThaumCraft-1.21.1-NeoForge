package thaumcraft.common.crafting;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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

public class InfusionEnchantmentRecipe implements Recipe<InfusionEnchantmentRecipe.Input> {
    private final String group;
    private final String research;
    private final Holder<Enchantment> enchantment;
    private final NonNullList<Ingredient> components;
    private final int instability;
    private final AspectList essentia;
    private final int recipeXp;

    public InfusionEnchantmentRecipe(String group, String research, Holder<Enchantment> enchantment,
            List<Ingredient> components, int instability, AspectList essentia) {
        this.group = group;
        this.research = research;
        this.enchantment = enchantment;
        this.components = NonNullList.copyOf(components);
        this.instability = Math.max(0, instability);
        this.essentia = essentia.copy();
        this.recipeXp = Math.max(1, enchantment.value().getMinCost(1) / 3);
    }

    @Override
    public boolean matches(Input input, Level level) {
        ItemStack catalyst = input.catalyst();
        if (catalyst.isEmpty() || !catalyst.supportsEnchantment(this.enchantment)) {
            return false;
        }

        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(catalyst);
        int currentLevel = enchantments.getLevel(this.enchantment);
        if (currentLevel >= this.enchantment.value().getMaxLevel()) {
            return false;
        }

        for (Holder<Enchantment> existing : enchantments.keySet()) {
            if (!existing.equals(this.enchantment) && !Enchantment.areCompatible(this.enchantment, existing)) {
                return false;
            }
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
        ItemStack output = input.catalyst().copy();
        int level = this.currentLevel(output) + 1;
        EnchantmentHelper.updateEnchantments(output, enchantments -> enchantments.set(this.enchantment, level));
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.components;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializers.INFUSION_ENCHANTMENT.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipeTypes.INFUSION_ENCHANTMENT.get();
    }

    public String getResearch() {
        return this.research;
    }

    public Holder<Enchantment> getEnchantment() {
        return this.enchantment;
    }

    public ResourceLocation getEnchantmentId() {
        return this.enchantment.unwrapKey()
                .map(ResourceKey::location)
                .orElse(ResourceLocation.withDefaultNamespace("empty"));
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

    public int getRecipeXp() {
        return this.recipeXp;
    }

    public int calcInstability(ItemStack recipeInput) {
        int enchantmentLevels = 0;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(recipeInput).entrySet()) {
            enchantmentLevels += entry.getIntValue();
        }
        return enchantmentLevels / 2 + this.instability;
    }

    public int calcXp(ItemStack recipeInput) {
        return this.recipeXp * (1 + this.currentLevel(recipeInput));
    }

    public AspectList getScaledEssentia(ItemStack recipeInput) {
        AspectList scaled = this.essentia.copy();
        float mod = this.currentLevel(recipeInput);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(recipeInput).entrySet()) {
            if (!entry.getKey().equals(this.enchantment)) {
                mod += entry.getIntValue() * 0.1F;
            }
        }
        for (Aspect aspect : scaled.getAspects()) {
            scaled.add(aspect, (int) (scaled.getAmount(aspect) * mod));
        }
        return scaled;
    }

    private int currentLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(this.enchantment);
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

    public static class Serializer implements RecipeSerializer<InfusionEnchantmentRecipe> {
        public static final MapCodec<InfusionEnchantmentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(InfusionEnchantmentRecipe::getGroup),
                Codec.STRING.optionalFieldOf("research", "").forGetter(InfusionEnchantmentRecipe::getResearch),
                Enchantment.CODEC.fieldOf("enchantment").forGetter(InfusionEnchantmentRecipe::getEnchantment),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("components").forGetter(InfusionEnchantmentRecipe::getComponents),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(InfusionEnchantmentRecipe::getInstability),
                AspectList.CODEC.optionalFieldOf("essentia", AspectList.EMPTY)
                        .forGetter(InfusionEnchantmentRecipe::getEssentia))
                .apply(instance, InfusionEnchantmentRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionEnchantmentRecipe> STREAM_CODEC = StreamCodec.of(
                InfusionEnchantmentRecipe.Serializer::toNetwork, InfusionEnchantmentRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<InfusionEnchantmentRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfusionEnchantmentRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static InfusionEnchantmentRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            Holder<Enchantment> enchantment = Enchantment.STREAM_CODEC.decode(buffer);
            int componentCount = buffer.readVarInt();
            List<Ingredient> components = new ArrayList<>(componentCount);
            for (int i = 0; i < componentCount; i++) {
                components.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            int instability = buffer.readVarInt();
            AspectList essentia = AspectList.STREAM_CODEC.decode(buffer);
            return new InfusionEnchantmentRecipe(group, research, enchantment, components, instability, essentia);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, InfusionEnchantmentRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(recipe.research);
            Enchantment.STREAM_CODEC.encode(buffer, recipe.enchantment);
            buffer.writeVarInt(recipe.components.size());
            for (Ingredient component : recipe.components) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, component);
            }
            buffer.writeVarInt(recipe.instability);
            AspectList.STREAM_CODEC.encode(buffer, recipe.essentia);
        }
    }
}
