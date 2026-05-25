package thaumcraft.common.items.wands;

import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;

public class WandCastingItem extends Item {
    public WandCastingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public int getVis(ItemStack stack, Aspect aspect) {
        return WandVisHelper.getVis(stack, aspect);
    }

    public void setVis(ItemStack stack, Aspect aspect, int amount) {
        WandVisHelper.setVis(stack, aspect, amount);
    }

    public int addVis(ItemStack stack, Aspect aspect, int amount) {
        return WandVisHelper.addVis(stack, aspect, amount);
    }

    public boolean consumeVis(ItemStack stack, Aspect aspect, int amount) {
        return WandVisHelper.consumeVis(stack, aspect, amount);
    }

    public int getMaxVis(ItemStack stack) {
        return WandVisHelper.WOOD_IRON_MAX_VIS;
    }

    public boolean hasEnoughVis(ItemStack stack, Aspect aspect, int amount) {
        return WandVisHelper.hasEnoughVis(stack, aspect, amount);
    }

    public float getConsumptionModifier(ItemStack stack, Player player, Aspect aspect, boolean crafting) {
        float modifier = 1.0F;
        if (player != null) {
            modifier -= ThaumcraftCuriosCompat.getVisDiscount(player, aspect) / 100.0F;
        }
        return Math.max(modifier, 0.1F);
    }

    public ItemStack getFocusItem(ItemStack stack) {
        return WandFocusHelper.getFocusItem(stack);
    }

    public ItemFocusBasic getFocus(ItemStack stack) {
        return WandFocusHelper.getFocus(stack);
    }

    public void setFocus(ItemStack stack, ItemStack focus) {
        WandFocusHelper.setFocus(stack, focus);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return WandVisHelper.fillAllVis(new ItemStack(this));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        int maxVis = this.getMaxVis(stack);
        tooltipComponents.add(Component.translatable("item.thaumcraft.wand.capacity", formatVis(maxVis))
                .withStyle(ChatFormatting.GOLD));

        if (Screen.hasShiftDown()) {
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                tooltipComponents.add(detailedVisLine(stack, aspect, maxVis));
            }
        } else {
            tooltipComponents.add(compactVisLine(stack));
        }

        ItemStack focus = this.getFocusItem(stack);
        if (!focus.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.thaumcraft.wand.focus", focus.getHoverName())
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack wand = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);

        if (WandFocusHelper.isFocus(other) && !WandFocusHelper.hasFocus(wand)) {
            if (!level.isClientSide) {
                WandFocusHelper.setFocus(wand, other);
                if (!player.getAbilities().instabuild) {
                    other.shrink(1);
                }
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.set",
                        WandFocusHelper.getFocusItem(wand).getHoverName()), true);
                level.playSound(null, player.blockPosition(), TCSoundEvents.HHON.get(), SoundSource.PLAYERS, 0.35F, 1.0F);
            }
            return InteractionResultHolder.sidedSuccess(wand, level.isClientSide);
        }

        if (player.isShiftKeyDown() && WandFocusHelper.hasFocus(wand)) {
            if (!level.isClientSide) {
                ItemStack focus = WandFocusHelper.removeFocus(wand);
                if (!FocusPouchCurioItem.addFocusToEquipped(player, focus) && !player.getInventory().add(focus)) {
                    player.drop(focus, false);
                }
                player.displayClientMessage(Component.translatable("item.thaumcraft.wand.focus.removed"), true);
                level.playSound(null, player.blockPosition(), TCSoundEvents.HHOFF.get(), SoundSource.PLAYERS, 0.35F, 1.0F);
            }
            return InteractionResultHolder.sidedSuccess(wand, level.isClientSide);
        }

        return InteractionResultHolder.pass(wand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack wand = context.getItemInHand();
        BlockState state = level.getBlockState(pos);

        if (state.is(TCBlocks.TABLE.get())) {
            if (!level.isClientSide) {
                BlockState worktableState = TCBlocks.ARCANE_WORKTABLE.get().defaultBlockState()
                        .setValue(SimpleTableBlock.FACING, state.getValue(SimpleTableBlock.FACING));
                level.setBlock(pos, worktableState, 3);

                Player player = context.getPlayer();
                if (player == null || !player.getAbilities().instabuild) {
                    if (level.getBlockEntity(pos) instanceof ArcaneWorktableBlockEntity worktable) {
                        worktable.setWand(wand.copyWithCount(1));
                        worktable.setChanged();
                    }
                    wand.shrink(1);
                }

                level.playSound(null, pos, TCSoundEvents.WAND.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
                level.playSound(null, pos, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!state.is(Blocks.BOOKSHELF)) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            level.removeBlock(pos, false);

            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                    new ItemStack(TCItems.THAUMONOMICON.get()));
            entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            entity.setNoGravity(true);
            serverLevel.addFreshEntity(entity);

            serverLevel.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 0.5D,
                    pos.getZ() + 0.5D, 32, 0.55D, 0.55D, 0.55D, 0.35D);
            level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    TCSoundEvents.WAND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static String formatVis(int amount) {
        if (amount % 100 == 0) {
            return Integer.toString(amount / 100);
        }
        return String.format(Locale.ROOT, "%.2f", amount / 100.0D).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private MutableComponent compactVisLine(ItemStack stack) {
        MutableComponent line = Component.empty();
        boolean first = true;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (!first) {
                line.append(Component.literal(" | ").withStyle(ChatFormatting.GRAY));
            }
            line.append(Component.literal(formatVis(this.getVis(stack, aspect))).setStyle(aspectStyle(aspect)));
            first = false;
        }
        return line;
    }

    private MutableComponent detailedVisLine(ItemStack stack, Aspect aspect, int maxVis) {
        int amount = this.getVis(stack, aspect);
        int percent = maxVis <= 0 ? 0 : Math.round(amount * 100.0F / maxVis);
        return Component.translatable("tc.aspect." + aspect.getTag()).setStyle(aspectStyle(aspect))
                .append(Component.literal(" x " + formatVis(amount) + ", ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("(" + percent + "% vis cost)").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static Style aspectStyle(Aspect aspect) {
        return Style.EMPTY.withColor(TextColor.fromRgb(aspect.getColor()));
    }
}
