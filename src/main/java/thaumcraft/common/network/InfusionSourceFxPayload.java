package thaumcraft.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

public record InfusionSourceFxPayload(BlockPos target, BlockPos source, int color, int ticks)
        implements CustomPacketPayload {
    public static final Type<InfusionSourceFxPayload> TYPE = new Type<>(Thaumcraft.id("infusion_source_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionSourceFxPayload> STREAM_CODEC =
            StreamCodec.of(InfusionSourceFxPayload::encode, InfusionSourceFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InfusionSourceFxPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist.isClient()) {
            handleClient(payload);
        }
    }

    private static void handleClient(InfusionSourceFxPayload payload) {
        try {
            Class<?> handler = Class.forName("thaumcraft.client.network.TCClientPayloadHandler");
            handler.getMethod("handleInfusionSourceFx", InfusionSourceFxPayload.class).invoke(null, payload);
        } catch (ReflectiveOperationException exception) {
            Thaumcraft.LOGGER.warn("Unable to handle infusion source fx", exception);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, InfusionSourceFxPayload payload) {
        buffer.writeBlockPos(payload.target);
        buffer.writeBlockPos(payload.source);
        buffer.writeInt(payload.color);
        buffer.writeVarInt(payload.ticks);
    }

    private static InfusionSourceFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new InfusionSourceFxPayload(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt(),
                buffer.readVarInt());
    }
}
