package thaumcraft.api.aspects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public record PrimalVisStorage(int air, int fire, int water, int earth, int order, int entropy) {
    public static final PrimalVisStorage EMPTY = new PrimalVisStorage(0, 0, 0, 0, 0, 0);

    public static final Codec<PrimalVisStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("air", 0).forGetter(PrimalVisStorage::air),
            Codec.INT.optionalFieldOf("fire", 0).forGetter(PrimalVisStorage::fire),
            Codec.INT.optionalFieldOf("water", 0).forGetter(PrimalVisStorage::water),
            Codec.INT.optionalFieldOf("earth", 0).forGetter(PrimalVisStorage::earth),
            Codec.INT.optionalFieldOf("order", 0).forGetter(PrimalVisStorage::order),
            Codec.INT.optionalFieldOf("entropy", 0).forGetter(PrimalVisStorage::entropy))
            .apply(instance, PrimalVisStorage::new));

    public static final StreamCodec<ByteBuf, PrimalVisStorage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PrimalVisStorage::air,
            ByteBufCodecs.VAR_INT, PrimalVisStorage::fire,
            ByteBufCodecs.VAR_INT, PrimalVisStorage::water,
            ByteBufCodecs.VAR_INT, PrimalVisStorage::earth,
            ByteBufCodecs.VAR_INT, PrimalVisStorage::order,
            ByteBufCodecs.VAR_INT, PrimalVisStorage::entropy,
            PrimalVisStorage::new);

    public PrimalVisStorage {
        air = Math.max(0, air);
        fire = Math.max(0, fire);
        water = Math.max(0, water);
        earth = Math.max(0, earth);
        order = Math.max(0, order);
        entropy = Math.max(0, entropy);
    }

    public int get(Aspect aspect) {
        return switch (aspect) {
            case AIR -> this.air;
            case FIRE -> this.fire;
            case WATER -> this.water;
            case EARTH -> this.earth;
            case ORDER -> this.order;
            case ENTROPY -> this.entropy;
        };
    }

    public PrimalVisStorage with(Aspect aspect, int amount, int maxVis) {
        int clamped = Mth.clamp(amount, 0, Math.max(0, maxVis));
        return switch (aspect) {
            case AIR -> new PrimalVisStorage(clamped, this.fire, this.water, this.earth, this.order, this.entropy);
            case FIRE -> new PrimalVisStorage(this.air, clamped, this.water, this.earth, this.order, this.entropy);
            case WATER -> new PrimalVisStorage(this.air, this.fire, clamped, this.earth, this.order, this.entropy);
            case EARTH -> new PrimalVisStorage(this.air, this.fire, this.water, clamped, this.order, this.entropy);
            case ORDER -> new PrimalVisStorage(this.air, this.fire, this.water, this.earth, clamped, this.entropy);
            case ENTROPY -> new PrimalVisStorage(this.air, this.fire, this.water, this.earth, this.order, clamped);
        };
    }

    public boolean isEmpty() {
        return this.air == 0 && this.fire == 0 && this.water == 0 && this.earth == 0 && this.order == 0 && this.entropy == 0;
    }
}
