package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;

public record ResearchTableCombineAspectPayload(Aspect first, Aspect second) implements CustomPacketPayload {
    public static final Type<ResearchTableCombineAspectPayload> TYPE =
            new Type<>(Thaumcraft.id("research_table_combine_aspect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTableCombineAspectPayload> STREAM_CODEC =
            StreamCodec.of(ResearchTableCombineAspectPayload::encode, ResearchTableCombineAspectPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchTableCombineAspectPayload payload) {
        Aspect.STREAM_CODEC.encode(buffer, payload.first);
        Aspect.STREAM_CODEC.encode(buffer, payload.second);
    }

    private static ResearchTableCombineAspectPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ResearchTableCombineAspectPayload(Aspect.STREAM_CODEC.decode(buffer), Aspect.STREAM_CODEC.decode(buffer));
    }
}
