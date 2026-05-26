package thaumcraft.common.items.wands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

public final class WandParts {
    public static final String WOOD_ROD = "wood";
    public static final String IRON_CAP = "iron";

    public static final Map<String, Cap> CAPS = new LinkedHashMap<>();
    public static final Map<String, Rod> RODS = new LinkedHashMap<>();

    public static final Cap CAP_IRON = cap("iron", 1, 1.1F);
    public static final Cap CAP_GOLD = cap("gold", 3, 1.0F);
    public static final Cap CAP_COPPER = cap("copper", 2, 1.1F, 1.0F, Aspect.ORDER, Aspect.ENTROPY);
    public static final Cap CAP_SILVER = cap("silver", 4, 1.0F, 0.95F, Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER);
    public static final Cap CAP_THAUMIUM = cap("thaumium", 6, 0.9F);
    public static final Cap CAP_VOID = cap("void", 9, 0.8F);

    public static final Rod ROD_WOOD = rod("wood", 25, 1, false, false, null);
    public static final Rod ROD_GREATWOOD = rod("greatwood", 50, 3, false, false, null);
    public static final Rod ROD_OBSIDIAN = rod("obsidian", 75, 6, false, false, Aspect.EARTH);
    public static final Rod ROD_BLAZE = rod("blaze", 75, 6, false, true, Aspect.FIRE);
    public static final Rod ROD_ICE = rod("ice", 75, 6, false, false, Aspect.WATER);
    public static final Rod ROD_QUARTZ = rod("quartz", 75, 6, false, false, Aspect.ORDER);
    public static final Rod ROD_BONE = rod("bone", 75, 6, false, false, Aspect.ENTROPY);
    public static final Rod ROD_REED = rod("reed", 75, 6, false, false, Aspect.AIR);
    public static final Rod ROD_SILVERWOOD = rod("silverwood", 100, 9, false, false, null);
    public static final Rod STAFF_GREATWOOD = rod("greatwood_staff", 125, 8, true, false, null);
    public static final Rod STAFF_OBSIDIAN = rod("obsidian_staff", 175, 14, true, false, Aspect.EARTH);
    public static final Rod STAFF_BLAZE = rod("blaze_staff", 175, 14, true, true, Aspect.FIRE);
    public static final Rod STAFF_ICE = rod("ice_staff", 175, 14, true, false, Aspect.WATER);
    public static final Rod STAFF_QUARTZ = rod("quartz_staff", 175, 14, true, false, Aspect.ORDER);
    public static final Rod STAFF_BONE = rod("bone_staff", 175, 14, true, false, Aspect.ENTROPY);
    public static final Rod STAFF_REED = rod("reed_staff", 175, 14, true, false, Aspect.AIR);
    public static final Rod STAFF_SILVERWOOD = rod("silverwood_staff", 250, 24, true, false, null);
    public static final Rod STAFF_PRIMAL = rod("primal_staff", 250, 32, true, false, null);

    private WandParts() {
    }

    public static Cap cap(String tag) {
        return CAPS.getOrDefault(tag, CAP_IRON);
    }

    public static Rod rod(String tag) {
        return RODS.getOrDefault(tag, ROD_WOOD);
    }

    public static Optional<Cap> capFrom(ItemStack stack) {
        return stack.getItem() instanceof WandPartItem part && part.getKind() == WandPartItem.Kind.CAP
                ? Optional.of(cap(part.getTag()))
                : Optional.empty();
    }

    public static Optional<Rod> rodFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        if (stack.getItem() instanceof WandPartItem part && part.getKind() == WandPartItem.Kind.ROD) {
            return Optional.of(rod(part.getTag()));
        }
        if (stack.is(net.minecraft.world.item.Items.STICK)) {
            return Optional.of(ROD_WOOD);
        }
        return Optional.empty();
    }

    private static Cap cap(String tag, int craftCost, float baseModifier) {
        return register(new Cap(tag, craftCost, baseModifier, baseModifier, new Aspect[0]));
    }

    private static Cap cap(String tag, int craftCost, float baseModifier, float specialModifier, Aspect... specialAspects) {
        return register(new Cap(tag, craftCost, baseModifier, specialModifier, specialAspects));
    }

    private static Cap register(Cap cap) {
        CAPS.put(cap.tag(), cap);
        return cap;
    }

    private static Rod rod(String tag, int capacity, int craftCost, boolean staff, boolean glowing, Aspect rechargeAspect) {
        Rod rod = new Rod(tag, capacity, craftCost, staff, glowing, rechargeAspect);
        RODS.put(tag, rod);
        return rod;
    }

    public record Cap(String tag, int craftCost, float baseModifier, float specialModifier, Aspect[] specialAspects) {
        public float costModifier(Aspect aspect) {
            for (Aspect specialAspect : this.specialAspects) {
                if (specialAspect == aspect) {
                    return this.specialModifier;
                }
            }
            return this.baseModifier;
        }
    }

    public record Rod(String tag, int capacity, int craftCost, boolean staff, boolean glowing, Aspect rechargeAspect) {
        public String textureTag() {
            return this.staff && this.tag.endsWith("_staff")
                    ? this.tag.substring(0, this.tag.length() - "_staff".length())
                    : this.tag;
        }

        public boolean hasRunes() {
            return "primal_staff".equals(this.tag);
        }
    }
}
