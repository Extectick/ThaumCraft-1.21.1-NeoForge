package thaumcraft.common.lib.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.api.IRunicArmor;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.curios.ThaumcraftCuriosCompat;
import thaumcraft.common.lib.RunicShielding;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import top.theillusivec4.curios.api.SlotResult;

public final class RunicShieldEvents {
    private final Map<UUID, Integer> runicCharge = new HashMap<>();
    private final Map<UUID, Long> nextRechargeGameTime = new HashMap<>();
    private final Map<UUID, Integer> rechargeDelay = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        UUID id = player.getUUID();
        RunicInfo info = collectRunicInfo(player);
        if (info.maxCharge() <= 0) {
            this.runicCharge.remove(id);
            this.nextRechargeGameTime.remove(id);
            this.rechargeDelay.remove(id);
            return;
        }

        int current = Math.min(this.runicCharge.getOrDefault(id, 0), info.maxCharge());
        int delay = Math.max(0, this.rechargeDelay.getOrDefault(id, 0) - 1);
        if (delay > 0) {
            this.rechargeDelay.put(id, delay);
            this.runicCharge.put(id, current);
            return;
        }
        this.rechargeDelay.remove(id);

        long gameTime = player.level().getGameTime();
        long nextRecharge = this.nextRechargeGameTime.getOrDefault(id, 0L);
        if (current < info.maxCharge() && gameTime >= nextRecharge) {
            current++;
            this.nextRechargeGameTime.put(id, gameTime + rechargeIntervalTicks(info.chargedUpgrades()));
            player.level().playSound(null, player.blockPosition(), TCSoundEvents.RUNIC_SHIELD_CHARGE.get(),
                    SoundSource.PLAYERS, 0.18F, 1.0F);
        }

        this.runicCharge.put(id, current);
    }

    @SubscribeEvent
    public void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (isRunicBypassDamage(event.getSource().typeHolder().unwrapKey().map(ResourceKey::location).orElse(null))) {
            return;
        }

        UUID id = player.getUUID();
        RunicInfo info = collectRunicInfo(player);
        int charge = Math.min(this.runicCharge.getOrDefault(id, 0), info.maxCharge());
        if (info.maxCharge() <= 0 || charge <= 0) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage <= 0.0F) {
            return;
        }

        float blocked = Math.min(charge, damage);
        int remainingCharge = Math.max(0, charge - (int) Math.ceil(blocked));
        event.setNewDamage(Math.max(0.0F, damage - blocked));
        this.runicCharge.put(id, remainingCharge);
        player.level().playSound(null, player.blockPosition(), TCSoundEvents.RUNIC_SHIELD_EFFECT.get(),
                SoundSource.PLAYERS, 0.55F, 1.0F);

        if (remainingCharge <= 0) {
            this.rechargeDelay.put(id, ThaumcraftConfig.RUNIC_RECHARGE_DELAY_TICKS.get());
            applyEmergencyRecharge(player, id, info);
        }
    }

    private static RunicInfo collectRunicInfo(Player player) {
        RunicInfo info = RunicInfo.EMPTY;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                info = info.add(player.getItemBySlot(slot));
            }
        }

        for (SlotResult result : ThaumcraftCuriosCompat.findEquipped(player, stack -> stack.getItem() instanceof IRunicArmor)) {
            info = info.add(result.stack());
        }

        return info;
    }

    private static int getFinalCharge(ItemStack stack) {
        return RunicShielding.getFinalCharge(stack);
    }

    private static long rechargeIntervalTicks(int chargedUpgrades) {
        int milliseconds = Math.max(500, ThaumcraftConfig.RUNIC_RECHARGE_MILLISECONDS.get() - chargedUpgrades * 500);
        return Math.max(1L, Math.round(milliseconds / 50.0D));
    }

    private void applyEmergencyRecharge(Player player, UUID id, RunicInfo info) {
        if (info.emergencyUpgrades() <= 0) {
            return;
        }

        int restored = Math.min(info.maxCharge(), 8 * info.emergencyUpgrades());
        if (restored > 0) {
            this.runicCharge.put(id, restored);
            this.nextRechargeGameTime.put(id, player.level().getGameTime() + rechargeIntervalTicks(info.chargedUpgrades()));
        }
    }

    private static boolean isRunicBypassDamage(ResourceLocation damageTypeLocation) {
        if (damageTypeLocation == null) {
            return false;
        }
        return DamageTypes.DROWN.location().equals(damageTypeLocation)
                || DamageTypes.WITHER.location().equals(damageTypeLocation)
                || DamageTypes.FELL_OUT_OF_WORLD.location().equals(damageTypeLocation)
                || DamageTypes.STARVE.location().equals(damageTypeLocation);
    }

    private record RunicInfo(int maxCharge, int chargedUpgrades, int kineticUpgrades, int healingUpgrades,
            int emergencyUpgrades) {
        private static final RunicInfo EMPTY = new RunicInfo(0, 0, 0, 0, 0);

        private RunicInfo add(ItemStack stack) {
            int charge = getFinalCharge(stack);
            if (charge <= 0) {
                return this;
            }

            int charged = this.chargedUpgrades + (stack.is(TCItems.RUNIC_RING_CHARGED.get()) ? 1 : 0);
            int kinetic = this.kineticUpgrades + (stack.is(TCItems.RUNIC_GIRDLE_KINETIC.get()) ? 1 : 0);
            int healing = this.healingUpgrades + (stack.is(TCItems.RUNIC_RING_REGEN.get()) ? 1 : 0);
            int emergency = this.emergencyUpgrades + (stack.is(TCItems.RUNIC_AMULET_EMERGENCY.get()) ? 1 : 0);
            return new RunicInfo(this.maxCharge + charge, charged, kinetic, healing, emergency);
        }
    }
}
