package thaumcraft.common.research;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import thaumcraft.api.aspects.AspectList;

public record ScanResult(Kind kind, String key, ItemStack stack, AspectList aspects, Vec3 effectCenter,
        int runeDuration, String displayName) {
    public enum Kind {
        OBJECT,
        ENTITY,
        PHENOMENA
    }

    public ScanResult {
        stack = stack == null ? ItemStack.EMPTY : stack.copyWithCount(1);
        aspects = aspects == null ? AspectList.EMPTY : aspects.copy();
        runeDuration = Math.max(1, runeDuration);
        displayName = displayName == null ? "" : displayName;
    }

    public ScanResult(Kind kind, String key, ItemStack stack, AspectList aspects, Vec3 effectCenter,
            int runeDuration) {
        this(kind, key, stack, aspects, effectCenter, runeDuration, "");
    }

    public boolean sameTarget(ScanResult other) {
        return other != null && this.kind == other.kind && this.key.equals(other.key);
    }

    public boolean hasAspects() {
        return !this.aspects.isEmpty();
    }

    public static String objectKey(ItemStack stack) {
        return "object:" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + ":" + (stack.isDamageableItem() ? -1 : 0);
    }
}
