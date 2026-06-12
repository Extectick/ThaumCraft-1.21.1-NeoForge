package thaumictinkerer.common.items.equipment;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;

public class BlackHoleTalismanItem extends Item {
    public BlackHoleTalismanItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ResourceLocation stored = stack.get(TTDataComponents.STORED_BLOCK);
        
        if (player.isShiftKeyDown()) {
            if (stored != null) {
                stack.remove(TTDataComponents.STORED_BLOCK);
                stack.remove(TTDataComponents.STORED_AMOUNT);
                stack.set(TTDataComponents.TALISMAN_ACTIVE, false);
                level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResultHolder.success(stack);
            }
        } else {
            if (stored != null) {
                boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
                stack.set(TTDataComponents.TALISMAN_ACTIVE, !active);
                level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResultHolder.success(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        ResourceLocation stored = stack.get(TTDataComponents.STORED_BLOCK);

        if (stored == null) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                stack.set(TTDataComponents.STORED_BLOCK, id);
                stack.set(TTDataComponents.STORED_AMOUNT, 0);
                level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        } else {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, context.getClickedFace());
            Block storedBlock = BuiltInRegistries.BLOCK.get(stored);
            int amount = stack.getOrDefault(TTDataComponents.STORED_AMOUNT, 0);
            
            if (handler != null && player.isShiftKeyDown()) {
                if (amount > 0 && storedBlock != null && !level.isClientSide) {
                    ItemStack toInsert = new ItemStack(storedBlock.asItem(), Math.min(amount, 64));
                    ItemStack remaining = ItemHandlerHelper.insertItemStacked(handler, toInsert, false);
                    int inserted = toInsert.getCount() - remaining.getCount();
                    if (inserted > 0) {
                        stack.set(TTDataComponents.STORED_AMOUNT, amount - inserted);
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.PASS;
            }

            if (amount > 0 && storedBlock != null) {
                BlockPlaceContext placeContext = new BlockPlaceContext(context);
                if (placeContext.canPlace()) {
                    BlockState placeState = storedBlock.getStateForPlacement(placeContext);
                    if (placeState != null && placeState.canSurvive(level, placeContext.getClickedPos())) {
                        level.setBlock(placeContext.getClickedPos(), placeState, 11);
                        level.playSound(null, placeContext.getClickedPos(), placeState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.isCreative()) {
                            stack.set(TTDataComponents.STORED_AMOUNT, amount - 1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (level.getGameTime() % 10 == 0) {
                boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
                ResourceLocation stored = stack.get(TTDataComponents.STORED_BLOCK);
                if (active && stored != null) {
                    Block storedBlock = BuiltInRegistries.BLOCK.get(stored);
                    if (storedBlock != null) {
                        Item storedItem = storedBlock.asItem();
                        int totalInInv = 0;
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack invStack = player.getInventory().getItem(i);
                            if (invStack.is(storedItem)) {
                                totalInInv += invStack.getCount();
                            }
                        }

                        if (totalInInv > 64) {
                            int toAbsorb = totalInInv - 64;
                            int absorbed = 0;
                            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                                ItemStack invStack = player.getInventory().getItem(i);
                                if (invStack.is(storedItem)) {
                                    int count = invStack.getCount();
                                    if (absorbed + count <= toAbsorb) {
                                        absorbed += count;
                                        invStack.setCount(0);
                                    } else {
                                        int diff = toAbsorb - absorbed;
                                        absorbed += diff;
                                        invStack.shrink(diff);
                                    }
                                }
                                if (absorbed >= toAbsorb) break;
                            }
                            int currentAmount = stack.getOrDefault(TTDataComponents.STORED_AMOUNT, 0);
                            stack.set(TTDataComponents.STORED_AMOUNT, currentAmount + absorbed);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ResourceLocation stored = stack.get(TTDataComponents.STORED_BLOCK);
        if (stored != null) {
            Block storedBlock = BuiltInRegistries.BLOCK.get(stored);
            if (storedBlock != null) {
                tooltipComponents.add(Component.translatable("tt.tooltip.stored_block").append(": ").append(storedBlock.getName()));
                tooltipComponents.add(Component.translatable("tt.tooltip.stored_amount").append(": ").append(String.valueOf(stack.getOrDefault(TTDataComponents.STORED_AMOUNT, 0))));
            }
        }
        boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
        tooltipComponents.add(Component.translatable("tt.tooltip.active").append(": ").append(active ? Component.translatable("tt.tooltip.on") : Component.translatable("tt.tooltip.off")));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) || super.isFoil(stack);
    }
}

