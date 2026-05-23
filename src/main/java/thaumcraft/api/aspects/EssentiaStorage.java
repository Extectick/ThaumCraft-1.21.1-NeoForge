package thaumcraft.api.aspects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public record EssentiaStorage(Aspect aspect, int amount) {
    public static final EssentiaStorage EMPTY = new EssentiaStorage(Aspect.AIR, 0);

    public static final Codec<EssentiaStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Aspect.CODEC.optionalFieldOf("aspect", Aspect.AIR).forGetter(EssentiaStorage::aspect),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(EssentiaStorage::amount))
            .apply(instance, EssentiaStorage::new));

    public static final StreamCodec<ByteBuf, EssentiaStorage> STREAM_CODEC = StreamCodec.composite(
            Aspect.STREAM_CODEC, EssentiaStorage::aspect,
            ByteBufCodecs.VAR_INT, EssentiaStorage::amount,
            EssentiaStorage::new);

    public EssentiaStorage {
        amount = Math.max(0, amount);
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public EssentiaStorage withAmount(int amount, int capacity) {
        return new EssentiaStorage(this.aspect, Mth.clamp(amount, 0, Math.max(0, capacity)));
    }

    public EssentiaStorage withAspect(Aspect aspect) {
        return new EssentiaStorage(aspect, this.amount);
    }
}
