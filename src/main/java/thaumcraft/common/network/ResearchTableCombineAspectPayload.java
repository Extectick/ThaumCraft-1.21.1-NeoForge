package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.menus.ResearchTableMenu;

public record ResearchTableCombineAspectPayload(Aspect first, Aspect second) implements CustomPacketPayload {
    public static final Type<ResearchTableCombineAspectPayload> TYPE =
            new Type<>(Thaumcraft.id("research_table_combine_aspect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTableCombineAspectPayload> STREAM_CODEC =
            StreamCodec.of(ResearchTableCombineAspectPayload::encode, ResearchTableCombineAspectPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ResearchTableCombineAspectPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof ResearchTableMenu menu) {
            menu.combineAspects(player, payload.first, payload.second);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchTableCombineAspectPayload payload) {
        Aspect.STREAM_CODEC.encode(buffer, payload.first);
        Aspect.STREAM_CODEC.encode(buffer, payload.second);
    }

    private static ResearchTableCombineAspectPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ResearchTableCombineAspectPayload(Aspect.STREAM_CODEC.decode(buffer), Aspect.STREAM_CODEC.decode(buffer));
    }
}
