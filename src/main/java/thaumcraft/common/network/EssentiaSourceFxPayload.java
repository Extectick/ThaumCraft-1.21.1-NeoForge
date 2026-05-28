package thaumcraft.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

public record EssentiaSourceFxPayload(BlockPos target, BlockPos source, int color) implements CustomPacketPayload {
    public static final Type<EssentiaSourceFxPayload> TYPE = new Type<>(Thaumcraft.id("essentia_source_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EssentiaSourceFxPayload> STREAM_CODEC =
            StreamCodec.of(EssentiaSourceFxPayload::encode, EssentiaSourceFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EssentiaSourceFxPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist.isClient()) {
            handleClient(payload);
        }
    }

    private static void handleClient(EssentiaSourceFxPayload payload) {
        try {
            Class<?> handler = Class.forName("thaumcraft.client.network.TCClientPayloadHandler");
            handler.getMethod("handleEssentiaSourceFx", EssentiaSourceFxPayload.class)
                    .invoke(null, payload);
        } catch (ReflectiveOperationException exception) {
            Thaumcraft.LOGGER.warn("Unable to handle essentia source fx", exception);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, EssentiaSourceFxPayload payload) {
        buffer.writeBlockPos(payload.target);
        buffer.writeBlockPos(payload.source);
        buffer.writeInt(payload.color);
    }

    private static EssentiaSourceFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new EssentiaSourceFxPayload(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt());
    }
}
