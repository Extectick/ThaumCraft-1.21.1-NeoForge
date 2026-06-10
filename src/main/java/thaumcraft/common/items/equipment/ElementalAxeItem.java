package thaumcraft.common.items.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.entities.item.FollowingItemEntity;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.items.equipment.TCTiers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ElementalAxeItem extends AxeItem {
    public ElementalAxeItem() {
        super(TCTiers.ELEMENTAL, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(AxeItem.createAttributes(TCTiers.ELEMENTAL, 5.0F, -3.0F)));
    }



    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player && !player.isShiftKeyDown()) {
            if (state.is(BlockTags.LOGS)) {

                BlockEntity blockEntity = level.getBlockEntity(pos);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);


                if (level instanceof ServerLevel serverLevel) {
                    for (ItemStack drop : Block.getDrops(state, serverLevel, pos, blockEntity, player, stack)) {
                        FollowingItemEntity fi = new FollowingItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop, player, 2);
                        level.addFreshEntity(fi);
                    }
                }
                level.levelEvent(2001, pos, Block.getId(state));


                fellConnectedLogs(level, pos, player, stack);
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    /**
     * BFS to find the farthest connected log from the broken position, then break it.
     * Repeats until no more connected logs are found (full tree felling).
     * Limits total blocks broken to prevent infinite recursion on massive forests.
     */
    private void fellConnectedLogs(Level level, BlockPos origin, Player player, ItemStack stack) {
        int maxBlocks = 256;
        int broken = 0;

        while (broken < maxBlocks) {
            BlockPos farthest = findFarthestLog(level, origin, 24, 48);
            if (farthest == null) {
                break;
            }

            BlockState farthestState = level.getBlockState(farthest);
            if (!farthestState.is(BlockTags.LOGS)) {
                break;
            }


            if (level instanceof ServerLevel serverLevel) {
                for (ItemStack drop : Block.getDrops(farthestState, serverLevel, farthest, level.getBlockEntity(farthest), player, stack)) {
                    FollowingItemEntity fi = new FollowingItemEntity(level, farthest.getX() + 0.5, farthest.getY() + 0.5, farthest.getZ() + 0.5, drop, player,
                            2);
                    level.addFreshEntity(fi);
                }
            }

            level.levelEvent(2001, farthest, Block.getId(farthestState));
            level.destroyBlock(farthest, false, player);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            broken++;
        }

        if (broken > 0) {
            level.playSound(null, origin, TCSoundEvents.BUBBLE.get(), SoundSource.BLOCKS, 0.15F, 1.0F);
        }
    }

    /**
     * BFS from origin to find the farthest connected log block.
     * Limits search radius to maxXZ horizontally and maxY vertically.
     */
    private BlockPos findFarthestLog(Level level, BlockPos origin, int maxXZ, int maxY) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin);

        BlockPos farthest = null;
        double farthestDist = 0;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            double dist = current.distSqr(origin);
            if (dist > farthestDist) {
                farthestDist = dist;
                farthest = current;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        BlockPos neighbor = current.offset(dx, dy, dz);

                        if (Math.abs(neighbor.getX() - origin.getX()) > maxXZ) {
                            continue;
                        }
                        if (Math.abs(neighbor.getY() - origin.getY()) > maxY) {
                            continue;
                        }
                        if (Math.abs(neighbor.getZ() - origin.getZ()) > maxXZ) {
                            continue;
                        }
                        if (visited.contains(neighbor)) {
                            continue;
                        }

                        BlockState state = level.getBlockState(neighbor);
                        if (state.is(BlockTags.LOGS) && state.getDestroySpeed(level, neighbor) >= 0) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }


        return farthest != null && !farthest.equals(origin) ? farthest : null;
    }



    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (level.isClientSide) {
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }

        double range = 10.0;
        AABB box = player.getBoundingBox().inflate(range);
        var items = level.getEntitiesOfClass(ItemEntity.class, box, e -> !e.isRemoved() && e.isAlive() && e.distanceTo(player) <= range);

        for (ItemEntity item : items) {
            double dx = player.getX() - item.getX();
            double dy = (player.getY() + player.getBbHeight() / 2.0) - item.getY();
            double dz = player.getZ() - item.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0.5) {
                continue;
            }

            double speed = 0.3;
            item.setDeltaMovement(clamp(item.getDeltaMovement().x + dx / dist * speed, -0.35, 0.35),
                    clamp(item.getDeltaMovement().y + dy / dist * speed, -0.35, 0.35), clamp(item.getDeltaMovement().z + dz / dist * speed, -0.35, 0.35));
            item.hasImpulse = true;


            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 2 == 0) {
                float rx = (level.random.nextFloat() - level.random.nextFloat()) * 0.125F;
                float ry = (level.random.nextFloat() - level.random.nextFloat()) * 0.125F;
                float rz = (level.random.nextFloat() - level.random.nextFloat()) * 0.125F;
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.BUBBLE, item.getX() + rx, item.getY() + item.getBbHeight() / 2.0 + ry,
                        item.getZ() + rz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}




