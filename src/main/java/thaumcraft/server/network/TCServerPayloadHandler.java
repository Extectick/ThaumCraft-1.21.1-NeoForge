package thaumcraft.server.network;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.wands.FocusPouchContents;
import thaumcraft.common.items.wands.WandFocusHelper;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.menus.ResearchTableMenu;
import thaumcraft.common.network.CycleWandFocusPayload;
import thaumcraft.common.network.ResearchTableCombineAspectPayload;
import thaumcraft.common.network.ResearchTablePlaceAspectPayload;
import thaumcraft.common.network.ThaumonomiconCreateNotePayload;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchManager;

public final class TCServerPayloadHandler {
    private TCServerPayloadHandler() {
    }

    public static void handleCycleWandFocus(CycleWandFocusPayload payload, ServerPlayer player) {
        cycleFocus(player);
    }

    public static void handleResearchTablePlaceAspect(ResearchTablePlaceAspectPayload payload, ServerPlayer player) {
        if (player.containerMenu instanceof ResearchTableMenu menu) {
            menu.placeAspect(player, new HexUtils.Hex(payload.q(), payload.r()), payload.aspect());
        }
    }

    public static void handleResearchTableCombineAspect(ResearchTableCombineAspectPayload payload, ServerPlayer player) {
        if (player.containerMenu instanceof ResearchTableMenu menu) {
            menu.combineAspects(player, payload.first(), payload.second());
        }
    }

    public static void handleThaumonomiconCreateNote(ThaumonomiconCreateNotePayload payload, ServerPlayer player) {
        ResearchManager.tryCreateResearchNote(player, payload.researchKey());
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
        FocusPouchContents contents = pouch.getOrDefault(TCDataComponents.FOCUS_POUCH_CONTENTS,
                FocusPouchContents.EMPTY);
        ItemStack currentFocus = WandFocusHelper.getFocusItem(wand);
        int nextSlot = findNextUsableFocus(contents, currentFocus);

        if (nextSlot >= 0) {
            ItemStack nextFocus = contents.get(nextSlot);
            WandFocusHelper.setFocus(wand, nextFocus);
            pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS, contents.with(nextSlot, currentFocus));
            player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.set",
                    nextFocus.getHoverName()), true);
            player.level().playSound(null, player.blockPosition(), TCSoundEvents.HHON.get(), SoundSource.PLAYERS,
                    0.35F, 1.0F);
            return;
        }

        if (!currentFocus.isEmpty()) {
            Optional<FocusPouchContents> updated = contents.addFocus(currentFocus);
            updated.ifPresent(newContents -> {
                WandFocusHelper.removeFocus(wand);
                pouch.set(TCDataComponents.FOCUS_POUCH_CONTENTS, newContents);
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.removed"), true);
                player.level().playSound(null, player.blockPosition(), TCSoundEvents.HHOFF.get(),
                        SoundSource.PLAYERS, 0.35F, 1.0F);
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
