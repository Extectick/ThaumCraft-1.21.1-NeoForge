package thaumictinkerer.common.core.handler;


import net.minecraft.core.BlockPos;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.registry.TTDataComponents;
import thaumictinkerer.common.registry.TTItems;

@EventBusSubscriber(modid = ThaumicTinkerer.MODID)
public class KAMIEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean hasChest = false;

        ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helm.is(TTItems.ADVANCED_ICHOR_HELMET.get()) && helm.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false)) {
            if (player.isInWater()) {
                player.setAirSupply(300);
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 202, 0, true, false));
            }
            if (player.isInLava()) {
                player.setAirSupply(300);
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 202, 0, true, false));
            }
            if (player.getFoodData().getFoodLevel() > 0 && player.getFoodData().getFoodLevel() < 18 && player.getHealth() < player.getMaxHealth() && player.tickCount % 80 == 0) {
                player.heal(1.0F);
            }
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(TTItems.ADVANCED_ICHOR_CHESTPLATE.get()) && chest.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false)) {
            hasChest = true;
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.isCreative() && !player.isSpectator()) {
            if (player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.is(TTItems.ADVANCED_ICHOR_LEGGINGS.get()) && legs.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 202, 0, true, false));
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.is(TTItems.ADVANCED_ICHOR_BOOTS.get()) && boots.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0, true, false));
            
            if (player.getAttribute(Attributes.STEP_HEIGHT) != null && player.getAttribute(Attributes.STEP_HEIGHT).getBaseValue() != 1.0D) {
                player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0D);
            }
            
            int x = player.blockPosition().getX();
            int y = player.blockPosition().getY() - 1;
            int z = player.blockPosition().getZ();
            if (player.level().getBlockState(new BlockPos(x, y, z)).is(Blocks.DIRT)) {
                player.level().setBlockAndUpdate(new BlockPos(x, y, z), Blocks.GRASS_BLOCK.defaultBlockState());
            }
        } else {
            if (player.getAttribute(Attributes.STEP_HEIGHT) != null && player.getAttribute(Attributes.STEP_HEIGHT).getBaseValue() == 1.0D) {
                player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6D);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

            boolean hasAwakenedChest = chest.is(TTItems.ADVANCED_ICHOR_CHESTPLATE.get()) && chest.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false);
            boolean hasAwakenedBoots = boots.is(TTItems.ADVANCED_ICHOR_BOOTS.get()) && boots.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false);

            if (hasAwakenedChest || hasAwakenedBoots) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.is(TTItems.ADVANCED_ICHOR_CHESTPLATE.get()) && chest.getOrDefault(TTDataComponents.AWAKENED_ARMOR_MODE, false)) {
                if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
                    event.setNewDamage(0);
                }
            }
        }
    }
}




