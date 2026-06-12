package thaumictinkerer.common.items.equipment;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;

public class KamiToolHelper {

    private static String getModeKey(String translationKeyBase, int mode) {
        String color = switch (mode) {
            case 0 -> "green";
            case 1 -> "red";
            case 2 -> "blue";
            default -> "green";
        };
        return translationKeyBase + "." + color;
    }

    public static InteractionResultHolder<ItemStack> handleToolUse(Level level, Player player, InteractionHand hand, String translationKeyBase) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int mode = stack.getOrDefault(TTDataComponents.TOOL_MODE, 0);
            mode = (mode + 1) % 3;
            stack.set(TTDataComponents.TOOL_MODE, mode);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, 0.1F);
            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable(getModeKey(translationKeyBase, mode)).withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void appendModeTooltip(ItemStack stack, List<Component> tooltip, String translationKeyBase) {
        int mode = stack.getOrDefault(TTDataComponents.TOOL_MODE, 0);
        tooltip.add(Component.translatable(getModeKey(translationKeyBase, mode)).withStyle(ChatFormatting.DARK_AQUA));
    }

    public static void breakAOE(Level level, Player player, BlockPos pos, Direction face, int radius) {
        if (radius <= 0) return;
        boolean xz = face.getAxis() == Direction.Axis.Y;
        boolean xy = face.getAxis() == Direction.Axis.Z;
        boolean yz = face.getAxis() == Direction.Axis.X;

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i == 0 && j == 0) continue;

                BlockPos targetPos = pos;
                if (xz) targetPos = pos.offset(i, 0, j);
                else if (xy) targetPos = pos.offset(i, j, 0);
                else if (yz) targetPos = pos.offset(0, i, j);

                breakExtraBlock(level, player, targetPos);
            }
        }
    }

    public static void breakLine(Level level, Player player, BlockPos pos, Direction face, int length) {
        Direction lookDir = player.getDirection();
        for (int i = 1; i < length; i++) {
            BlockPos targetPos = pos.relative(lookDir, i);
            breakExtraBlock(level, player, targetPos);
        }
    }

    public static void breakColumn(Level level, Player player, BlockPos pos, int depth) {
        for (int i = 1; i <= depth; i++) {
            BlockPos targetPos = pos.below(i);
            breakExtraBlock(level, player, targetPos);
        }
    }

    public static void breakTree(Level level, Player player, BlockPos pos) {
        breakTreeRecursive(level, player, pos, 0);
    }

    private static void breakTreeRecursive(Level level, Player player, BlockPos pos, int depth) {
        if (depth > 64) return;
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos targetPos = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(targetPos);
                    if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                        if (breakExtraBlock(level, player, targetPos)) {
                            breakTreeRecursive(level, player, targetPos, depth + 1);
                        }
                    }
                }
            }
        }
    }

    private static boolean breakExtraBlock(Level level, Player player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0.0F) {
            ItemStack tool = player.getMainHandItem();
            if (tool.isCorrectToolForDrops(state)) {
                Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, tool);
                level.destroyBlock(pos, false, player);
                return true;
            }
        }
        return false;
    }
}

