package thaumcraft.common.network;

import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;

public record ResearchTablePlaceAspectPayload(int q, int r, Optional<Aspect> aspect) implements CustomPacketPayload {
    public static final Type<ResearchTablePlaceAspectPayload> TYPE = new Type<>(Thaumcraft.id("research_table_place_aspect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTablePlaceAspectPayload> STREAM_CODEC =
            StreamCodec.of(ResearchTablePlaceAspectPayload::encode, ResearchTablePlaceAspectPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchTablePlaceAspectPayload payload) {
        buffer.writeVarInt(payload.q);
        buffer.writeVarInt(payload.r);
        buffer.writeBoolean(payload.aspect.isPresent());
        payload.aspect.ifPresent(aspect -> Aspect.STREAM_CODEC.encode(buffer, aspect));
    }

    private static ResearchTablePlaceAspectPayload decode(RegistryFriendlyByteBuf buffer) {
        int q = buffer.readVarInt();
        int r = buffer.readVarInt();
        Optional<Aspect> aspect = buffer.readBoolean() ? Optional.of(Aspect.STREAM_CODEC.decode(buffer))
                : Optional.empty();
        return new ResearchTablePlaceAspectPayload(q, r, aspect);
    }
}
