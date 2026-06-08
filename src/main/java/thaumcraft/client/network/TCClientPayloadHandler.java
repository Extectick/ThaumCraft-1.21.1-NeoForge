package thaumcraft.client.network;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import thaumcraft.client.fx.BlockZapFxHandler;
import thaumcraft.client.fx.EssentiaSourceFxHandler;
import thaumcraft.client.fx.InfusionSourceFxHandler;
import thaumcraft.client.fx.PedestalSparkleFxHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.ThaumometerScanMessagePayload;
import thaumcraft.common.network.ThaumometerScanFxPayload;
import thaumcraft.common.network.WarpMessagePayload;
import thaumcraft.common.research.ResearchRegistry;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.client.fx.BlockRunesParticle;

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

    public static void handlePedestalSparkleFx(PedestalSparkleFxPayload payload) {
        PedestalSparkleFxHandler.add(payload.pos(), payload.eventId());
    }

    public static void handleThaumometerScanFx(ThaumometerScanFxPayload payload) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null || minecraft.particleEngine == null) {
            return;
        }
        minecraft.particleEngine.add(new BlockRunesParticle(minecraft.level, payload.x() + 0.5D,
                payload.y() + 0.5D, payload.z() + 0.5D, payload.red(), payload.green(), payload.blue(),
                payload.duration(), payload.gravity()));
    }

    public static void handleThaumometerScanMessage(ThaumometerScanMessagePayload payload) {
        if (payload.kind() == ThaumometerScanMessagePayload.COMPLETE) {
            PlayerNotifications.addNotification(Component.translatable("tc.scan.complete", payload.targetName()));
            payload.discoveries().getAspectsSorted().forEach(PlayerNotifications::addDiscovery);
            payload.gains().getAspectsSortedAmount().forEach(aspect ->
                    PlayerNotifications.addResearchPoints(aspect, payload.gains().getAmount(aspect)));
        } else if (payload.kind() == ThaumometerScanMessagePayload.DISCOVERY_ERROR) {
            if (payload.targetName().isBlank()) {
                PlayerNotifications.addNotification(Component.translatable("tc.discoveryerror"));
            } else {
                PlayerNotifications.addNotification(Component.translatable("tc.discoveryerror",
                        Component.translatable("tc.aspect.help." + payload.targetName())));
            }
        } else if (payload.kind() == ThaumometerScanMessagePayload.UNKNOWN_OBJECT) {
            PlayerNotifications.addNotification(Component.translatable("tc.unknownobject"));
        }
    }

    public static void handleWarpMessage(WarpMessagePayload payload) {
        if (payload.amount() == 0 || net.minecraft.client.Minecraft.getInstance().player == null
                || net.minecraft.client.Minecraft.getInstance().level == null) {
            return;
        }
        String key;
        if (payload.warpType() == 0) {
            key = payload.amount() > 0 ? "tc.addwarp" : "tc.removewarp";
        } else if (payload.warpType() == 1) {
            key = payload.amount() > 0 ? "tc.addwarpsticky" : "tc.removewarpsticky";
        } else {
            key = payload.amount() > 0 ? "tc.addwarptemp" : "tc.removewarptemp";
        }
        if (payload.amount() > 0 && payload.warpType() != 2) {
            net.minecraft.client.Minecraft.getInstance().level.playLocalSound(
                    net.minecraft.client.Minecraft.getInstance().player.getX(),
                    net.minecraft.client.Minecraft.getInstance().player.getY(),
                    net.minecraft.client.Minecraft.getInstance().player.getZ(),
                    TCSoundEvents.WHISPERS.get(), SoundSource.PLAYERS, 0.5F, 1.0F, false);
        }
        PlayerNotifications.addNotification(Component.translatable(key));
    }
}
