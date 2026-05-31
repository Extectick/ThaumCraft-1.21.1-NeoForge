package thaumcraft.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record PedestalSparkleFxPayload(BlockPos pos, int eventId) implements CustomPacketPayload {
    public static final Type<PedestalSparkleFxPayload> TYPE = new Type<>(Thaumcraft.id("pedestal_sparkle_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PedestalSparkleFxPayload> STREAM_CODEC =
            StreamCodec.of(PedestalSparkleFxPayload::encode, PedestalSparkleFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, PedestalSparkleFxPayload payload) {
        buffer.writeBlockPos(payload.pos);
        buffer.writeVarInt(payload.eventId);
    }

    private static PedestalSparkleFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new PedestalSparkleFxPayload(buffer.readBlockPos(), buffer.readVarInt());
    }
}
