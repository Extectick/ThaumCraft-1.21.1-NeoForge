package thaumictinkerer.common.recipes;


import net.minecraft.core.NonNullList;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import thaumictinkerer.common.registry.TTItems;
import thaumictinkerer.common.registry.TTRecipeSerializers;

public class SpellClothRecipe extends CustomRecipe {
    public SpellClothRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundCloth = false;
        boolean foundEnchantedItem = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(TTItems.SPELL_CLOTH.get())) {
                    if (foundCloth) return false;
                    foundCloth = true;
                } else if (stack.isEnchanted() || stack.has(DataComponents.STORED_ENCHANTMENTS)) {
                    if (foundEnchantedItem) return false;
                    foundEnchantedItem = true;
                } else {
                    return false;
                }
            }
        }

        return foundCloth && foundEnchantedItem;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        ItemStack enchantedItem = ItemStack.EMPTY;
        ItemStack cloth = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(TTItems.SPELL_CLOTH.get())) {
                    cloth = stack;
                } else {
                    enchantedItem = stack;
                }
            }
        }

        if (enchantedItem.isEmpty() || cloth.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = enchantedItem.copy();

        int levelsToRemove = 0;
        ItemEnchantments enchantments = result.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchantments.entrySet()) {
            levelsToRemove += entry.getIntValue();
        }
        result.remove(DataComponents.ENCHANTMENTS);
        result.remove(DataComponents.STORED_ENCHANTMENTS);
        
        return result;
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.is(TTItems.SPELL_CLOTH.get())) {
                ItemStack cloth = stack.copy();
                cloth.setCount(1);

                int damageToApply = 0;
                for (int j = 0; j < input.size(); j++) {
                    ItemStack other = input.getItem(j);
                    if (!other.isEmpty() && !other.is(TTItems.SPELL_CLOTH.get())) {
                        ItemEnchantments enchantments = other.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                        for (var entry : enchantments.entrySet()) {
                            damageToApply += entry.getIntValue();
                        }
                        ItemEnchantments stored = other.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                        for (var entry : stored.entrySet()) {
                            damageToApply += entry.getIntValue();
                        }
                    }
                }
                
                cloth.setDamageValue(cloth.getDamageValue() + damageToApply);
                if (cloth.getDamageValue() < cloth.getMaxDamage()) {
                    remaining.set(i, cloth);
                }
            } else if (stack.hasCraftingRemainingItem()) {
                remaining.set(i, stack.getCraftingRemainingItem());
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TTRecipeSerializers.SPELL_CLOTH_RECIPE.get();
    }
}




