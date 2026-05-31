package thaumcraft.server.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.items.ResearchNotesItem;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchEntry;
import thaumcraft.common.research.ResearchKnowledgeData;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchNoteData;
import thaumcraft.common.research.ResearchRegistry;

public final class ServerResearchService {
    private ServerResearchService() {
    }

    public static void tickResearchTable(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity table) {
        if (table.incrementNextRecalc() <= ResearchTableBlockEntity.BONUS_RECALC_INTERVAL) {
            return;
        }
        table.resetNextRecalc();
        if (recalculateResearchTableBonus(level, pos, table)) {
            table.markChangedAndSync(level, pos, state);
        }
    }

    public static boolean grantResearchTree(Player player, String key) {
        if (player == null || ResearchRegistry.get(key).isEmpty()) {
            return false;
        }
        List<String> keys = new ArrayList<>();
        collectResearchTree(key, keys, new HashSet<>());
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, ResearchManager.getKnowledge(player).completeAll(keys));
        player.syncData(TCDataAttachments.RESEARCH_KNOWLEDGE);
        return true;
    }

    private static boolean recalculateResearchTableBonus(Level level, BlockPos pos, ResearchTableBlockEntity table) {
        Aspect found = findEnvironmentalBonus(level, pos, table);
        if (found == null || table.hasBonusAspect(found)) {
            return false;
        }
        table.addBonusAspect(found);
        return true;
    }

    private static Aspect findEnvironmentalBonus(Level level, BlockPos pos, ResearchTableBlockEntity table) {
        if (!level.isDay() && level.getMaxLocalRawBrightness(pos.above()) < 4
                && !level.canSeeSky(pos.above()) && level.random.nextInt(20) == 0) {
            return Aspect.ENTROPY;
        }
        int worldHeight = Math.max(1, level.getHeight());
        if (pos.getY() > worldHeight * 0.75F && level.random.nextInt(20) == 0) {
            return Aspect.AIR;
        }
        if (pos.getY() > worldHeight * 0.66F && level.random.nextInt(20) == 0) {
            return Aspect.AIR;
        }
        if (pos.getY() > worldHeight * 0.5F && level.random.nextInt(20) == 0) {
            return Aspect.AIR;
        }

        for (BlockPos scanPos : BlockPos.betweenClosed(pos.offset(-ResearchTableBlockEntity.BONUS_SCAN_RANGE,
                -ResearchTableBlockEntity.BONUS_SCAN_RANGE, -ResearchTableBlockEntity.BONUS_SCAN_RANGE),
                pos.offset(ResearchTableBlockEntity.BONUS_SCAN_RANGE, ResearchTableBlockEntity.BONUS_SCAN_RANGE,
                        ResearchTableBlockEntity.BONUS_SCAN_RANGE))) {
            BlockState state = level.getBlockState(scanPos);
            Aspect aspect = bonusFromBlock(state);
            if (aspect != null && !table.hasBonusAspect(aspect) && level.random.nextInt(bonusChance(state)) == 0) {
                return aspect;
            }
            if ((state.is(Blocks.BOOKSHELF) || state.is(TCBlocks.WARDED_JAR.get()))
                    && level.random.nextInt(300) == 0) {
                Aspect[] aspects = Aspect.values();
                return aspects[level.random.nextInt(aspects.length)];
            }
        }
        return null;
    }

    private static Aspect bonusFromBlock(BlockState state) {
        if (state.is(TCBlocks.INFUSED_AIR_ORE.get())) {
            return Aspect.AIR;
        }
        if (state.is(TCBlocks.INFUSED_FIRE_ORE.get()) || state.is(Blocks.FIRE) || state.getFluidState().is(FluidTags.LAVA)) {
            return Aspect.FIRE;
        }
        if (state.is(TCBlocks.INFUSED_WATER_ORE.get()) || state.getFluidState().is(FluidTags.WATER)) {
            return Aspect.WATER;
        }
        if (state.is(TCBlocks.INFUSED_EARTH_ORE.get()) || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.STONE)) {
            return Aspect.EARTH;
        }
        if (state.is(TCBlocks.INFUSED_ORDER_ORE.get()) || state.is(Blocks.REDSTONE_WIRE)
                || state.is(Blocks.REPEATER) || state.is(Blocks.COMPARATOR)) {
            return Aspect.ORDER;
        }
        if (state.is(TCBlocks.INFUSED_ENTROPY_ORE.get())) {
            return Aspect.ENTROPY;
        }
        return null;
    }

    private static int bonusChance(BlockState state) {
        return state.is(TCBlocks.INFUSED_AIR_ORE.get()) || state.is(TCBlocks.INFUSED_FIRE_ORE.get())
                || state.is(TCBlocks.INFUSED_WATER_ORE.get()) || state.is(TCBlocks.INFUSED_EARTH_ORE.get())
                || state.is(TCBlocks.INFUSED_ORDER_ORE.get()) || state.is(TCBlocks.INFUSED_ENTROPY_ORE.get()) ? 20 : 30;
    }

    public static int grantAllResearch(Player player) {
        if (player == null) {
            return 0;
        }
        List<String> keys = ResearchRegistry.entries().stream().map(ResearchEntry::key).toList();
        player.setData(TCDataAttachments.RESEARCH_KNOWLEDGE, ResearchManager.getKnowledge(player).completeAll(keys));
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

    public static boolean tryCreateResearchNote(ServerPlayer player, String key) {
        if (player == null || !ResearchManager.canStart(player, key)) {
            return false;
        }
        if (!ResearchManager.hasScribingToolsAndPaper(player)) {
            player.displayClientMessage(Component.translatable("tc.research.shortprim"), true);
            return false;
        }
        if (ResearchManager.hasResearchNote(player, key)) {
            player.displayClientMessage(Component.translatable("tc.research.hasnote"), true);
            return false;
        }

        ItemStack note = ResearchManager.createNoteFor(player, key);
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
        ResearchKnowledgeData knowledge = ResearchManager.getKnowledge(player);
        if (knowledge.isComplete(key)) {
            return false;
        }
        ResearchKnowledgeData completed = knowledge.complete(key);
        for (String sibling : ResearchRegistry.get(key).orElseThrow().siblings()) {
            Optional<ResearchEntry> siblingEntry = ResearchRegistry.get(sibling);
            if (siblingEntry.isPresent() && ResearchManager.hasRequisites(completed, siblingEntry.get())) {
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
