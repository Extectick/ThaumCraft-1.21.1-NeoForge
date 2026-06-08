package thaumcraft.server.research;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.items.JarBlockItem;
import thaumcraft.common.network.ThaumometerScanMessagePayload;
import thaumcraft.common.network.ThaumometerScanFxPayload;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.AspectPoolData;
import thaumcraft.common.research.AuraNodeScan;
import thaumcraft.common.research.EntityAspectRegistry;
import thaumcraft.common.research.ScanResult;
import thaumcraft.common.research.ScannableBlockAspectRegistry;
import thaumcraft.common.research.ScannedKnowledgeData;
import thaumcraft.common.research.ThaumometerRaycast;

public final class ServerThaumometerScanService {
    private static final int SCAN_RANGE = 10;
    private static final int COMPLETE_TICK = 20;
    private static final Map<UUID, PendingScan> PENDING = new HashMap<>();

    private ServerThaumometerScanService() {
    }

    public static InteractionResult startScan(Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer) || !player.getItemInHand(hand).is(TCItems.THAUMOMETER.get())) {
            return InteractionResult.CONSUME;
        }

        Optional<ScanResult> scan = findScanTarget(serverPlayer);
        if (scan.isEmpty()) {
            PENDING.remove(serverPlayer.getUUID());
            return InteractionResult.CONSUME;
        }

        PENDING.put(serverPlayer.getUUID(), new PendingScan(hand, scan.get(), 0));
        tickEffects(serverPlayer, scan.get());
        return InteractionResult.CONSUME;
    }

    public static InteractionResult startBlockScan(Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(player instanceof ServerPlayer serverPlayer) || !player.getItemInHand(hand).is(TCItems.THAUMOMETER.get())) {
            return InteractionResult.CONSUME;
        }

        Optional<ScanResult> entityScan = findEntityScanTarget(serverPlayer);
        Optional<ScanResult> scan = entityScan.isPresent() ? entityScan : findBlockScanTarget(serverPlayer);
        if (scan.isEmpty()) {
            PENDING.remove(serverPlayer.getUUID());
            return InteractionResult.CONSUME;
        }

        PENDING.put(serverPlayer.getUUID(), new PendingScan(hand, scan.get(), 0));
        tickEffects(serverPlayer, scan.get());
        return InteractionResult.CONSUME;
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PendingScan pending = PENDING.get(player.getUUID());
        if (pending == null) {
            return;
        }

        if (!player.isUsingItem() || player.getUsedItemHand() != pending.hand()
                || !player.getItemInHand(pending.hand()).is(TCItems.THAUMOMETER.get())) {
            PENDING.remove(player.getUUID());
            return;
        }

        Optional<ScanResult> current = findScanTarget(player);
        if (current.isEmpty() || !pending.scan().sameTarget(current.get())) {
            PENDING.remove(player.getUUID());
            return;
        }

        int ticks = pending.ticks() + 1;
        ScanResult scan = current.get();
        if (ticks % 2 == 0) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), TCSoundEvents.CAMERATICKS.get(),
                    SoundSource.PLAYERS, 0.2F, 0.45F + player.level().random.nextFloat() * 0.1F);
        }
        tickEffects(player, scan);

        if (ticks >= COMPLETE_TICK) {
            PENDING.remove(player.getUUID());
            completeScan(player, scan);
            return;
        }

        PENDING.put(player.getUUID(), new PendingScan(pending.hand(), pending.scan(), ticks));
    }

    private static Optional<ScanResult> findScanTarget(ServerPlayer player) {
        Optional<ScanResult> entity = findEntityScanTarget(player);
        if (entity.isPresent()) {
            return entity;
        }

        return findBlockScanTarget(player);
    }

    private static Optional<ScanResult> findBlockScanTarget(ServerPlayer player) {
        HitResult hit = ThaumometerRaycast.pick(player.level(), player, SCAN_RANGE);
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK) {
            return Optional.empty();
        }
        return scanBlock(player, blockHit);
    }

    private static Optional<ScanResult> findEntityScanTarget(ServerPlayer player) {
        return ThaumometerRaycast.pickEntity(player.level(), player, SCAN_RANGE,
                        ServerThaumometerScanService::isScannableEntity)
                .flatMap(hit -> scanEntity(player, hit.getEntity()));
    }

    private static boolean isScannableEntity(Entity entity) {
        return entity.isAlive() && (entity.isPickable() || entity instanceof ItemEntity);
    }

    private static Optional<ScanResult> scanEntity(ServerPlayer player, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem().copyWithCount(1);
            return scanObject(player, stack, entity.position().add(-0.5D, entity.getBbHeight() * 0.5D - 0.5D, -0.5D),
                    Math.max(1, (int)(entity.getBbHeight() * 15.0F)));
        }

        String key = EntityAspectRegistry.entityKey(entity);
        AspectList aspects = EntityAspectRegistry.getEntityAspects(entity);
        ScanResult scan = new ScanResult(ScanResult.Kind.ENTITY, key, ItemStack.EMPTY, aspects,
                entity.position().add(-0.5D, entity.getBbHeight() * 0.5D - 0.5D, -0.5D),
                Math.max(1, (int)(entity.getBbHeight() * 15.0F)), entity.getDisplayName().getString());
        return isValidTarget(player, scan) ? Optional.of(scan) : Optional.empty();
    }

    private static Optional<ScanResult> scanBlock(ServerPlayer player, BlockHitResult hitResult) {
        Level level = player.level();
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.AIR)) {
            return Optional.empty();
        }
        if (level.getBlockEntity(pos) instanceof INode node) {
            ScanResult scan = new ScanResult(ScanResult.Kind.PHENOMENA, AuraNodeScan.key(node), ItemStack.EMPTY,
                    AuraNodeScan.aspects(node), new Vec3(pos.getX(), pos.getY() + 0.25D, pos.getZ()), 15,
                    AuraNodeScan.name(node).getString());
            return isValidTarget(player, scan) ? Optional.of(scan) : Optional.empty();
        }
        if (level.getBlockEntity(pos) instanceof WardedJarBlockEntity jar && !jar.getEssentia().isEmpty()) {
            return Optional.empty();
        }

        Optional<ScannableBlockAspectRegistry.Entry> special = ScannableBlockAspectRegistry.get(state);
        if (special.isPresent()) {
            ScannableBlockAspectRegistry.Entry entry = special.get();
            return scanSpecialObject(player, entry.key(), entry.aspects(),
                    new Vec3(pos.getX(), pos.getY() + 0.25D, pos.getZ()), entry.name().getString());
        }
        ItemStack stack = stackForState(state);
        return scanObject(player, stack, new Vec3(pos.getX(), pos.getY() + 0.25D, pos.getZ()), 15);
    }

    private static ItemStack stackForState(BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        return stack.isEmpty() ? new ItemStack(state.getBlock()) : stack;
    }

    private static Optional<ScanResult> scanObject(ServerPlayer player, ItemStack stack, Vec3 effectCenter,
            int runeDuration) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        if (stack.getItem() instanceof JarBlockItem
                && !stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY).isEmpty()) {
            return Optional.empty();
        }
        String key = ScanResult.objectKey(stack);
        AspectList aspects = ObjectAspectRegistry.getObjectTagsWithBonus(stack);
        ScanResult scan = new ScanResult(ScanResult.Kind.OBJECT, key, stack, aspects, effectCenter, runeDuration);
        return isValidTarget(player, scan) ? Optional.of(scan) : Optional.empty();
    }

    private static Optional<ScanResult> scanSpecialObject(ServerPlayer player, String key, AspectList aspects,
            Vec3 effectCenter, String displayName) {
        ScanResult scan = new ScanResult(ScanResult.Kind.OBJECT, key, ItemStack.EMPTY, aspects, effectCenter, 15,
                displayName);
        return isValidTarget(player, scan) ? Optional.of(scan) : Optional.empty();
    }

    private static boolean isValidTarget(ServerPlayer player, ScanResult scan) {
        if (scan == null) {
            return false;
        }
        return !player.getData(TCDataAttachments.SCANNED_KNOWLEDGE).has(scan);
    }

    private static void completeScan(ServerPlayer player, ScanResult scan) {
        if (!validScan(scan.aspects(), player)) {
            PacketDistributor.sendToPlayer(player, new ThaumometerScanMessagePayload(
                    scan.aspects().isEmpty() ? ThaumometerScanMessagePayload.UNKNOWN_OBJECT
                            : ThaumometerScanMessagePayload.DISCOVERY_ERROR,
                    firstMissingParent(player, scan.aspects()), AspectList.EMPTY));
            return;
        }

        ScannedKnowledgeData scanned = player.getData(TCDataAttachments.SCANNED_KNOWLEDGE);
        if (scanned.has(scan)) {
            return;
        }
        player.setData(TCDataAttachments.SCANNED_KNOWLEDGE, scanned.add(scan));

        AspectPoolData pool = player.getData(TCDataAttachments.ASPECT_POOL);
        AspectList gains = new AspectList();
        AspectList discoveries = new AspectList();
        for (Aspect aspect : scan.aspects().getAspects()) {
            if (canLearnAspect(player, aspect)) {
                int before = pool.get(aspect);
                boolean discoveredBefore = pool.isDiscovered(aspect);
                pool = pool.learn(aspect, scan.aspects().getAmount(aspect) + 1);
                int gained = pool.get(aspect) - before;
                if (gained > 0) {
                    gains.add(aspect, gained);
                }
                if (!discoveredBefore && pool.isDiscovered(aspect)) {
                    discoveries.add(aspect, 1);
                }
            }
        }
        player.setData(TCDataAttachments.ASPECT_POOL, pool);
        PacketDistributor.sendToPlayer(player, new ThaumometerScanMessagePayload(
                ThaumometerScanMessagePayload.COMPLETE, targetName(scan), gains, discoveries));
    }

    private static boolean validScan(AspectList aspects, ServerPlayer player) {
        if (aspects.isEmpty()) {
            return false;
        }
        for (Aspect aspect : aspects.getAspects()) {
            if (!canLearnAspect(player, aspect)) {
                return false;
            }
        }
        return true;
    }

    private static boolean canLearnAspect(ServerPlayer player, Aspect aspect) {
        if (aspect == null || aspect.isPrimal()) {
            return true;
        }
        AspectPoolData pool = player.getData(TCDataAttachments.ASPECT_POOL);
        Aspect[] components = aspect.getComponents();
        return components != null && components.length == 2 && pool.isDiscovered(components[0])
                && pool.isDiscovered(components[1]);
    }

    private static String firstMissingParent(ServerPlayer player, AspectList aspects) {
        AspectPoolData pool = player.getData(TCDataAttachments.ASPECT_POOL);
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null || aspect.isPrimal()) {
                continue;
            }
            Aspect[] components = aspect.getComponents();
            if (components != null && components.length == 2) {
                if (!pool.isDiscovered(components[0])) {
                    return components[0].getTag();
                }
                if (!pool.isDiscovered(components[1])) {
                    return components[1].getTag();
                }
            }
        }
        return "";
    }

    private static void tickEffects(ServerPlayer player, ScanResult scan) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        float red = 0.3F + player.level().random.nextFloat() * 0.7F;
        float blue = 0.3F + player.level().random.nextFloat() * 0.7F;
        PacketDistributor.sendToPlayersNear(serverLevel, null, scan.effectCenter().x(), scan.effectCenter().y(),
                scan.effectCenter().z(), 32.0D, new ThaumometerScanFxPayload(scan.effectCenter().x(),
                        scan.effectCenter().y(), scan.effectCenter().z(), red, 0.0F, blue, scan.runeDuration(),
                        0.03F));
    }

    private static String targetName(ScanResult scan) {
        if (!scan.stack().isEmpty()) {
            return scan.stack().getHoverName().getString();
        }
        if (!scan.displayName().isBlank()) {
            return scan.displayName();
        }
        return scan.key();
    }

    private record PendingScan(InteractionHand hand, ScanResult scan, int ticks) {
    }
}
