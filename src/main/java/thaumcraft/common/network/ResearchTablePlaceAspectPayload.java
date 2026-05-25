package thaumcraft.common.network;

import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.menus.ResearchTableMenu;

public record ResearchTablePlaceAspectPayload(int q, int r, Optional<Aspect> aspect) implements CustomPacketPayload {
    public static final Type<ResearchTablePlaceAspectPayload> TYPE = new Type<>(Thaumcraft.id("research_table_place_aspect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTablePlaceAspectPayload> STREAM_CODEC =
            StreamCodec.of(ResearchTablePlaceAspectPayload::encode, ResearchTablePlaceAspectPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ResearchTablePlaceAspectPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof ResearchTableMenu menu) {
            menu.placeAspect(player, new HexUtils.Hex(payload.q, payload.r), payload.aspect);
        }
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
