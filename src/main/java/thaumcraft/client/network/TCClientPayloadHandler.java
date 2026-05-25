package thaumcraft.client.network;

import net.minecraft.network.chat.Component;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.research.ResearchRegistry;

public final class TCClientPayloadHandler {
    private TCClientPayloadHandler() {
    }

    public static void handleResearchComplete(ResearchCompleteNotificationPayload payload) {
        ResearchRegistry.get(payload.key()).ifPresent(entry -> PlayerNotifications.addNotification(
                Component.translatable("tc.research.complete", Component.translatable(entry.nameTranslationKey())),
                entry.primaryAspect()));
    }
}
