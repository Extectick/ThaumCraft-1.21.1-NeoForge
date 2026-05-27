package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.registry.TCDataComponents;

public final class ObjectAspectRegistry {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "thaumcraft/item_aspects";
    private static final int MAX_GENERATION_PASSES = 12;
    private static final Map<Item, AspectList> ITEM_ASPECTS = new LinkedHashMap<>();
    private static final Map<Item, AspectList> GENERATED_ASPECTS = new LinkedHashMap<>();
    private static final Map<Item, AspectList> TAG_ITEM_ASPECTS = new LinkedHashMap<>();
    private static final List<TagEntry> TAG_ASPECTS = new ArrayList<>();

    private ObjectAspectRegistry() {
    }

    public static void registerReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener(event.getServerResources().getRecipeManager(), event.getRegistryAccess()));
    }

    public static AspectList getObjectTags(ItemStack stack) {
        if (stack.isEmpty()) {
            return AspectList.EMPTY;
        }

        AspectList itemAspects = ITEM_ASPECTS.get(stack.getItem());
        if (itemAspects != null) {
            return withPotionAspects(stack, itemAspects.copy());
        }

        AspectList tagAspects = TAG_ITEM_ASPECTS.get(stack.getItem());
        if (tagAspects != null) {
            return withPotionAspects(stack, tagAspects.copy());
        }

        AspectList generatedAspects = GENERATED_ASPECTS.get(stack.getItem());
        AspectList aspects = generatedAspects != null ? generatedAspects.copy() : AspectList.EMPTY;
        return withPotionAspects(stack, aspects);
    }

    public static AspectList getObjectTagsWithBonus(ItemStack stack) {
        return getBonusTags(stack, getObjectTags(stack));
    }

    public static AspectList getBonusTags(ItemStack stack, AspectList sourceTags) {
        if (stack.isEmpty()) {
            return AspectList.EMPTY;
        }

        AspectList output = new AspectList();
        addContainedEssentia(stack, output);
        if (sourceTags != null) {
            output.add(sourceTags);
        }

        Item item = stack.getItem();
        if (item instanceof ArmorItem armor) {
            output.merge(Aspect.ARMOR, armor.getMaterial().value().getDefense(armor.getType()));
        } else if (item instanceof SwordItem sword) {
            int weapon = Math.max(1, (int) (sword.getTier().getAttackDamageBonus() + 1.0F));
            output.merge(Aspect.WEAPON, weapon);
        } else if (item instanceof BowItem) {
            output.merge(Aspect.WEAPON, 3).merge(Aspect.FLIGHT, 1);
        } else if (item instanceof PickaxeItem pickaxe) {
            output.merge(Aspect.MINE, harvestLevel(pickaxe.getTier()) + 1);
        } else if (item instanceof DiggerItem digger) {
            output.merge(Aspect.TOOL, harvestLevel(digger.getTier()) + 1);
        } else if (item instanceof ShearsItem || item instanceof HoeItem) {
            output.merge(Aspect.HARVEST, harvestTierByDurability(stack));
        }

        addEnchantments(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), output);
        addEnchantments(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), output);
        return cullTags(output);
    }

    public static int itemEntryCount() {
        return ITEM_ASPECTS.size();
    }

    public static int tagEntryCount() {
        return TAG_ASPECTS.size();
    }

    public static int generatedEntryCount() {
        return GENERATED_ASPECTS.size();
    }

    private static void reload(Map<ResourceLocation, JsonElement> entries) {
        Map<Item, AspectList> itemAspects = new LinkedHashMap<>();
        List<TagEntry> tagAspects = new ArrayList<>();

        entries.forEach((id, element) -> {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(element, id.toString());
                AspectList aspects = AspectList.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,
                        GsonHelper.getNonNull(json, "aspects"))
                        .getOrThrow(message -> new JsonParseException("Invalid aspect list: " + message));
                if (aspects.isEmpty()) {
                    Thaumcraft.LOGGER.warn("Ignoring empty item aspect entry {}", id);
                    return;
                }

                if (json.has("item") == json.has("tag")) {
                    throw new JsonParseException("Expected exactly one of 'item' or 'tag'");
                }

                if (json.has("item")) {
                    ResourceLocation itemId = ResourceLocation.parse(GsonHelper.getAsString(json, "item"));
                    Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
                    if (item.isEmpty()) {
                        Thaumcraft.LOGGER.warn("Ignoring item aspect entry {} because item {} does not exist", id,
                                itemId);
                        return;
                    }
                    itemAspects.put(item.get(), aspects);
                } else {
                    ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(json, "tag"));
                    tagAspects.add(new TagEntry(TagKey.create(Registries.ITEM, tagId), aspects));
                }
            } catch (RuntimeException exception) {
                Thaumcraft.LOGGER.error("Failed to load item aspect entry {}", id, exception);
            }
        });

        ITEM_ASPECTS.clear();
        ITEM_ASPECTS.putAll(itemAspects);
        TAG_ASPECTS.clear();
        TAG_ASPECTS.addAll(tagAspects);
        rebuildTagItemCache();
        Thaumcraft.LOGGER.info("Loaded {} item aspect entries, {} item tag aspect entries, {} cached tagged items",
                ITEM_ASPECTS.size(), TAG_ASPECTS.size(), TAG_ITEM_ASPECTS.size());
    }

    private static void regenerateFromRecipes(RecipeManager recipeManager, HolderLookup.Provider registries) {
        long start = System.nanoTime();
        Map<Item, AspectList> generated = new LinkedHashMap<>();
        List<RecipeHolder<?>> recipes = recipeManager.getRecipes().stream().toList();
        int passes = 0;
        int checkedRecipes = 0;

        for (int pass = 0; pass < MAX_GENERATION_PASSES; pass++) {
            Map<Ingredient, AspectList> ingredientCache = new IdentityHashMap<>();
            passes++;
            int before = generated.size();
            for (RecipeHolder<?> holder : recipes) {
                checkedRecipes++;
                Recipe<?> recipe = holder.value();
                ItemStack result = recipe.getResultItem(registries).copy();
                if (result.isEmpty() || hasExplicitAspects(result) || generated.containsKey(result.getItem())) {
                    continue;
                }

                AspectList generatedAspects = generateRecipeAspects(recipe, result, generated, ingredientCache,
                        new HashSet<>());
                if (!generatedAspects.isEmpty()) {
                    generated.put(result.getItem(), capAspects(generatedAspects, 64));
                }
            }
            if (generated.size() == before) {
                break;
            }
        }

        GENERATED_ASPECTS.clear();
        GENERATED_ASPECTS.putAll(generated);
        Thaumcraft.LOGGER.info("Generated {} item aspect entries from {} recipes in {} passes ({} checks, {} ms)",
                GENERATED_ASPECTS.size(), recipes.size(), passes, checkedRecipes, (System.nanoTime() - start) / 1_000_000L);
    }

    private static AspectList generateRecipeAspects(Recipe<?> recipe, ItemStack result, Map<Item, AspectList> generated,
            Map<Ingredient, AspectList> ingredientCache, Set<Item> history) {
        AspectList aspects = getAspectsFromIngredients(recipe.getIngredients(), result.getCount(), generated,
                ingredientCache, history);
        if (recipe instanceof ArcaneWorktableRecipe arcaneRecipe) {
            addPrimalVisCost(aspects, arcaneRecipe.getVisCost(), result.getCount());
        } else if (recipe instanceof InfusionRecipe infusionRecipe) {
            AspectList essentia = infusionRecipe.getEssentia();
            for (Aspect aspect : essentia.getAspects()) {
                int amount = (int) (Math.sqrt(essentia.getAmount(aspect)) / Math.max(1, result.getCount()));
                aspects.add(aspect, amount);
            }
        }
        return aspects;
    }

    private static AspectList getAspectsFromIngredients(List<Ingredient> ingredients, int resultCount,
            Map<Item, AspectList> generated, Map<Ingredient, AspectList> ingredientCache, Set<Item> history) {
        AspectList mid = new AspectList();
        for (Ingredient ingredient : ingredients) {
            AspectList ingredientAspects = chooseIngredientAspects(ingredient, generated, ingredientCache, history);
            if (ingredientAspects.isEmpty()) {
                return AspectList.EMPTY;
            }
            mid.add(ingredientAspects);
        }

        AspectList output = new AspectList();
        int divisor = Math.max(1, resultCount);
        for (Aspect aspect : mid.getAspects()) {
            output.add(aspect, (int) (mid.getAmount(aspect) * 0.75F / divisor));
        }
        return output;
    }

    private static AspectList chooseIngredientAspects(Ingredient ingredient, Map<Item, AspectList> generated,
            Map<Ingredient, AspectList> ingredientCache, Set<Item> history) {
        AspectList cached = ingredientCache.get(ingredient);
        if (cached != null) {
            return cached;
        }

        AspectList best = AspectList.EMPTY;
        int bestValue = Integer.MAX_VALUE;
        for (ItemStack candidate : ingredient.getItems()) {
            if (candidate.isEmpty() || history.contains(candidate.getItem())) {
                continue;
            }
            history.add(candidate.getItem());
            AspectList aspects = getKnownObjectTags(candidate, generated);
            history.remove(candidate.getItem());
            if (!aspects.isEmpty() && aspects.visSize() < bestValue) {
                best = aspects;
                bestValue = aspects.visSize();
            }
        }
        ingredientCache.put(ingredient, best);
        return best;
    }

    private static AspectList getKnownObjectTags(ItemStack stack, Map<Item, AspectList> generated) {
        AspectList itemAspects = ITEM_ASPECTS.get(stack.getItem());
        if (itemAspects != null) {
            return itemAspects.copy();
        }

        AspectList tagAspects = TAG_ITEM_ASPECTS.get(stack.getItem());
        if (tagAspects != null) {
            return tagAspects.copy();
        }

        AspectList generatedAspects = generated.get(stack.getItem());
        return generatedAspects != null ? generatedAspects.copy() : AspectList.EMPTY;
    }

    private static boolean hasExplicitAspects(ItemStack stack) {
        if (ITEM_ASPECTS.containsKey(stack.getItem())) {
            return true;
        }
        return TAG_ITEM_ASPECTS.containsKey(stack.getItem());
    }

    private static void rebuildTagItemCache() {
        TAG_ITEM_ASPECTS.clear();
        if (TAG_ASPECTS.isEmpty()) {
            return;
        }

        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = item.getDefaultInstance();
            if (stack.isEmpty()) {
                continue;
            }

            AspectList merged = new AspectList();
            for (TagEntry entry : TAG_ASPECTS) {
                if (stack.is(entry.tag())) {
                    merged.merge(entry.aspects());
                }
            }
            if (!merged.isEmpty()) {
                TAG_ITEM_ASPECTS.put(item, merged);
            }
        }
    }

    private static AspectList capAspects(AspectList aspects, int maxAmount) {
        AspectList capped = new AspectList();
        for (Aspect aspect : aspects.getAspects()) {
            int amount = Math.min(maxAmount, aspects.getAmount(aspect));
            if (amount > 0) {
                capped.add(aspect, amount);
            }
        }
        return capped;
    }

    private static void addContainedEssentia(ItemStack stack, AspectList output) {
        EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        if (!essentia.isEmpty()) {
            output.add(essentia.aspect(), essentia.amount());
        }
    }

    private static boolean isPotionLike(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION
                || item == Items.TIPPED_ARROW;
    }

    private static AspectList withPotionAspects(ItemStack stack, AspectList aspects) {
        if (!isPotionLike(stack)) {
            return aspects;
        }
        AspectList output = aspects.copy();
        addPotionAspects(stack, output);
        return capAspects(output, 64);
    }

    private static void addPotionAspects(ItemStack stack, AspectList output) {
        PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
        if (potion == null) {
            return;
        }

        output.merge(Aspect.WATER, 1);
        if (stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
            output.merge(Aspect.ENTROPY, 2);
        }

        for (MobEffectInstance effect : potion.getAllEffects()) {
            int strength = effect.getAmplifier() + 1;
            output.merge(Aspect.MAGIC, strength * 2);
            if (effect.getEffect().is(MobEffects.BLINDNESS)) {
                output.merge(Aspect.DARKNESS, strength * 3);
            } else if (effect.getEffect().is(MobEffects.CONFUSION)) {
                output.merge(Aspect.ELDRITCH, strength * 3);
            } else if (effect.getEffect().is(MobEffects.DAMAGE_BOOST)) {
                output.merge(Aspect.WEAPON, strength * 3);
            } else if (effect.getEffect().is(MobEffects.DIG_SLOWDOWN)) {
                output.merge(Aspect.TRAP, strength * 3);
            } else if (effect.getEffect().is(MobEffects.DIG_SPEED)) {
                output.merge(Aspect.TOOL, strength * 3);
            } else if (effect.getEffect().is(MobEffects.FIRE_RESISTANCE)) {
                output.merge(Aspect.ARMOR, strength);
                output.merge(Aspect.FIRE, strength * 2);
            } else if (effect.getEffect().is(MobEffects.HARM)) {
                output.merge(Aspect.DEATH, strength * 3);
            } else if (effect.getEffect().is(MobEffects.HEAL)) {
                output.merge(Aspect.HEAL, strength * 3);
            } else if (effect.getEffect().is(MobEffects.HUNGER)) {
                output.merge(Aspect.DEATH, strength * 3);
            } else if (effect.getEffect().is(MobEffects.INVISIBILITY)) {
                output.merge(Aspect.SENSES, strength * 3);
            } else if (effect.getEffect().is(MobEffects.JUMP)) {
                output.merge(Aspect.FLIGHT, strength * 3);
            } else if (effect.getEffect().is(MobEffects.MOVEMENT_SLOWDOWN)) {
                output.merge(Aspect.TRAP, strength * 3);
            } else if (effect.getEffect().is(MobEffects.MOVEMENT_SPEED)) {
                output.merge(Aspect.MOTION, strength * 3);
            } else if (effect.getEffect().is(MobEffects.NIGHT_VISION)) {
                output.merge(Aspect.SENSES, strength * 3);
            } else if (effect.getEffect().is(MobEffects.POISON)) {
                output.merge(Aspect.POISON, strength * 3);
            } else if (effect.getEffect().is(MobEffects.REGENERATION)) {
                output.merge(Aspect.HEAL, strength * 3);
            } else if (effect.getEffect().is(MobEffects.DAMAGE_RESISTANCE)) {
                output.merge(Aspect.ARMOR, strength * 3);
            } else if (effect.getEffect().is(MobEffects.WATER_BREATHING)) {
                output.merge(Aspect.AIR, strength * 3);
            } else if (effect.getEffect().is(MobEffects.WEAKNESS)) {
                output.merge(Aspect.DEATH, strength * 3);
            }
        }
    }

    private static void addEnchantments(ItemEnchantments enchantments, AspectList output) {
        int totalLevel = 0;
        for (Object2IntMap.Entry<net.minecraft.core.Holder<Enchantment>> entry : enchantments.entrySet()) {
            int level = entry.getIntValue();
            if (level <= 0) {
                continue;
            }

            if (entry.getKey().is(Enchantments.AQUA_AFFINITY)) {
                output.merge(Aspect.WATER, level);
            } else if (entry.getKey().is(Enchantments.BANE_OF_ARTHROPODS)) {
                output.merge(Aspect.BEAST, level);
            } else if (entry.getKey().is(Enchantments.BLAST_PROTECTION)) {
                output.merge(Aspect.ARMOR, level);
            } else if (entry.getKey().is(Enchantments.EFFICIENCY)) {
                output.merge(Aspect.TOOL, level);
            } else if (entry.getKey().is(Enchantments.FEATHER_FALLING)) {
                output.merge(Aspect.FLIGHT, level);
            } else if (entry.getKey().is(Enchantments.FIRE_ASPECT) || entry.getKey().is(Enchantments.FLAME)) {
                output.merge(Aspect.FIRE, level);
            } else if (entry.getKey().is(Enchantments.FIRE_PROTECTION)
                    || entry.getKey().is(Enchantments.PROJECTILE_PROTECTION)
                    || entry.getKey().is(Enchantments.PROTECTION)) {
                output.merge(Aspect.ARMOR, level);
            } else if (entry.getKey().is(Enchantments.FORTUNE) || entry.getKey().is(Enchantments.LOOTING)
                    || entry.getKey().is(Enchantments.LUCK_OF_THE_SEA) || entry.getKey().is(Enchantments.LURE)) {
                output.merge(Aspect.GREED, level);
            } else if (entry.getKey().is(Enchantments.INFINITY)) {
                output.merge(Aspect.CRAFT, level);
            } else if (entry.getKey().is(Enchantments.KNOCKBACK) || entry.getKey().is(Enchantments.PUNCH)
                    || entry.getKey().is(Enchantments.RESPIRATION)) {
                output.merge(Aspect.AIR, level);
            } else if (entry.getKey().is(Enchantments.POWER) || entry.getKey().is(Enchantments.SHARPNESS)
                    || entry.getKey().is(Enchantments.THORNS) || entry.getKey().is(Enchantments.IMPALING)
                    || entry.getKey().is(Enchantments.SWEEPING_EDGE) || entry.getKey().is(Enchantments.DENSITY)
                    || entry.getKey().is(Enchantments.BREACH)) {
                output.merge(Aspect.WEAPON, level);
            } else if (entry.getKey().is(Enchantments.SILK_TOUCH)) {
                output.merge(Aspect.EXCHANGE, level);
            } else if (entry.getKey().is(Enchantments.SMITE)) {
                output.merge(Aspect.ENTROPY, level);
            } else if (entry.getKey().is(Enchantments.UNBREAKING)) {
                output.merge(Aspect.EARTH, level);
            } else if (entry.getKey().is(Enchantments.DEPTH_STRIDER) || entry.getKey().is(Enchantments.SOUL_SPEED)
                    || entry.getKey().is(Enchantments.SWIFT_SNEAK) || entry.getKey().is(Enchantments.RIPTIDE)
                    || entry.getKey().is(Enchantments.QUICK_CHARGE)) {
                output.merge(Aspect.MOTION, level);
            } else if (entry.getKey().is(Enchantments.FROST_WALKER)) {
                output.merge(Aspect.COLD, level);
            } else if (entry.getKey().is(Enchantments.CHANNELING)) {
                output.merge(Aspect.WEATHER, level);
            } else if (entry.getKey().is(Enchantments.MULTISHOT) || entry.getKey().is(Enchantments.PIERCING)) {
                output.merge(Aspect.FLIGHT, level);
            } else if (entry.getKey().is(Enchantments.MENDING)) {
                output.merge(Aspect.TOOL, level);
            } else if (entry.getKey().is(Enchantments.BINDING_CURSE)
                    || entry.getKey().is(Enchantments.VANISHING_CURSE)) {
                output.merge(Aspect.TRAP, level);
            } else if (entry.getKey().is(Enchantments.WIND_BURST)) {
                output.merge(Aspect.AIR, level);
            }
            totalLevel += level;
        }

        if (totalLevel > 0) {
            output.merge(Aspect.MAGIC, totalLevel);
        }
    }

    private static int harvestLevel(Tier tier) {
        if (tier == Tiers.WOOD || tier == Tiers.GOLD) {
            return 0;
        }
        if (tier == Tiers.STONE) {
            return 1;
        }
        if (tier == Tiers.IRON) {
            return 2;
        }
        if (tier == Tiers.DIAMOND) {
            return 3;
        }
        if (tier == Tiers.NETHERITE) {
            return 4;
        }
        return Math.max(0, Math.round(tier.getSpeed() / 2.0F) - 1);
    }

    private static int harvestTierByDurability(ItemStack stack) {
        int durability = stack.getMaxDamage();
        if (durability <= Tiers.WOOD.getUses()) {
            return 1;
        }
        if (durability <= Tiers.STONE.getUses() || durability <= Tiers.GOLD.getUses()) {
            return 2;
        }
        if (durability <= Tiers.IRON.getUses()) {
            return 3;
        }
        return 4;
    }

    private static AspectList cullTags(AspectList input) {
        AspectList output = new AspectList();
        for (Aspect aspect : input.getAspects()) {
            if (aspect != null && input.getAmount(aspect) > 0) {
                output.add(aspect, input.getAmount(aspect));
            }
        }

        while (output.size() > 6) {
            Aspect lowest = null;
            float low = Float.MAX_VALUE;
            for (Aspect aspect : output.getAspects()) {
                float amount = output.getAmount(aspect);
                if (aspect.isPrimal()) {
                    amount *= 0.9F;
                } else {
                    amount = weightCompoundAspect(aspect, amount);
                }

                if (amount < low) {
                    low = amount;
                    lowest = aspect;
                }
            }
            output.remove(lowest);
        }
        return output;
    }

    private static float weightCompoundAspect(Aspect aspect, float amount) {
        Aspect[] components = aspect.getComponents();
        if (components == null || components.length != 2) {
            return amount;
        }

        for (Aspect component : components) {
            if (!component.isPrimal()) {
                amount *= 1.1F;
                Aspect[] subComponents = component.getComponents();
                if (subComponents != null && subComponents.length == 2) {
                    if (!subComponents[0].isPrimal()) {
                        amount *= 1.05F;
                    }
                    if (!subComponents[1].isPrimal()) {
                        amount *= 1.05F;
                    }
                }
            }
        }
        return amount;
    }

    private static void addPrimalVisCost(AspectList aspects, thaumcraft.api.aspects.PrimalVisStorage visCost,
            int resultCount) {
        int divisor = Math.max(1, resultCount);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int vis = visCost.get(aspect) / 100;
            int amount = (int) (Math.sqrt(vis) / divisor);
            aspects.add(aspect, amount);
        }
    }

    private record TagEntry(TagKey<Item> tag, AspectList aspects) {
        private TagEntry {
            aspects = aspects.copy();
        }
    }

    private static class ReloadListener extends SimpleJsonResourceReloadListener {
        private final RecipeManager recipeManager;
        private final HolderLookup.Provider registries;

        private ReloadListener(RecipeManager recipeManager, HolderLookup.Provider registries) {
            super(GSON, DIRECTORY);
            this.recipeManager = recipeManager;
            this.registries = registries;
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> entries, net.minecraft.server.packs.resources.ResourceManager resourceManager,
                ProfilerFiller profiler) {
            long start = System.nanoTime();
            ObjectAspectRegistry.reload(entries);
            ObjectAspectRegistry.regenerateFromRecipes(recipeManager, this.registries);
            Thaumcraft.LOGGER.info("Object aspect reload finished in {} ms", (System.nanoTime() - start) / 1_000_000L);
        }
    }
}
