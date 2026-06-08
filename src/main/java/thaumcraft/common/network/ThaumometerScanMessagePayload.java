package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectList;

public record ThaumometerScanMessagePayload(byte kind, String targetName, AspectList gains, AspectList discoveries)
        implements CustomPacketPayload {
    public static final byte COMPLETE = 0;
    public static final byte DISCOVERY_ERROR = 1;
    public static final byte UNKNOWN_OBJECT = 2;
    public static final Type<ThaumometerScanMessagePayload> TYPE = new Type<>(Thaumcraft.id("thaumometer_scan_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ThaumometerScanMessagePayload> STREAM_CODEC =
            StreamCodec.of(ThaumometerScanMessagePayload::encode, ThaumometerScanMessagePayload::decode);

    public ThaumometerScanMessagePayload {
        targetName = targetName == null ? "" : targetName;
        gains = gains == null ? AspectList.EMPTY : gains.copy();
        discoveries = discoveries == null ? AspectList.EMPTY : discoveries.copy();
    }

    public ThaumometerScanMessagePayload(byte kind, String targetName, AspectList gains) {
        this(kind, targetName, gains, AspectList.EMPTY);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ThaumometerScanMessagePayload payload) {
        buffer.writeByte(payload.kind);
        ByteBufCodecs.STRING_UTF8.encode(buffer, payload.targetName);
        AspectList.STREAM_CODEC.encode(buffer, payload.gains);
        AspectList.STREAM_CODEC.encode(buffer, payload.discoveries);
    }

    private static ThaumometerScanMessagePayload decode(RegistryFriendlyByteBuf buffer) {
        return new ThaumometerScanMessagePayload(buffer.readByte(), ByteBufCodecs.STRING_UTF8.decode(buffer),
                AspectList.STREAM_CODEC.decode(buffer), AspectList.STREAM_CODEC.decode(buffer));
    }
}
