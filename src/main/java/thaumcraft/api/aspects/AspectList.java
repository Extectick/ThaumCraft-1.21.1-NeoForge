package thaumcraft.api.aspects;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AspectList {
    public static final AspectList EMPTY = new AspectList();

    public static final Codec<AspectList> CODEC = Codec.unboundedMap(Aspect.CODEC, Codec.INT)
            .xmap(AspectList::new, AspectList::asMap);

    public static final StreamCodec<ByteBuf, AspectList> STREAM_CODEC = StreamCodec.of(
            AspectList::toNetwork, AspectList::fromNetwork);

    private final EnumMap<Aspect, Integer> aspects = new EnumMap<>(Aspect.class);

    public AspectList() {
    }

    public AspectList(Map<Aspect, Integer> aspects) {
        aspects.forEach(this::add);
    }

    public AspectList copy() {
        return new AspectList(this.aspects);
    }

    public int size() {
        return this.aspects.size();
    }

    public boolean isEmpty() {
        return this.aspects.isEmpty();
    }

    public int visSize() {
        int total = 0;
        for (int amount : this.aspects.values()) {
            total += amount;
        }
        return total;
    }

    public List<Aspect> getAspects() {
        return List.copyOf(this.aspects.keySet());
    }

    public List<Aspect> getPrimalAspects() {
        return this.aspects.keySet().stream()
                .filter(Aspect::isPrimal)
                .toList();
    }

    public List<Aspect> getAspectsSorted() {
        return this.aspects.keySet().stream()
                .sorted((left, right) -> left.getTag().compareTo(right.getTag()))
                .toList();
    }

    public List<Aspect> getAspectsSortedAmount() {
        return this.aspects.keySet().stream()
                .sorted((left, right) -> Integer.compare(this.getAmount(right), this.getAmount(left)))
                .toList();
    }

    public int getAmount(Aspect aspect) {
        return this.aspects.getOrDefault(aspect, 0);
    }

    public boolean reduce(Aspect aspect, int amount) {
        if (amount <= 0) {
            return true;
        }
        if (this.getAmount(aspect) < amount) {
            return false;
        }
        this.remove(aspect, amount);
        return true;
    }

    public AspectList remove(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return this;
        }
        int remaining = this.getAmount(aspect) - amount;
        if (remaining <= 0) {
            this.aspects.remove(aspect);
        } else {
            this.aspects.put(aspect, remaining);
        }
        return this;
    }

    public AspectList remove(Aspect aspect) {
        if (aspect != null) {
            this.aspects.remove(aspect);
        }
        return this;
    }

    public AspectList add(Aspect aspect, int amount) {
        if (aspect != null && amount > 0) {
            this.aspects.merge(aspect, amount, Integer::sum);
        }
        return this;
    }

    public AspectList add(AspectList other) {
        for (Aspect aspect : other.getAspects()) {
            this.add(aspect, other.getAmount(aspect));
        }
        return this;
    }

    public AspectList merge(Aspect aspect, int amount) {
        if (aspect != null && amount > 0 && amount > this.getAmount(aspect)) {
            this.aspects.put(aspect, amount);
        }
        return this;
    }

    public AspectList merge(AspectList other) {
        for (Aspect aspect : other.getAspects()) {
            this.merge(aspect, other.getAmount(aspect));
        }
        return this;
    }

    public Map<Aspect, Integer> asMap() {
        return Collections.unmodifiableMap(this.aspects);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        this.writeToNBT(tag);
        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        this.readFromNBT(tag, "Aspects");
    }

    public void readFromNBT(CompoundTag tag, String label) {
        this.aspects.clear();
        ListTag list = tag.getList(label, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag aspectTag = list.getCompound(i);
            Aspect.byTag(aspectTag.getString("key"))
                    .ifPresent(aspect -> this.add(aspect, aspectTag.getInt("amount")));
        }
    }

    public void writeToNBT(CompoundTag tag) {
        this.writeToNBT(tag, "Aspects");
    }

    public void writeToNBT(CompoundTag tag, String label) {
        ListTag list = new ListTag();
        for (Aspect aspect : this.getAspects()) {
            CompoundTag aspectTag = new CompoundTag();
            aspectTag.putString("key", aspect.getTag());
            aspectTag.putInt("amount", this.getAmount(aspect));
            list.add(aspectTag);
        }
        tag.put(label, list);
    }

    public static AspectList load(CompoundTag tag) {
        AspectList aspects = new AspectList();
        aspects.readFromNBT(tag);
        return aspects;
    }

    public static AspectList load(CompoundTag tag, String label) {
        AspectList aspects = new AspectList();
        aspects.readFromNBT(tag, label);
        return aspects;
    }

    private static AspectList fromNetwork(ByteBuf buffer) {
        int size = ByteBufCodecs.VAR_INT.decode(buffer);
        AspectList aspects = new AspectList();
        for (int i = 0; i < size; i++) {
            Aspect aspect = Aspect.STREAM_CODEC.decode(buffer);
            int amount = ByteBufCodecs.VAR_INT.decode(buffer);
            aspects.add(aspect, amount);
        }
        return aspects;
    }

    private static void toNetwork(ByteBuf buffer, AspectList aspects) {
        ByteBufCodecs.VAR_INT.encode(buffer, aspects.size());
        for (Aspect aspect : aspects.getAspects()) {
            Aspect.STREAM_CODEC.encode(buffer, aspect);
            ByteBufCodecs.VAR_INT.encode(buffer, aspects.getAmount(aspect));
        }
    }
}
