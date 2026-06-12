package thaumictinkerer.common.items.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumictinkerer.common.registry.TTDataComponents;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Iterator;
import java.util.List;

public class CleansingTalismanItem extends Item implements ICurioItem {
    public CleansingTalismanItem(Properties properties) {
        super(properties.stacksTo(1).durability(100));
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            tickCleansing(stack, level, player);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide && slotContext.entity() instanceof Player player) {
            tickCleansing(stack, player.level(), player);
        }
    }

    private void tickCleansing(ItemStack stack, Level level, Player player) {
        if (level.getGameTime() % 20 == 0) {
            boolean active = stack.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
            if (active) {
                boolean cleansed = false;
                Iterator<MobEffectInstance> iterator = player.getActiveEffects().iterator();
                while (iterator.hasNext()) {
                    MobEffectInstance effect = iterator.next();
                    if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                        player.removeEffect(effect.getEffect());
                        cleansed = true;
                        break;
                    }
                }
                
                if (cleansed) {
                    stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                }
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

