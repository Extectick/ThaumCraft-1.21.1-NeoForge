package thaumcraft.client.network;

import net.minecraft.network.chat.Component;
import thaumcraft.client.fx.BlockZapFxHandler;
import thaumcraft.client.fx.EssentiaSourceFxHandler;
import thaumcraft.client.fx.InfusionSourceFxHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
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

    public static void handleEssentiaSourceFx(EssentiaSourceFxPayload payload) {
        EssentiaSourceFxHandler.add(payload.target(), payload.source(), payload.color());
    }

    public static void handleInfusionSourceFx(InfusionSourceFxPayload payload) {
        InfusionSourceFxHandler.add(payload.target(), payload.source(), payload.color(), payload.ticks());
    }

    public static void handleBlockZapFx(BlockZapFxPayload payload) {
        BlockZapFxHandler.add(payload.fromX(), payload.fromY(), payload.fromZ(), payload.toX(), payload.toY(),
                payload.toZ());
    }
}
