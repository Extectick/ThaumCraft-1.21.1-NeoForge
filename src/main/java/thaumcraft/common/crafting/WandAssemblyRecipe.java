package thaumcraft.common.crafting;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandParts;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCRecipeSerializers;

public class WandAssemblyRecipe extends ArcaneWorktableRecipe implements DynamicArcaneRecipe {
    private static final ShapedRecipePattern DUMMY_PATTERN = new ShapedRecipePattern(1, 1,
            NonNullList.withSize(1, Ingredient.EMPTY), Optional.empty());

    public WandAssemblyRecipe() {
        super("wand_assembly", DUMMY_PATTERN, ItemStack.EMPTY, PrimalVisStorage.EMPTY);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findAssembly(input).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return findAssembly(input)
                .map(assembly -> WandCastingItem.createVariant(TCItems.WAND_CASTING.get(), assembly.rod().tag(),
                        assembly.cap().tag(), assembly.sceptre()))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return WandCastingItem.createVariant(TCItems.WAND_CASTING.get(), WandParts.WOOD_ROD, WandParts.IRON_CAP, false);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public PrimalVisStorage getVisCost(CraftingInput input) {
        return findAssembly(input).map(assembly -> cost(assembly.cap(), assembly.rod(), assembly.sceptre()))
                .orElse(PrimalVisStorage.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializers.WAND_ASSEMBLY.get();
    }

    private static Optional<Assembly> findAssembly(CraftingInput input) {
        if (input.width() != 3 || input.height() != 3) {
            return Optional.empty();
        }

        Optional<Assembly> wand = findWand(input);
        if (wand.isPresent()) {
            return wand;
        }
        return findSceptre(input);
    }

    private static Optional<Assembly> findWand(CraftingInput input) {
        if (!empty(input, 0, 0) || !empty(input, 1, 0) || !empty(input, 0, 1)
                || !empty(input, 1, 2) || !empty(input, 2, 1) || !empty(input, 2, 2)) {
            return Optional.empty();
        }

        Optional<WandParts.Cap> capA = WandParts.capFrom(input.getItem(2, 0));
        Optional<WandParts.Cap> capB = WandParts.capFrom(input.getItem(0, 2));
        Optional<WandParts.Rod> rod = WandParts.rodFrom(input.getItem(1, 1));
        if (capA.isEmpty() || capB.isEmpty() || rod.isEmpty() || !capA.get().tag().equals(capB.get().tag())) {
            return Optional.empty();
        }
        return Optional.of(new Assembly(capA.get(), rod.get(), false));
    }

    private static Optional<Assembly> findSceptre(CraftingInput input) {
        if (!empty(input, 0, 0) || !empty(input, 0, 1) || !empty(input, 1, 2) || !empty(input, 2, 2)
                || !input.getItem(2, 0).is(TCItems.PRIMAL_CHARM.get())) {
            return Optional.empty();
        }

        Optional<WandParts.Cap> capA = WandParts.capFrom(input.getItem(1, 0));
        Optional<WandParts.Cap> capB = WandParts.capFrom(input.getItem(2, 1));
        Optional<WandParts.Cap> capC = WandParts.capFrom(input.getItem(0, 2));
        Optional<WandParts.Rod> rod = WandParts.rodFrom(input.getItem(1, 1));
        if (capA.isEmpty() || capB.isEmpty() || capC.isEmpty() || rod.isEmpty()
                || !capA.get().tag().equals(capB.get().tag()) || !capA.get().tag().equals(capC.get().tag())
                || rod.get().staff()) {
            return Optional.empty();
        }
        return Optional.of(new Assembly(capA.get(), rod.get(), true));
    }

    private static boolean empty(CraftingInput input, int x, int y) {
        return input.getItem(x, y).isEmpty();
    }

    private static PrimalVisStorage cost(WandParts.Cap cap, WandParts.Rod rod, boolean sceptre) {
        int cost = cap.craftCost() * rod.craftCost();
        if (sceptre) {
            cost = (int) (cost * 1.5F);
        }
        return new PrimalVisStorage(cost * 100, cost * 100, cost * 100, cost * 100, cost * 100, cost * 100);
    }

    private record Assembly(WandParts.Cap cap, WandParts.Rod rod, boolean sceptre) {
    }

    public static class Serializer implements RecipeSerializer<WandAssemblyRecipe> {
        private static final MapCodec<WandAssemblyRecipe> CODEC = MapCodec.unit(WandAssemblyRecipe::new);
        private static final StreamCodec<RegistryFriendlyByteBuf, WandAssemblyRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork);

        private static WandAssemblyRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new WandAssemblyRecipe();
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, WandAssemblyRecipe recipe) {
        }

        @Override
        public MapCodec<WandAssemblyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WandAssemblyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
