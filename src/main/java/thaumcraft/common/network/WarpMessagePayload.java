package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

public record WarpMessagePayload(byte warpType, int amount) implements CustomPacketPayload {
    public static final Type<WarpMessagePayload> TYPE = new Type<>(Thaumcraft.id("warp_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WarpMessagePayload> STREAM_CODEC =
            StreamCodec.of(WarpMessagePayload::encode, WarpMessagePayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WarpMessagePayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist.isClient()) {
            handleClient(payload);
        }
    }

    private static void handleClient(WarpMessagePayload payload) {
        try {
            Class<?> handler = Class.forName("thaumcraft.client.network.TCClientPayloadHandler");
            handler.getMethod("handleWarpMessage", WarpMessagePayload.class).invoke(null, payload);
        } catch (ReflectiveOperationException exception) {
            Thaumcraft.LOGGER.warn("Unable to handle warp message", exception);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, WarpMessagePayload payload) {
        buffer.writeByte(payload.warpType);
        ByteBufCodecs.VAR_INT.encode(buffer, payload.amount);
    }

    private static WarpMessagePayload decode(RegistryFriendlyByteBuf buffer) {
        return new WarpMessagePayload(buffer.readByte(), ByteBufCodecs.VAR_INT.decode(buffer));
    }
}
