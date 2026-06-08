package thaumcraft.server.aura;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.blockentities.NodeJarBlockEntity;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchManager;

public final class ServerNodeJarService {
    private static final PrimalVisStorage CAPTURE_COST =
            new PrimalVisStorage(7000, 7000, 7000, 7000, 7000, 7000);

    private ServerNodeJarService() {
    }

    public static InteractionResult tryCapture(Level level, BlockPos clickedPos, ItemStack wand, Player player) {
        if (!level.getBlockState(clickedPos).is(Tags.Blocks.GLASS_BLOCKS)
                || !(wand.getItem() instanceof WandCastingItem wandItem)
                || player == null) {
            return InteractionResult.PASS;
        }

        BlockPos center = findCenter(level, clickedPos);
        if (center == null) {
            return InteractionResult.PASS;
        }

        if (!ResearchManager.isComplete(player, "NODEJAR")) {
            if (!level.isClientSide) {
                level.playSound(null, clickedPos, TCSoundEvents.WANDFAIL.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.getAbilities().instabuild
                && !wandItem.consumeAllVis(wand, player, CAPTURE_COST, true)) {
            level.playSound(null, clickedPos, TCSoundEvents.WANDFAIL.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
            return InteractionResult.FAIL;
        }

        capture(level, center);
        return InteractionResult.CONSUME;
    }

    @Nullable
    private static BlockPos findCenter(Level level, BlockPos clickedPos) {
        for (int x = clickedPos.getX() - 2; x <= clickedPos.getX(); x++) {
            for (int y = clickedPos.getY() - 3; y <= clickedPos.getY(); y++) {
                for (int z = clickedPos.getZ() - 2; z <= clickedPos.getZ(); z++) {
                    BlockPos center = new BlockPos(x + 1, y, z + 1);
                    if (fits(level, center)) {
                        return center;
                    }
                }
            }
        }
        return null;
    }

    private static boolean fits(Level level, BlockPos center) {
        for (int dy = -1; dy <= 2; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (dy == 2) {
                        BlockState state = level.getBlockState(pos);
                        if (!state.is(BlockTags.WOODEN_SLABS)
                                || state.hasProperty(SlabBlock.TYPE)
                                && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                            return false;
                        }
                    } else if (dy == 0 && dx == 0 && dz == 0) {
                        if (!(level.getBlockEntity(pos) instanceof AuraNodeBlockEntity)) {
                            return false;
                        }
                    } else if (!level.getBlockState(pos).is(Tags.Blocks.GLASS_BLOCKS)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void capture(Level level, BlockPos center) {
        if (!(level.getBlockEntity(center) instanceof AuraNodeBlockEntity node)) {
            return;
        }

        AspectList current = node.getAspects().copy();
        NodeModifier modifier = damagedModifier(node.getNodeModifier(), level);
        UUID id = parseNodeId(node.getNodeId());
        NodeJarData data = new NodeJarData(id, node.getNodeType(), modifier, current, current);

        for (int dy = -1; dy <= 2; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.removeBlock(center.offset(dx, dy, dz), false);
                }
            }
        }

        level.setBlock(center, TCBlocks.NODE_IN_A_JAR.get().defaultBlockState(), 3);
        if (level.getBlockEntity(center) instanceof NodeJarBlockEntity jar) {
            jar.setNodeData(data);
        }
        level.blockEvent(center, TCBlocks.NODE_IN_A_JAR.get(), 9, 0);
        sendCaptureSparkles(level, center);
        level.playSound(null, center, TCSoundEvents.WAND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Nullable
    private static NodeModifier damagedModifier(@Nullable NodeModifier modifier, Level level) {
        if (level.random.nextFloat() >= 0.75F) {
            return modifier;
        }
        if (modifier == null) {
            return NodeModifier.PALE;
        }
        return switch (modifier) {
            case BRIGHT -> null;
            case PALE, FADING -> NodeModifier.FADING;
        };
    }

    private static UUID parseNodeId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            return UUID.randomUUID();
        }
    }

    private static void sendCaptureSparkles(Level level, BlockPos center) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int dy = -1; dy <= 2; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos sparklePos = center.offset(dx, dy, dz);
                    PacketDistributor.sendToPlayersNear(serverLevel, null, center.getX(), center.getY(),
                            center.getZ(), 32.0D, new PedestalSparkleFxPayload(sparklePos, 13));
                }
            }
        }
    }
}
