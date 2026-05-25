package thaumcraft.client.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ThaumcraftConfig;

public final class PlayerNotifications {
    public static final long ASPECT_NOTIFICATION_DURATION_MS = 1500L;

    private static List<Notification> notificationList = new ArrayList<>();
    private static List<AspectNotification> aspectList = new ArrayList<>();
    private static long lastSoundMillis;

    private PlayerNotifications() {
    }

    public static void addNotification(String text) {
        addNotification(Component.literal(text), null, 0xFFFFFF);
    }

    public static void addNotification(Component text) {
        addNotification(text, null, 0xFFFFFF);
    }

    public static void addNotification(String text, Aspect aspect) {
        addNotification(Component.literal(text), aspect);
    }

    public static void addNotification(Component text, Aspect aspect) {
        if (aspect == null) {
            addNotification(text);
            return;
        }
        addNotification(text, aspectTexture(aspect), aspect.getColor());
    }

    public static void addNotification(String text, ResourceLocation image) {
        addNotification(Component.literal(text), image, 0xFFFFFF);
    }

    public static void addNotification(Component text, ResourceLocation image) {
        addNotification(text, image, 0xFFFFFF);
    }

    public static void addNotification(String text, ResourceLocation image, int color) {
        addNotification(Component.literal(text), image, color);
    }

    public static void addNotification(Component text, ResourceLocation image, int color) {
        long time = nowMillis();
        long delay = notificationDelayMs();
        long timeBonus = notificationList.isEmpty() ? delay / 2L : 0L;
        notificationList.add(new Notification(text, image, time + delay + timeBonus,
                time + notificationFadeInMs(), color));
    }

    public static void addAspectNotification(Aspect aspect) {
        if (aspect == null) {
            return;
        }
        long time = nowMillis() + ThreadLocalRandom.current().nextLong(1000L);
        float x = 0.4F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        float y = 0.4F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        aspectList.add(new AspectNotification(aspect, x, y, time, time + ASPECT_NOTIFICATION_DURATION_MS));
    }

    public static void addResearchPoints(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return;
        }
        addNotification(Component.translatable("tc.addaspectpool", amount, displayName(aspect)), aspect);
        for (int i = 0; i < amount; i++) {
            addAspectNotification(aspect);
        }
        playResearchPointSound();
    }

    public static void addDiscovery(Aspect aspect) {
        if (aspect == null) {
            return;
        }
        addNotification(Component.translatable("tc.addaspectdiscovery", displayName(aspect)), aspectTexture(aspect),
                0xDDAA00);
        playDiscoverySound();
    }

    public static List<Notification> getListAndUpdate(long time) {
        List<Notification> temp = new ArrayList<>();
        boolean first = true;
        for (Notification notification : notificationList) {
            if (notification.expire() >= time) {
                if (!first) {
                    temp.add(new Notification(notification.text(), notification.image(), time + notificationDelayMs(),
                            notification.created(), notification.color()));
                } else {
                    temp.add(notification);
                }
            }
            first = false;
        }
        notificationList = temp;
        return List.copyOf(temp);
    }

    public static List<AspectNotification> getAspectListAndUpdate(long time) {
        List<AspectNotification> temp = new ArrayList<>();
        for (AspectNotification notification : aspectList) {
            if (notification.expire() >= time) {
                temp.add(notification);
            }
        }
        aspectList = temp;
        return List.copyOf(temp);
    }

    public static long nowMillis() {
        return System.nanoTime() / 1_000_000L;
    }

    public static long notificationDelayMs() {
        return ThaumcraftConfig.NOTIFICATION_DELAY_MILLISECONDS.get();
    }

    public static long notificationFadeInMs() {
        return notificationDelayMs() / 4L;
    }

    public static int notificationMax() {
        return ThaumcraftConfig.NOTIFICATION_MAX.get();
    }

    private static void playResearchPointSound() {
        long now = nowMillis();
        if (now <= lastSoundMillis) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            float pitch = 0.9F + minecraft.player.level().random.nextFloat() * 0.2F;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, pitch, 0.1F));
            lastSoundMillis = now + 100L;
        }
    }

    private static void playDiscoverySound() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            float pitch = 0.5F + minecraft.player.level().random.nextFloat() * 0.2F;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, pitch, 0.2F));
        }
    }

    private static String displayName(Aspect aspect) {
        String tag = aspect.getTag();
        return Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
    }

    private static ResourceLocation aspectTexture(Aspect aspect) {
        return Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png");
    }

    public record AspectNotification(Aspect aspect, float startX, float startY, long created, long expire) {
    }

    public record Notification(Component text, ResourceLocation image, long expire, long created, int color) {
    }
}
