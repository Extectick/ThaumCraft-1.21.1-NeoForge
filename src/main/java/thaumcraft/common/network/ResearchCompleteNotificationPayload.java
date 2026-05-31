package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record ResearchCompleteNotificationPayload(String key) implements CustomPacketPayload {
    public static final Type<ResearchCompleteNotificationPayload> TYPE =
            new Type<>(Thaumcraft.id("research_complete_notification"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCompleteNotificationPayload> STREAM_CODEC =
            StreamCodec.of(ResearchCompleteNotificationPayload::encode, ResearchCompleteNotificationPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchCompleteNotificationPayload payload) {
        buffer.writeUtf(payload.key);
    }

    private static ResearchCompleteNotificationPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ResearchCompleteNotificationPayload(buffer.readUtf());
    }
}
