package thaumictinkerer.common.items.equipment;


import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumictinkerer.common.registry.TTDataComponents;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class XpTalismanItem extends Item implements ICurioItem {
    private static final int MAX_XP = 1500;
    private static final int XP_PER_BOTTLE = 10;

    public XpTalismanItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        
        if (player.isShiftKeyDown()) {
            boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
            stack.set(TTDataComponents.TALISMAN_ACTIVE, !active);
            level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        } else {
            int storedXp = stack.getOrDefault(TTDataComponents.STORED_XP, 0);
            if (storedXp >= XP_PER_BOTTLE) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack invStack = player.getInventory().getItem(i);
                    if (invStack.is(Items.GLASS_BOTTLE)) {
                        invStack.shrink(1);
                        ItemStack xpBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                        if (!player.getInventory().add(xpBottle)) {
                            player.drop(xpBottle, false);
                        }
                        stack.set(TTDataComponents.STORED_XP, storedXp - XP_PER_BOTTLE);
                        level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                        return InteractionResultHolder.success(stack);
                    }
                }
            }
        }
        
        return super.use(level, player, usedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            tickXpAbsorption(stack, level, player);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide && slotContext.entity() instanceof Player player) {
            tickXpAbsorption(stack, player.level(), player);
        }
    }

    private void tickXpAbsorption(ItemStack stack, Level level, Player player) {
        if (level.getGameTime() % 5 == 0) {
            boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
            int storedXp = stack.getOrDefault(TTDataComponents.STORED_XP, 0);
            
            if (active && storedXp < MAX_XP) {
                AABB bounds = player.getBoundingBox().inflate(5.0D);
                List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, bounds);
                
                for (ExperienceOrb orb : orbs) {
                    if (storedXp + orb.getValue() <= MAX_XP) {
                        storedXp += orb.getValue();
                        orb.discard();
                        level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.8F));
                    }
                }
                stack.set(TTDataComponents.STORED_XP, storedXp);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int storedXp = stack.getOrDefault(TTDataComponents.STORED_XP, 0);
        tooltipComponents.add(Component.translatable("tt.tooltip.stored_xp").append(": ").append(String.valueOf(storedXp)).append(" / ").append(String.valueOf(MAX_XP)));
        
        boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
        tooltipComponents.add(Component.translatable("tt.tooltip.active").append(": ").append(active ? Component.translatable("tt.tooltip.on") : Component.translatable("tt.tooltip.off")));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) || super.isFoil(stack);
    }
}

