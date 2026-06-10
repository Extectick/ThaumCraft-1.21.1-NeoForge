package thaumcraft.common.items.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.items.equipment.TCTiers;

import java.util.List;

public class CrimsonBladeItem extends SwordItem implements IWarpingGear {
    public CrimsonBladeItem() {
        super(TCTiers.CRIMSON_VOID, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(TCTiers.CRIMSON_VOID, 3, -2.4F)));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (stack.isDamaged() && entity != null && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide && (!(target instanceof Player) || !(attacker instanceof Player) || target.level().getServer().isPvpAllowed())) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 2;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("enchantment.special.sapgreat").withStyle(ChatFormatting.GOLD));
    }
}




