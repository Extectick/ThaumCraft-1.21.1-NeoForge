package thaumcraft.common.items.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.entities.item.FollowingItemEntity;
import thaumcraft.common.items.equipment.TCTiers;

import java.util.ArrayList;
import java.util.List;

public class ElementalShovelItem extends ShovelItem {
    public ElementalShovelItem() {
        super(TCTiers.ELEMENTAL, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(ShovelItem.createAttributes(TCTiers.ELEMENTAL, 1.5F, -3.0F)));
    }



    public static byte getOrientation(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().contains("or")) {
            return data.copyTag().getByte("or");
        }
        return 0;
    }

    public static void setOrientation(ItemStack stack, byte o) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putByte("or", (byte) (o % 3)));
    }



    private static boolean isReplaceableForPlacement(BlockState state) {
        return state.isAir() || state.is(Blocks.VINE) || state.is(Blocks.SHORT_GRASS) || state.liquid() || state.is(Blocks.DEAD_BUSH) || state.canBeReplaced();
    }



    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                setOrientation(stack, (byte) ((getOrientation(stack) + 1) % 3));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return super.useOn(context);
        }

        if (player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        int xm = face.getStepX();
        int ym = face.getStepY();
        int zm = face.getStepZ();
        Block clickedBlock = level.getBlockState(clickedPos).getBlock();
        byte orientation = getOrientation(stack);

        boolean placed = false;
        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                int xx = 0, yy = 0, zz = 0;
                if (orientation == 1) {
                    yy = bb;
                    if (face == Direction.UP || face == Direction.DOWN) {
                        int l = ((int) Math.floor(player.getYRot() * 4.0F / 360.0F + 0.5D)) & 3;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else if (face == Direction.NORTH || face == Direction.SOUTH) {
                        zz = aa;
                    } else {
                        xx = aa;
                    }
                } else if (orientation == 2) {
                    if (face == Direction.UP || face == Direction.DOWN) {
                        int l = ((int) Math.floor(player.getYRot() * 4.0F / 360.0F + 0.5D)) & 3;
                        yy = bb;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else {
                        zz = bb;
                        xx = aa;
                    }
                } else {
                    if (face == Direction.UP || face == Direction.DOWN) {
                        xx = aa;
                        zz = bb;
                    } else if (face == Direction.NORTH || face == Direction.SOUTH) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                }

                BlockPos targetPos = clickedPos.offset(xx + xm, yy + ym, zz + zm);
                BlockState targetState = level.getBlockState(targetPos);

                if (isReplaceableForPlacement(targetState)) {

                    boolean hasBlocks = false;
                    if (player.isCreative() || consumeBlockFromInventory(player, clickedBlock)) {
                        level.setBlock(targetPos, level.getBlockState(clickedPos), 3);
                        hasBlocks = true;
                    } else if (clickedBlock == Blocks.GRASS_BLOCK && (player.isCreative() || consumeBlockFromInventory(player, Blocks.DIRT))) {
                        level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                        hasBlocks = true;
                    }

                    if (hasBlocks) {
                        SoundType sound = level.getBlockState(clickedPos).getSoundType();
                        level.playSound(player, targetPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
                        stack.hurtAndBreak(1, player, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                        player.swing(context.getHand());
                        placed = true;
                    }
                }
            }
        }
        return placed ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }



    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player && !player.isShiftKeyDown()) {
            HitResult hit = player.pick(20.0, 0, false);
            if (hit instanceof BlockHitResult blockHit) {
                Direction face = blockHit.getDirection();
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
                if (targetState.isAir() || targetState.getDestroySpeed(level, target) < 0) {
                    continue;
                }
                if (!targetState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
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
            for (ItemStack drop : Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, stack)) {
                level.addFreshEntity(new FollowingItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop, player, 3));
            }
        }
    }


    //TODO: use glass block as preview block

    /**
     * Returns positions for ghost block preview, respecting orientation mode.
     * Exact port of original ItemElementalShovel.getArchitectBlocks().
     */
    public List<BlockPos> getArchitectBlocks(ItemStack stack, Level level, BlockPos pos, Direction face, Player player) {
        List<BlockPos> b = new ArrayList<>();
        if (!player.isShiftKeyDown()) {
            return b;
        }

        int xm = face.getStepX();
        int ym = face.getStepY();
        int zm = face.getStepZ();
        byte orientation = getOrientation(stack);

        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                int xx = 0, yy = 0, zz = 0;
                if (orientation == 1) {

                    yy = bb;
                    if (face == Direction.UP || face == Direction.DOWN) {
                        int l = ((int) Math.floor(player.getYRot() * 4.0F / 360.0F + 0.5D)) & 3;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else if (face == Direction.NORTH || face == Direction.SOUTH) {
                        zz = aa;
                    } else {
                        xx = aa;
                    }
                } else if (orientation == 2) {

                    if (face == Direction.UP || face == Direction.DOWN) {
                        int l = ((int) Math.floor(player.getYRot() * 4.0F / 360.0F + 0.5D)) & 3;
                        yy = bb;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else {
                        zz = bb;
                        xx = aa;
                    }
                } else {

                    if (face == Direction.UP || face == Direction.DOWN) {
                        xx = aa;
                        zz = bb;
                    } else if (face == Direction.NORTH || face == Direction.SOUTH) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                }

                BlockPos targetPos = pos.offset(xx + xm, yy + ym, zz + zm);
                BlockState targetState = level.getBlockState(targetPos);
                if (isReplaceableForPlacement(targetState)) {
                    b.add(targetPos);
                }
            }
        }
        return b;
    }

    private boolean consumeBlockFromInventory(Player player, Block block) {
        Item blockItem = block.asItem();
        if (blockItem == Items.AIR) {
            return false;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty() && invStack.is(blockItem)) {
                invStack.shrink(1);
                if (invStack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }
}




