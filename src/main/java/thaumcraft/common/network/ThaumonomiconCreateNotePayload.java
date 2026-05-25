package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.research.ResearchManager;

public record ThaumonomiconCreateNotePayload(String researchKey) implements CustomPacketPayload {
    public static final Type<ThaumonomiconCreateNotePayload> TYPE =
            new Type<>(Thaumcraft.id("thaumonomicon_create_note"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ThaumonomiconCreateNotePayload> STREAM_CODEC =
            StreamCodec.of(ThaumonomiconCreateNotePayload::encode, ThaumonomiconCreateNotePayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ThaumonomiconCreateNotePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ResearchManager.tryCreateResearchNote(player, payload.researchKey);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ThaumonomiconCreateNotePayload payload) {
        buffer.writeUtf(payload.researchKey, 128);
    }

    private static ThaumonomiconCreateNotePayload decode(RegistryFriendlyByteBuf buffer) {
        return new ThaumonomiconCreateNotePayload(buffer.readUtf(128));
    }
}
