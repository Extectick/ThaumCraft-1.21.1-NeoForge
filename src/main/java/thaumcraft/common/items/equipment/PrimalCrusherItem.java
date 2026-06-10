package thaumcraft.common.items.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.entities.item.FollowingItemEntity;
import thaumcraft.common.items.equipment.TCTiers;

import java.util.List;

public class PrimalCrusherItem extends DiggerItem implements IWarpingGear {
    public PrimalCrusherItem() {
        super(TCTiers.PRIMAL_VOID, BlockTags.MINEABLE_WITH_PICKAXE, new Item.Properties().rarity(Rarity.EPIC)
                .stacksTo(1)
                .attributes(createAttributes(TCTiers.PRIMAL_VOID, 3.5F, -3.0F)));
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return this.getTier().getSpeed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player && !player.isShiftKeyDown()) {
            HitResult hit = player.pick(20.0, 0, false);
            if (hit instanceof BlockHitResult blockHit) {
                Direction face = blockHit.getDirection();
                harvestWithAttraction(level, pos, player, stack);
                mine3x3(level, pos, face, player, stack);
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    private void mine3x3(Level level, BlockPos center, Direction face, Player player, ItemStack stack) {
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) {
                    continue;
                }

                int dx = 0, dy = 0, dz = 0;
                if (face == Direction.UP || face == Direction.DOWN) {
                    dx = a;
                    dz = b;
                } else if (face == Direction.NORTH || face == Direction.SOUTH) {
                    dx = a;
                    dy = b;
                } else {
                    dz = a;
                    dy = b;
                }

                BlockPos target = center.offset(dx, dy, dz);
                BlockState targetState = level.getBlockState(target);

                if (targetState.isAir()) {
                    continue;
                }
                if (targetState.getDestroySpeed(level, target) < 0) {
                    continue;
                }

                boolean effective = targetState.is(BlockTags.MINEABLE_WITH_PICKAXE) || targetState.is(BlockTags.MINEABLE_WITH_SHOVEL);
                if (!effective) {
                    continue;
                }

                if (!player.mayUseItemAt(target, face, stack)) {
                    continue;
                }

                harvestWithAttraction(level, target, player, stack);
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
        }
    }

    private void harvestWithAttraction(Level level, BlockPos pos, Player player, ItemStack stack) {
        BlockState state = level.getBlockState(pos);

        level.destroyBlock(pos, false, player);

        level.levelEvent(2001, pos, Block.getId(state));

        if (level instanceof ServerLevel serverLevel) {

            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, stack);
            for (ItemStack drop : drops) {
                FollowingItemEntity item = new FollowingItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop, player);
                level.addFreshEntity(item);
            }
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (stack.isDamaged() && entity != null && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 2;
    }
}




