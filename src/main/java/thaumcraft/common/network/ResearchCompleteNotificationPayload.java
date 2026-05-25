package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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

    public static void handle(ResearchCompleteNotificationPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist.isClient()) {
            handleClient(payload);
        }
    }

    private static void handleClient(ResearchCompleteNotificationPayload payload) {
        try {
            Class<?> handler = Class.forName("thaumcraft.client.network.TCClientPayloadHandler");
            handler.getMethod("handleResearchComplete", ResearchCompleteNotificationPayload.class)
                    .invoke(null, payload);
        } catch (ReflectiveOperationException exception) {
            Thaumcraft.LOGGER.warn("Unable to handle research completion notification", exception);
        }
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchCompleteNotificationPayload payload) {
        buffer.writeUtf(payload.key);
    }

    private static ResearchCompleteNotificationPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ResearchCompleteNotificationPayload(buffer.readUtf());
    }
}
