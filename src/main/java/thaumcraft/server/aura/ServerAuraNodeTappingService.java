package thaumcraft.server.aura;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.items.wands.WandParts;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ThaumometerRaycast;

public final class ServerAuraNodeTappingService {
    private static final int CENTIVIS_PER_NODE_VIS = 100;
    private static final Map<UUID, ActiveTap> ACTIVE_TAPS = new ConcurrentHashMap<>();

    private ServerAuraNodeTappingService() {
    }

    public static void start(ServerPlayer player, InteractionHand hand, BlockPos nodePos) {
        stop(player);
        ACTIVE_TAPS.put(player.getUUID(), new ActiveTap(player.level().dimension(), nodePos.immutable(), hand));
    }

    public static void tick(ServerPlayer player, ItemStack wandStack, int remainingUseDuration) {
        ActiveTap tap = ACTIVE_TAPS.get(player.getUUID());
        if (tap == null || tap.hand() != player.getUsedItemHand()
                || !tap.dimension().equals(player.level().dimension())
                || !(wandStack.getItem() instanceof WandCastingItem wand)) {
            stopUsing(player);
            return;
        }

        HitResult hit = ThaumometerRaycast.pick(player.level(), player, player.blockInteractionRange());
        if (!(hit instanceof BlockHitResult blockHit) || !blockHit.getBlockPos().equals(tap.nodePos())
                || !(player.level().getBlockEntity(tap.nodePos()) instanceof AuraNodeBlockEntity node)) {
            stopUsing(player);
            return;
        }

        if (remainingUseDuration % 5 != 0) {
            return;
        }

        int amount = 1;
        if (ResearchManager.isComplete(player, "NODETAPPER1")) {
            amount++;
        }
        if (ResearchManager.isComplete(player, "NODETAPPER2")) {
            amount++;
        }

        boolean preserve = !player.isShiftKeyDown()
                && ResearchManager.isComplete(player, "NODEPRESERVE")
                && !WandParts.WOOD_ROD.equals(wand.getRod(wandStack))
                && !WandParts.IRON_CAP.equals(wand.getCap(wandStack));

        List<Aspect> candidates = new ArrayList<>();
        int maxVis = wand.getMaxVis(wandStack);
        int minimum = preserve ? 1 : 0;
        for (Aspect aspect : node.getAspects().getAspects()) {
            if (aspect.isPrimal()
                    && node.getAspects().getAmount(aspect) > minimum
                    && maxVis - wand.getVis(wandStack, aspect) >= CENTIVIS_PER_NODE_VIS) {
                candidates.add(aspect);
            }
        }

        if (candidates.isEmpty()) {
            node.clearDrainState(player.getId());
            return;
        }

        Aspect aspect = candidates.get(player.level().random.nextInt(candidates.size()));
        int available = node.getAspects().getAmount(aspect);
        amount = Math.min(amount, available);
        if (preserve && amount == available) {
            amount--;
        }
        int room = Math.max(0, maxVis - wand.getVis(wandStack, aspect)) / CENTIVIS_PER_NODE_VIS;
        amount = Math.min(amount, room);
        if (amount <= 0) {
            node.clearDrainState(player.getId());
            return;
        }

        int inserted = wand.addVis(wandStack, aspect, amount * CENTIVIS_PER_NODE_VIS);
        int accepted = inserted / CENTIVIS_PER_NODE_VIS;
        if (accepted <= 0 || !node.takeFromContainer(aspect, accepted)) {
            node.clearDrainState(player.getId());
            return;
        }

        node.setDrainState(player.getId(), aspect.getColor());
    }

    public static void stop(ServerPlayer player) {
        ActiveTap tap = ACTIVE_TAPS.remove(player.getUUID());
        if (tap == null) {
            return;
        }
        ServerLevel level = player.server.getLevel(tap.dimension());
        if (level != null && level.getBlockEntity(tap.nodePos()) instanceof AuraNodeBlockEntity node) {
            node.clearDrainState(player.getId());
        }
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            stop(player);
        }
    }

    private static void stopUsing(ServerPlayer player) {
        stop(player);
        player.stopUsingItem();
    }

    private record ActiveTap(ResourceKey<Level> dimension, BlockPos nodePos, InteractionHand hand) {
    }
}
