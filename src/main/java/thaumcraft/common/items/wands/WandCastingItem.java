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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;

public class WandCastingItem extends Item {
    public static final String ROD_WOOD = "wood";
    public static final String ROD_GREATWOOD_STAFF = "greatwood_staff";
    public static final String CAP_IRON = "iron";

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

    public boolean consumeVis(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
        return WandVisHelper.consumeVis(stack, aspect, effectiveVisCost(stack, player, aspect, amount, crafting));
    }

    public boolean consumeAllVis(ItemStack stack, Player player, PrimalVisStorage cost, boolean crafting) {
        if (cost == null || cost.isEmpty()) {
            return false;
        }
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (!hasEnoughVis(stack, player, aspect, cost.get(aspect), crafting)) {
                return false;
            }
        }
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int amount = cost.get(aspect);
            if (amount > 0) {
                consumeVis(stack, player, aspect, amount, crafting);
            }
        }
        return true;
    }

    public int getMaxVis(ItemStack stack) {
        return WandParts.rod(getRod(stack)).capacity() * (isSceptre(stack) ? 150 : 100);
    }

    public boolean hasEnoughVis(ItemStack stack, Aspect aspect, int amount) {
        return WandVisHelper.hasEnoughVis(stack, aspect, amount);
    }

    public float getConsumptionModifier(ItemStack stack, Player player, Aspect aspect, boolean crafting) {
        float modifier = WandParts.cap(getCap(stack)).costModifier(aspect);
        if (player != null) {
            modifier -= WandVisDiscounts.getTotalVisDiscount(player, aspect) / 100.0F;
        }
        if (!crafting) {
            modifier -= getFocusFrugal(stack) / 10.0F;
        }
        if (isSceptre(stack)) {
            modifier -= 0.1F;
        }
        return Math.max(modifier, 0.1F);
    }

    public int effectiveVisCost(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
        return (int) (amount * getConsumptionModifier(stack, player, aspect, crafting));
    }

    public boolean hasEnoughVis(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
        return WandVisHelper.hasEnoughVis(stack, aspect, effectiveVisCost(stack, player, aspect, amount, crafting));
    }

    public String getRod(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.WAND_ROD, ROD_WOOD);
    }

    public void setRod(ItemStack stack, String rod) {
        stack.set(TCDataComponents.WAND_ROD, rod);
    }

    public String getCap(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.WAND_CAP, CAP_IRON);
    }

    public void setCap(ItemStack stack, String cap) {
        stack.set(TCDataComponents.WAND_CAP, cap);
    }

    public boolean isStaff(ItemStack stack) {
        return WandParts.rod(getRod(stack)).staff();
    }

    public boolean isSceptre(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.WAND_SCEPTRE, false);
    }

    public void setSceptre(ItemStack stack, boolean sceptre) {
        if (sceptre) {
            stack.set(TCDataComponents.WAND_SCEPTRE, true);
        } else {
            stack.remove(TCDataComponents.WAND_SCEPTRE);
        }
    }

    public boolean hasRunes(ItemStack stack) {
        return WandParts.rod(getRod(stack)).hasRunes();
    }

    public ItemStack getFocusItem(ItemStack stack) {
        return WandFocusHelper.getFocusItem(stack);
    }

    public ItemFocusBasic getFocus(ItemStack stack) {
        return WandFocusHelper.getFocus(stack);
    }

    public int getFocusFrugal(ItemStack stack) {
        ItemStack focus = getFocusItem(stack);
        return focus.getItem() instanceof ItemFocusBasic focusItem ? focusItem.getFrugalUpgrade(focus) : 0;
    }

    public void setFocus(ItemStack stack, ItemStack focus) {
        WandFocusHelper.setFocus(stack, focus);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            rechargeFromRod(stack, player);
        }
    }

    @Override
    public ItemStack getDefaultInstance() {
        return WandVisHelper.fillAllVis(createVariant(this, ROD_WOOD, CAP_IRON, false));
    }

    public static ItemStack createVariant(Item item, String rod, String cap, boolean sceptre) {
        ItemStack stack = new ItemStack(item);
        stack.set(TCDataComponents.WAND_ROD, rod);
        stack.set(TCDataComponents.WAND_CAP, cap);
        if (sceptre) {
            stack.set(TCDataComponents.WAND_SCEPTRE, true);
        }
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        String rod = getRod(stack);
        if (rod.endsWith("_staff")) {
            rod = rod.substring(0, rod.length() - "_staff".length());
        }
        return Component.translatable("item.thaumcraft.wand.name",
                Component.translatable("item.thaumcraft.wand.cap." + getCap(stack)),
                Component.translatable("item.thaumcraft.wand.rod." + rod),
                Component.translatable(isStaff(stack) ? "item.thaumcraft.wand.obj.staff"
                        : (isSceptre(stack) ? "item.thaumcraft.wand.obj.sceptre" : "item.thaumcraft.wand.obj.wand")));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        int maxVis = this.getMaxVis(stack);
        tooltipComponents.add(Component.translatable("item.thaumcraft.wand.capacity", formatVis(maxVis))
                .withStyle(ChatFormatting.GOLD));

        if (Screen.hasShiftDown()) {
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                tooltipComponents.add(detailedVisLine(stack, aspect));
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

    private MutableComponent detailedVisLine(ItemStack stack, Aspect aspect) {
        int amount = this.getVis(stack, aspect);
        int percent = Math.round(this.getConsumptionModifier(stack, null, aspect, false) * 100.0F);
        return Component.empty()
                .append(Component.translatable("tc.aspect." + aspect.getTag()).setStyle(aspectStyle(aspect)))
                .append(Component.literal(" x " + formatVis(amount) + ", ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("(" + percent + "% vis cost)").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static Style aspectStyle(Aspect aspect) {
        return Style.EMPTY.withColor(TextColor.fromRgb(aspect.getColor()));
    }

    private void rechargeFromRod(ItemStack stack, Player player) {
        WandParts.Rod rod = WandParts.rod(getRod(stack));
        int maxVis = getMaxVis(stack);
        if (rod.rechargeAspect() != null) {
            if (player.tickCount % 200 == 0 && getVis(stack, rod.rechargeAspect()) < maxVis / 10) {
                addVis(stack, rod.rechargeAspect(), 100);
            }
            return;
        }

        if (!"primal_staff".equals(rod.tag()) || player.tickCount % 50 != 0) {
            return;
        }

        List<Aspect> aspectsWithRoom = Aspect.getPrimalAspects().stream()
                .filter(aspect -> getVis(stack, aspect) < maxVis / 10)
                .toList();
        if (!aspectsWithRoom.isEmpty()) {
            addVis(stack, aspectsWithRoom.get(levelRandomIndex(player, aspectsWithRoom.size())), 100);
        }
    }

    private static int levelRandomIndex(Player player, int bound) {
        return player.level().random.nextInt(bound);
    }
}
