package thaumcraft.common.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WarpData(int permanent, int sticky, int temporary, int counter) {
    public static final WarpData EMPTY = new WarpData(0, 0, 0, 0);
    public static final Codec<WarpData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("permanent", 0).forGetter(WarpData::permanent),
            Codec.INT.optionalFieldOf("sticky", 0).forGetter(WarpData::sticky),
            Codec.INT.optionalFieldOf("temporary", 0).forGetter(WarpData::temporary),
            Codec.INT.optionalFieldOf("counter", 0).forGetter(WarpData::counter)
    ).apply(instance, WarpData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WarpData> STREAM_CODEC =
            StreamCodec.of(WarpData::encode, WarpData::decode);

    public WarpData {
        permanent = Math.max(0, permanent);
        sticky = Math.max(0, sticky);
        temporary = Math.max(0, temporary);
        counter = Math.max(0, counter);
    }

    public int total() {
        return this.permanent + this.sticky + this.temporary;
    }

    public WarpData addPermanent(int amount) {
        int permanent = Math.max(0, this.permanent + amount);
        return new WarpData(permanent, this.sticky, this.temporary, permanent + this.sticky + this.temporary);
    }

    public WarpData addSticky(int amount) {
        int sticky = Math.max(0, this.sticky + amount);
        return new WarpData(this.permanent, sticky, this.temporary, this.permanent + sticky + this.temporary);
    }

    public WarpData addTemporary(int amount) {
        int temporary = Math.max(0, this.temporary + amount);
        return new WarpData(this.permanent, this.sticky, temporary, this.permanent + this.sticky + temporary);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, WarpData data) {
        ByteBufCodecs.VAR_INT.encode(buffer, data.permanent);
        ByteBufCodecs.VAR_INT.encode(buffer, data.sticky);
        ByteBufCodecs.VAR_INT.encode(buffer, data.temporary);
        ByteBufCodecs.VAR_INT.encode(buffer, data.counter);
    }

    private static WarpData decode(RegistryFriendlyByteBuf buffer) {
        return new WarpData(ByteBufCodecs.VAR_INT.decode((ByteBuf)buffer),
                ByteBufCodecs.VAR_INT.decode((ByteBuf)buffer),
                ByteBufCodecs.VAR_INT.decode((ByteBuf)buffer),
                ByteBufCodecs.VAR_INT.decode((ByteBuf)buffer));
    }
}
