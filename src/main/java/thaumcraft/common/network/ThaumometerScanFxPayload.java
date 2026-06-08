package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record ThaumometerScanFxPayload(double x, double y, double z, float red, float green, float blue, int duration,
        float gravity) implements CustomPacketPayload {
    public static final Type<ThaumometerScanFxPayload> TYPE = new Type<>(Thaumcraft.id("thaumometer_scan_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ThaumometerScanFxPayload> STREAM_CODEC =
            StreamCodec.of(ThaumometerScanFxPayload::encode, ThaumometerScanFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ThaumometerScanFxPayload payload) {
        buffer.writeDouble(payload.x);
        buffer.writeDouble(payload.y);
        buffer.writeDouble(payload.z);
        buffer.writeFloat(payload.red);
        buffer.writeFloat(payload.green);
        buffer.writeFloat(payload.blue);
        buffer.writeVarInt(payload.duration);
        buffer.writeFloat(payload.gravity);
    }

    private static ThaumometerScanFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ThaumometerScanFxPayload(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readVarInt(), buffer.readFloat());
    }
}
