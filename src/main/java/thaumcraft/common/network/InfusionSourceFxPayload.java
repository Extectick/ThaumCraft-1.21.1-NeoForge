package thaumcraft.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record InfusionSourceFxPayload(BlockPos target, BlockPos source, int color, int ticks, int sourceEntityId)
        implements CustomPacketPayload {
    public static final Type<InfusionSourceFxPayload> TYPE = new Type<>(Thaumcraft.id("infusion_source_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionSourceFxPayload> STREAM_CODEC =
            StreamCodec.of(InfusionSourceFxPayload::encode, InfusionSourceFxPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, InfusionSourceFxPayload payload) {
        buffer.writeBlockPos(payload.target);
        buffer.writeBlockPos(payload.source);
        buffer.writeInt(payload.color);
        buffer.writeVarInt(payload.ticks);
        buffer.writeVarInt(payload.sourceEntityId);
    }

    private static InfusionSourceFxPayload decode(RegistryFriendlyByteBuf buffer) {
        return new InfusionSourceFxPayload(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt(),
                buffer.readVarInt(), buffer.readVarInt());
    }
}
