package thaumictinkerer.common.items.equipment;


import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;

public class BloodSwordItem extends SwordItem {
    public BloodSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown()) {
            boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
            stack.set(TTDataComponents.TALISMAN_ACTIVE, !active);
            level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            player.hurt(player.damageSources().magic(), 2.0F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (player.getMainHandItem() == stack) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false, false));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
        tooltipComponents.add(Component.translatable("tt.tooltip.active").append(": ").append(active ? Component.translatable("tt.tooltip.on") : Component.translatable("tt.tooltip.off")));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false) || super.isFoil(stack);
    }
}

