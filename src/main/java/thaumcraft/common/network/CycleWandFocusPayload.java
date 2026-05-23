package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public record CycleWandFocusPayload() implements CustomPacketPayload {
    public static final CycleWandFocusPayload INSTANCE = new CycleWandFocusPayload();
    public static final Type<CycleWandFocusPayload> TYPE = new Type<>(Thaumcraft.id("cycle_wand_focus"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleWandFocusPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CycleWandFocusPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            cycleFocus(player);
        }
    }

    private static void cycleFocus(ServerPlayer player) {
        ItemStack wand = getHeldWand(player);
        if (wand.isEmpty()) {
            return;
        }

        ThaumcraftCuriosCompat.findFocusPouch(player)
                .map(slot -> slot.stack())
                .filter(stack -> stack.getItem() instanceof FocusPouchCurioItem)
                .ifPresent(pouch -> cycleFocus(player, wand, pouch));
    }

    private static ItemStack getHeldWand(Player player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (mainHand.is(TCItems.WAND_CASTING.get())) {
            return mainHand;
        }

        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        return offHand.is(TCItems.WAND_CASTING.get()) ? offHand : ItemStack.EMPTY;
    }

    private static void cycleFocus(ServerPlayer player, ItemStack wand, ItemStack pouch) {
        FocusPouchContents contents = pouch.getOrDefault(TCDataComponents.FOCUS_POUCH_CONTENTS, FocusPouchContents.EMPTY);
        ItemStack currentFocus = WandFocusHelper.getFocusItem(wand);
        int nextSlot = findNextUsableFocus(contents, currentFocus);

        if (nextSlot >= 0) {
            ItemStack nextFocus = contents.get(nextSlot);
            WandFocusHelper.setFocus(wand, nextFocus);
            pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS, contents.with(nextSlot, currentFocus));
            player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.set",
                    nextFocus.getHoverName()), true);
            return;
        }

        if (!currentFocus.isEmpty()) {
            contents.addFocus(currentFocus).ifPresent(updated -> {
                WandFocusHelper.removeFocus(wand);
                pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS, updated);
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.removed"), true);
            });
        }
    }

    private static int findNextUsableFocus(FocusPouchContents contents, ItemStack currentFocus) {
        int fallback = -1;
        for (int slot = 0; slot < FocusPouchContents.SIZE; slot++) {
            ItemStack focus = contents.get(slot);
            if (focus.isEmpty()) {
                continue;
            }

            if (fallback < 0) {
                fallback = slot;
            }

            if (currentFocus.isEmpty() || !ItemStack.isSameItemSameComponents(focus, currentFocus)) {
                return slot;
            }
        }
        return fallback;
    }
}
