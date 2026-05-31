package thaumcraft.common.research;

import java.util.Optional;
import java.util.Set;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.items.ResearchNotesItem;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.util.ServerResearchHooks;

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
        return ServerResearchHooks.grantResearchTree(player, key);
    }

    public static int grantAllResearch(Player player) {
        return ServerResearchHooks.grantAllResearch(player);
    }

    public static int resetResearch(Player player) {
        return ServerResearchHooks.resetResearch(player);
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
        return ServerResearchHooks.tryCreateResearchNote(player, key);
    }

    public static boolean completeResearch(Player player, String key) {
        return ServerResearchHooks.completeResearch(player, key);
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

}
