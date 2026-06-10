package thaumcraft.common.items.equipment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.items.equipment.TCTiers;

public class VoidShovelItem extends ShovelItem implements IWarpingGear {
    public VoidShovelItem() {
        super(TCTiers.VOID, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(ShovelItem.createAttributes(TCTiers.VOID, 1.5F, -3.0F)));
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
        return 1;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide && entity instanceof LivingEntity living && (!(entity instanceof Player) || player.level()
                .getServer()
                .isPvpAllowed())) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80));
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}




