package thaumcraft.common.research;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thaumcraft.api.aspects.Aspect;

public record AspectPoolData(Map<Aspect, Integer> aspects) {
    public static final int STARTING_PRIMAL_AMOUNT = 25;
    public static final int FIRST_DISCOVERY_BONUS = 2;
    public static final AspectPoolData EMPTY = new AspectPoolData(Map.of());
    public static final Codec<AspectPoolData> CODEC = Codec.unboundedMap(Aspect.CODEC, Codec.INT)
            .xmap(AspectPoolData::new, AspectPoolData::aspects);
    public static final StreamCodec<RegistryFriendlyByteBuf, AspectPoolData> STREAM_CODEC =
            StreamCodec.of(AspectPoolData::encode, AspectPoolData::decode);

    public AspectPoolData {
        EnumMap<Aspect, Integer> cleaned = new EnumMap<>(Aspect.class);
        aspects.forEach((aspect, amount) -> {
            if (aspect != null && amount != null && amount >= 0) {
                cleaned.put(aspect, Math.max(0, amount));
            }
        });
        aspects = Map.copyOf(cleaned);
    }

    public static AspectPoolData starter() {
        EnumMap<Aspect, Integer> aspects = new EnumMap<>(Aspect.class);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            aspects.put(aspect, startingPrimalAmount());
        }
        return new AspectPoolData(aspects);
    }

    private static int startingPrimalAmount() {
        return 15 + ThreadLocalRandom.current().nextInt(5);
    }

    public int get(Aspect aspect) {
        return this.aspects.getOrDefault(aspect, 0);
    }

    public boolean has(Aspect aspect, int amount) {
        return amount <= 0 || this.get(aspect) >= amount;
    }

    public boolean isDiscovered(Aspect aspect) {
        return aspect != null && this.aspects.containsKey(aspect);
    }

    public AspectPoolData discover(Aspect aspect) {
        if (aspect == null || this.isDiscovered(aspect)) {
            return this;
        }
        EnumMap<Aspect, Integer> copy = new EnumMap<>(Aspect.class);
        copy.putAll(this.aspects);
        copy.put(aspect, 0);
        return new AspectPoolData(copy);
    }

    public AspectPoolData add(Aspect aspect, int amount) {
        if (aspect == null) {
            return this;
        }
        if (amount <= 0) {
            return this.discover(aspect);
        }
        EnumMap<Aspect, Integer> copy = new EnumMap<>(Aspect.class);
        copy.putAll(this.aspects);
        copy.put(aspect, this.get(aspect) + amount);
        return new AspectPoolData(copy);
    }

    public AspectPoolData learn(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return this;
        }
        int gained = this.isDiscovered(aspect) ? amount : amount + FIRST_DISCOVERY_BONUS;
        return this.add(aspect, gained);
    }

    public AspectPoolData set(Aspect aspect, int amount) {
        if (aspect == null) {
            return this;
        }
        EnumMap<Aspect, Integer> copy = new EnumMap<>(Aspect.class);
        copy.putAll(this.aspects);
        copy.put(aspect, Math.max(0, amount));
        return new AspectPoolData(copy);
    }

    public AspectPoolData remove(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return this;
        }
        EnumMap<Aspect, Integer> copy = new EnumMap<>(Aspect.class);
        copy.putAll(this.aspects);
        int remaining = Math.max(0, this.get(aspect) - amount);
        copy.put(aspect, remaining);
        return new AspectPoolData(copy);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, AspectPoolData data) {
        buffer.writeMap(data.aspects, Aspect.STREAM_CODEC::encode, ByteBufCodecs.VAR_INT::encode);
    }

    private static AspectPoolData decode(RegistryFriendlyByteBuf buffer) {
        return new AspectPoolData(buffer.readMap(
                entryBuffer -> Aspect.STREAM_CODEC.decode((ByteBuf) entryBuffer),
                ByteBufCodecs.VAR_INT::decode));
    }
}
