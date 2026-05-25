package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.items.ResearchNotesItem;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.registry.TCDataAttachments;

public final class ResearchManager {
    private ResearchManager() {
    }

    public static ResearchKnowledgeData getKnowledge(Player player) {
        return player == null ? ResearchKnowledgeData.EMPTY : player.getData(TCDataAttachments.RESEARCH_KNOWLEDGE);
    }

    public static boolean isComplete(Player player, String key) {
        return getKnowledge(player).isComplete(key) || isAutoUnlocked(key);
    }

    public static ResearchStatus getStatus(Player player, String key) {
        Optional<ResearchEntry> entry = ResearchRegistry.get(key);
        if (entry.isEmpty()) {
            return ResearchStatus.LOCKED;
        }
        if (isComplete(player, key)) {
            return ResearchStatus.COMPLETE;
        }
        return canStart(player, entry.get()) ? ResearchStatus.AVAILABLE : ResearchStatus.LOCKED;
    }

    public static boolean canStart(Player player, String key) {
        Optional<ResearchEntry> entry = ResearchRegistry.get(key);
        return entry.isPresent() && canStart(player, entry.get());
    }

    public static boolean canStart(Player player, ResearchEntry entry) {
        if (player == null || entry == null) {
            return false;
        }
        if (isComplete(player, entry.key())) {
            return false;
        }
        ResearchKnowledgeData knowledge = getKnowledge(player);
        return hasRequisites(knowledge, entry);
    }

    public static boolean hasRequisites(Player player, String key) {
        Optional<ResearchEntry> entry = ResearchRegistry.get(key);
        return entry.isPresent() && hasRequisites(getKnowledge(player), entry.get());
    }

    public static boolean hasRequisites(ResearchKnowledgeData knowledge, ResearchEntry entry) {
        return containsAll(knowledge, entry.parents()) && containsAll(knowledge, entry.hiddenParents());
    }

    public static boolean grantResearchTree(Player player, String key) {
        if (player == null || ResearchRegistry.get(key).isEmpty()) {
            return false;
        }
        List<String> keys = new ArrayList<>();
        collectResearchTree(key, keys, new HashSet<>());
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, getKnowledge(player).completeAll(keys));
        player.syncData(TCDataAttachments.RESEARCH_KNOWLEDGE);
        return true;
    }

    public static int grantAllResearch(Player player) {
        if (player == null) {
            return 0;
        }
        List<String> keys = ResearchRegistry.entries().stream().map(ResearchEntry::key).toList();
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, getKnowledge(player).completeAll(keys));
        player.syncData(TCDataAttachments.RESEARCH_KNOWLEDGE);
        return keys.size();
    }

    public static int resetResearch(Player player) {
        if (player == null) {
            return 0;
        }
        List<String> autoUnlock = ResearchRegistry.entries().stream()
                .filter(entry -> entry.hasFlag(ResearchEntry.ResearchFlag.AUTO_UNLOCK))
                .map(ResearchEntry::key)
                .toList();
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, ResearchKnowledgeData.EMPTY.completeAll(autoUnlock));
        player.syncData(TCDataAttachments.RESEARCH_KNOWLEDGE);
        return autoUnlock.size();
    }

    public static ItemStack createNoteFor(Player player, String key) {
        if (!canStart(player, key)) {
            return ItemStack.EMPTY;
        }
        return ResearchNotesItem.create(key);
    }

    public static boolean hasResearchNote(Player player, String key) {
        if (player == null || key == null || key.isBlank()) {
            return false;
        }
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            ResearchNoteData data = stack.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
            if (!data.isEmpty() && !data.complete() && key.equals(data.key())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasScribingToolsAndPaper(Player player) {
        return player != null && (player.getAbilities().instabuild
                || (findUsableScribingToolsSlot(player) >= 0 && findPaperSlot(player) >= 0));
    }

    public static boolean tryCreateResearchNote(ServerPlayer player, String key) {
        if (player == null || !canStart(player, key)) {
            return false;
        }
        if (!hasScribingToolsAndPaper(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("tc.research.shortprim"), true);
            return false;
        }
        if (hasResearchNote(player, key)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("tc.research.hasnote"), true);
            return false;
        }

        ItemStack note = createNoteFor(player, key);
        if (note.isEmpty()) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            consumePaper(player);
            consumeScribingToolsInk(player);
        }
        if (!player.getInventory().add(note)) {
            player.drop(note, false);
        }
        player.level().playSound(null, player.blockPosition(), TCSoundEvents.WRITE.get(), SoundSource.PLAYERS,
                0.65F, 1.0F);
        return true;
    }

    public static boolean completeResearch(Player player, String key) {
        if (player == null || key == null || key.isBlank() || ResearchRegistry.get(key).isEmpty()) {
            return false;
        }
        ResearchKnowledgeData knowledge = getKnowledge(player);
        if (knowledge.isComplete(key)) {
            return false;
        }
        ResearchKnowledgeData completed = knowledge.complete(key);
        for (String sibling : ResearchRegistry.get(key).orElseThrow().siblings()) {
            Optional<ResearchEntry> siblingEntry = ResearchRegistry.get(sibling);
            if (siblingEntry.isPresent() && hasRequisites(completed, siblingEntry.get())) {
                completed = completed.complete(sibling);
            }
        }
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, completed);
        player.syncData(TCDataAttachments.RESEARCH_KNOWLEDGE);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ResearchCompleteNotificationPayload(key));
        }
        return true;
    }

    public static Optional<String> findHiddenResearch(Player player) {
        if (player == null) {
            return Optional.empty();
        }
        return ResearchRegistry.entries().stream()
                .filter(entry -> entry.hasFlag(ResearchEntry.ResearchFlag.HIDDEN))
                .filter(entry -> !isComplete(player, entry.key()))
                .filter(entry -> canStart(player, entry))
                .filter(entry -> !entry.tags().isEmpty())
                .map(ResearchEntry::key)
                .findAny();
    }

    private static void collectResearchTree(String key, List<String> keys, Set<String> visited) {
        if (!visited.add(key)) {
            return;
        }
        Optional<ResearchEntry> entry = ResearchRegistry.get(key);
        if (entry.isEmpty()) {
            return;
        }
        for (String parent : entry.get().parents()) {
            collectResearchTree(parent, keys, visited);
        }
        for (String parent : entry.get().hiddenParents()) {
            collectResearchTree(parent, keys, visited);
        }
        keys.add(key);
        for (String sibling : entry.get().siblings()) {
            collectResearchTree(sibling, keys, visited);
        }
    }

    private static boolean containsAll(ResearchKnowledgeData knowledge, Set<String> keys) {
        for (String key : keys) {
            if (!knowledge.isComplete(key) && !isAutoUnlocked(key)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAutoUnlocked(String key) {
        return ResearchRegistry.get(key)
                .map(entry -> entry.hasFlag(ResearchEntry.ResearchFlag.AUTO_UNLOCK))
                .orElse(false);
    }

    private static int findPaperSlot(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(Items.PAPER)) {
                return slot;
            }
        }
        return -1;
    }

    private static int findUsableScribingToolsSlot(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(TCItems.SCRIBING_TOOLS.get())
                    && (!stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage())) {
                return slot;
            }
        }
        return -1;
    }

    private static void consumePaper(Player player) {
        int slot = findPaperSlot(player);
        if (slot >= 0) {
            player.getInventory().getItem(slot).shrink(1);
        }
    }

    private static void consumeScribingToolsInk(Player player) {
        int slot = findUsableScribingToolsSlot(player);
        if (slot < 0) {
            return;
        }
        ItemStack tools = player.getInventory().getItem(slot);
        if (tools.isDamageableItem()) {
            tools.setDamageValue(Math.min(tools.getDamageValue() + 1, tools.getMaxDamage()));
        }
    }
}
