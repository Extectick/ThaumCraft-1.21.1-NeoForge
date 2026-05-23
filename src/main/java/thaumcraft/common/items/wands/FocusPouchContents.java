package thaumcraft.common.items.wands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record FocusPouchContents(List<ItemStack> stacks) {
    public static final int SIZE = 18;
    public static final FocusPouchContents EMPTY = new FocusPouchContents(List.of());
    public static final Codec<FocusPouchContents> CODEC = ItemStack.OPTIONAL_CODEC.listOf()
            .xmap(FocusPouchContents::new, FocusPouchContents::stacks);
    public static final StreamCodec<RegistryFriendlyByteBuf, FocusPouchContents> STREAM_CODEC = ItemStack.OPTIONAL_LIST_STREAM_CODEC
            .map(FocusPouchContents::new, FocusPouchContents::stacks);

    public FocusPouchContents {
        stacks = normalize(stacks);
    }

    public ItemStack get(int slot) {
        return slot >= 0 && slot < SIZE ? this.stacks.get(slot).copy() : ItemStack.EMPTY;
    }

    public FocusPouchContents with(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return this;
        }

        List<ItemStack> copy = copyStacks(this.stacks);
        ItemStack stored = stack.copy();
        if (!stored.isEmpty()) {
            stored.setCount(1);
        }
        copy.set(slot, stored);
        return new FocusPouchContents(copy);
    }

    public Optional<FocusPouchContents> addFocus(ItemStack focus) {
        if (!WandFocusHelper.isFocus(focus)) {
            return Optional.empty();
        }

        for (int slot = 0; slot < SIZE; slot++) {
            if (this.stacks.get(slot).isEmpty()) {
                return Optional.of(this.with(slot, focus));
            }
        }
        return Optional.empty();
    }

    public int focusCount() {
        int count = 0;
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static List<ItemStack> normalize(List<ItemStack> source) {
        List<ItemStack> normalized = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = i < source.size() ? source.get(i).copy() : ItemStack.EMPTY;
            if (!stack.isEmpty()) {
                stack.setCount(1);
            }
            normalized.add(stack);
        }
        return List.copyOf(normalized);
    }

    private static List<ItemStack> copyStacks(List<ItemStack> source) {
        List<ItemStack> copy = new ArrayList<>(SIZE);
        for (ItemStack stack : source) {
            copy.add(stack.copy());
        }
        return copy;
    }
}
