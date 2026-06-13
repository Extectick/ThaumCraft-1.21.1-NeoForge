package thaumcraft.common.items.equipment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.registry.TCSoundEvents;

public class ElementalSwordItem extends SwordItem {
    public ElementalSwordItem() {
        super(TCTiers.ELEMENTAL, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(TCTiers.ELEMENTAL, 3, -2.4F)));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (!(entity instanceof Player player)) {
            return;
        }

        int ticks = getUseDuration(stack, player) - remainingTicks;
        if (player.getDeltaMovement().y < 0.0D) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1.0D, 1.0D / 1.2D, 1.0D));
            player.fallDistance /= 1.2F;
        }

        double lift = Math.min(player.getDeltaMovement().y + 0.08D, 0.5D);
        if (lift > 0.5D) {
            lift = 0.2D;
        }
        player.setDeltaMovement(player.getDeltaMovement().x, lift, player.getDeltaMovement().z);
        player.hasImpulse = true;

        AABB box = player.getBoundingBox().inflate(2.5D);
        for (Entity target : level.getEntities(player, box, e -> e != player && e.isAlive() && player.getVehicle() != e)) {
            double dx = target.getX() - player.getX();
            double dy = target.getY() - player.getY();
            double dz = target.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz) + 0.1D;
            target.setDeltaMovement(target.getDeltaMovement().add(dx / 2.5D / distance, dy / 2.5D / distance, dz / 2.5D / distance));
            target.hasImpulse = true;
        }

        if (level.isClientSide) {
            for (int i = 0; i < 5; i++) {
                double ox = (level.random.nextDouble() - level.random.nextDouble()) * 1.5D;
                double oz = (level.random.nextDouble() - level.random.nextDouble()) * 1.5D;
                level.addParticle(ParticleTypes.CLOUD, player.getX() + ox, player.getY() + player.getBbHeight() * 0.5D, player.getZ() + oz,
                        -ox * 0.04D, 0.08D, -oz * 0.04D);
            }
            if (player.onGround()) {
                double angle = level.random.nextDouble() * Math.PI * 2.0D;
                level.addParticle(ParticleTypes.SMOKE, player.getX(), player.getY() + 0.1D, player.getZ(),
                        -Math.sin(angle) / 5.0D, 0.0D, Math.cos(angle) / 5.0D);
            }
            return;
        }

        if (ticks == 0 || ticks % 20 == 0) {
            level.playSound(null, player.blockPosition(), TCSoundEvents.WIND.get(), SoundSource.PLAYERS, 0.5F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        if (ticks > 0 && ticks % 20 == 0) {
            stack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!(attacker instanceof Player player) || attacker.level().isClientSide) {
            return result;
        }

        int hitCount = 0;
        AABB box = target.getBoundingBox().inflate(1.2D, 1.1D, 1.2D);
        for (LivingEntity nearby : attacker.level().getEntitiesOfClass(LivingEntity.class, box, e -> isValidSweepTarget(e, player, target))) {
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (nearby.hurt(attacker.damageSources().playerAttack(player), damage)) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                hitCount++;
            }
        }

        if (hitCount > 0) {
            attacker.level().playSound(null, target.blockPosition(), TCSoundEvents.SWING.get(), SoundSource.PLAYERS, 1.0F,
                    0.9F + attacker.level().random.nextFloat() * 0.2F);
            if (attacker.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + target.getBbHeight() * 0.5D,
                        target.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        return result;
    }

    private static boolean isValidSweepTarget(LivingEntity entity, Player player, LivingEntity primaryTarget) {
        if (entity == primaryTarget || entity.isRemoved() || !entity.isAlive()) {
            return false;
        }
        if (entity instanceof Player otherPlayer && otherPlayer.getUUID().equals(player.getUUID())) {
            return false;
        }
        if (entity instanceof TamableAnimal tameable && tameable.isOwnedBy(player)) {
            return false;
        }
        return true;
    }
}
