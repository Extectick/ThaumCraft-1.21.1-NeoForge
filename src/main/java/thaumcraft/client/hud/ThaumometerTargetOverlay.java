package thaumcraft.client.hud;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.items.JarBlockItem;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.EntityAspectRegistry;
import thaumcraft.common.research.AuraNodeScan;
import thaumcraft.common.research.ScanResult;
import thaumcraft.common.research.ScannableBlockAspectRegistry;
import thaumcraft.common.research.ThaumometerRaycast;

public final class ThaumometerTargetOverlay {
    private static final int SCAN_RANGE = 10;
    private static final int ASPECT_ICON_SIZE = 16;
    private static final int ASPECT_TEXTURE_SIZE = 32;

    private ThaumometerTargetOverlay() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.options.hideGui || !isHoldingThaumometer(player)) {
            return;
        }

        Target target = findTarget(minecraft, player).orElse(null);
        if (target == null || target.name().isBlank()) {
            return;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int centerX = width / 2;
        int centerY = Math.max(52, height / 2 - 38);
        int textWidth = minecraft.font.width(target.name());
        guiGraphics.drawString(minecraft.font, target.name(), centerX - textWidth / 2, centerY, 0xEEE6D6, true);

        if (!target.scanned() || target.aspects().isEmpty()) {
            return;
        }

        renderAspects(guiGraphics, minecraft.font, target.aspects(), centerX, centerY + 28);
    }

    private static Optional<Target> findTarget(Minecraft minecraft, LocalPlayer player) {
        Optional<Target> entity = findEntityTarget(player);
        if (entity.isPresent()) {
            return entity;
        }

        HitResult hit = ThaumometerRaycast.pick(player.level(), player, SCAN_RANGE);
        if (hit instanceof BlockHitResult blockHit && blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = player.level().getBlockState(pos);
            if (!state.isAir()) {
                if (player.level().getBlockEntity(pos) instanceof INode node) {
                    boolean scanned = player.getData(TCDataAttachments.SCANNED_KNOWLEDGE).phenomena()
                            .contains(AuraNodeScan.key(node));
                    return Optional.of(new Target(AuraNodeScan.name(node).getString(),
                            scanned ? node.getAspects().copy() : AspectList.EMPTY, scanned));
                }
                if (player.level().getBlockEntity(pos) instanceof WardedJarBlockEntity jar
                        && !jar.getEssentia().isEmpty()) {
                    return Optional.empty();
                }
                Optional<ScannableBlockAspectRegistry.Entry> special = ScannableBlockAspectRegistry.get(state);
                if (special.isPresent()) {
                    return Optional.of(targetFromSpecial(player, special.get()));
                }
                ItemStack stack = stackForState(state);
                return Optional.of(targetFromStack(player, stack));
            }
        }
        return Optional.empty();
    }

    private static Optional<Target> findEntityTarget(LocalPlayer player) {
        return ThaumometerRaycast.pickEntity(player.level(), player, SCAN_RANGE,
                        ThaumometerTargetOverlay::isScannableEntity)
                .map(hit -> targetFromEntity(player, hit.getEntity()));
    }

    private static boolean isScannableEntity(Entity entity) {
        return entity.isAlive() && (entity.isPickable() || entity instanceof ItemEntity);
    }

    private static Target targetFromEntity(LocalPlayer player, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return targetFromStack(player, itemEntity.getItem());
        }
        String key = EntityAspectRegistry.entityKey(entity);
        boolean scanned = player.getData(TCDataAttachments.SCANNED_KNOWLEDGE).entities().contains(key);
        AspectList aspects = scanned ? EntityAspectRegistry.getEntityAspects(entity) : AspectList.EMPTY;
        return new Target(entity.getDisplayName().getString(), aspects, scanned);
    }

    private static Target targetFromStack(LocalPlayer player, ItemStack stack) {
        ItemStack single = stack.copyWithCount(1);
        if (single.getItem() instanceof JarBlockItem
                && !single.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY).isEmpty()) {
            return new Target("", AspectList.EMPTY, false);
        }
        String key = ScanResult.objectKey(single);
        boolean scanned = player.getData(TCDataAttachments.SCANNED_KNOWLEDGE).objects().contains(key);
        AspectList aspects = scanned ? ObjectAspectRegistry.getObjectTagsWithBonus(single) : AspectList.EMPTY;
        return new Target(single.getHoverName().getString(), aspects, scanned);
    }

    private static ItemStack stackForState(BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        return stack.isEmpty() ? new ItemStack(state.getBlock()) : stack;
    }

    private static Target targetFromSpecial(LocalPlayer player, ScannableBlockAspectRegistry.Entry entry) {
        boolean scanned = player.getData(TCDataAttachments.SCANNED_KNOWLEDGE).objects().contains(entry.key());
        AspectList aspects = scanned ? entry.aspects() : AspectList.EMPTY;
        return new Target(entry.name().getString(), aspects, scanned);
    }

    private static void renderAspects(GuiGraphics guiGraphics, Font font, AspectList aspects, int centerX, int y) {
        int index = 0;
        int row = 0;
        int rowStart = 0;
        int rowLimit = Math.min(5, aspects.size());
        for (Aspect aspect : aspects.getAspectsSorted()) {
            if (index - rowStart >= rowLimit) {
                row++;
                rowStart = index;
                rowLimit = Math.min(Math.max(1, 5 - row), aspects.size() - rowStart);
            }
            int rowIndex = index - rowStart;
            int totalWidth = rowLimit * ASPECT_ICON_SIZE;
            int x = centerX - totalWidth / 2 + rowIndex * ASPECT_ICON_SIZE;
            renderAspect(guiGraphics, font, aspect, aspects.getAmount(aspect), x, y + row * ASPECT_ICON_SIZE);
            index++;
        }
    }

    private static void renderAspect(GuiGraphics guiGraphics, Font font, Aspect aspect, int amount, int x, int y) {
        int color = aspect.getColor();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        ResourceLocation texture = Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png");
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(red, green, blue, 1.0F);
        guiGraphics.blit(texture, x, y, ASPECT_ICON_SIZE, ASPECT_ICON_SIZE, 0.0F, 0.0F,
                ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderAmount(font, guiGraphics, amount, x, y);
    }

    private static void renderAmount(Font font, GuiGraphics guiGraphics, int amount, int x, int y) {
        if (amount <= 0) {
            return;
        }
        String text = String.valueOf(amount);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        int textX = (x + ASPECT_ICON_SIZE) * 2 - font.width(text);
        int textY = (y + ASPECT_ICON_SIZE) * 2 - font.lineHeight;
        guiGraphics.drawString(font, text, textX - 1, textY, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX + 1, textY, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY - 1, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY + 1, 0xFF000000, false);
        guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();
    }

    private static boolean isHoldingThaumometer(LocalPlayer player) {
        return player.getMainHandItem().is(TCItems.THAUMOMETER.get())
                || player.getOffhandItem().is(TCItems.THAUMOMETER.get());
    }

    private record Target(String name, AspectList aspects, boolean scanned) {
    }
}
