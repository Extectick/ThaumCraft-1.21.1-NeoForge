package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record BlockZapFxPayload(double fromX, double fromY, double fromZ, double toX, double toY, double toZ)
        implements CustomPacketPayload {
    public static final Type<BlockZapFxPayload> TYPE = new Type<>(Thaumcraft.id("block_zap_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockZapFxPayload> STREAM_CODEC =
            StreamCodec.of(BlockZapFxPayload::encode, BlockZapFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, BlockZapFxPayload payload) {
        buffer.writeDouble(payload.fromX);
        buffer.writeDouble(payload.fromY);
        buffer.writeDouble(payload.fromZ);
        buffer.writeDouble(payload.toX);
        buffer.writeDouble(payload.toY);
        buffer.writeDouble(payload.toZ);
    }

    private static BlockZapFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new BlockZapFxPayload(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }
}
