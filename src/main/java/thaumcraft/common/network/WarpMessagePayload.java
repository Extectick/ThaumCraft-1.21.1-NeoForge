package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record WarpMessagePayload(byte warpType, int amount) implements CustomPacketPayload {
    public static final Type<WarpMessagePayload> TYPE = new Type<>(Thaumcraft.id("warp_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WarpMessagePayload> STREAM_CODEC =
            StreamCodec.of(WarpMessagePayload::encode, WarpMessagePayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, WarpMessagePayload payload) {
        buffer.writeByte(payload.warpType);
        ByteBufCodecs.VAR_INT.encode(buffer, payload.amount);
    }

    private static WarpMessagePayload decode(RegistryFriendlyByteBuf buffer) {
        return new WarpMessagePayload(buffer.readByte(), ByteBufCodecs.VAR_INT.decode(buffer));
    }
}
