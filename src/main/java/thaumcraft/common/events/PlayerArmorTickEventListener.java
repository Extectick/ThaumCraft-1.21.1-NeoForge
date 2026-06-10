package thaumcraft.common.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.equipment.BootsTravellerItem;

@EventBusSubscriber
public class PlayerArmorTickEventListener {

    private static final ResourceLocation STEP_HEIGHT_ID = Thaumcraft.id("boots_traveller_step");
    private static final AttributeModifier STEP_HEIGHT_BOOST = new AttributeModifier(STEP_HEIGHT_ID, 0.4, AttributeModifier.Operation.ADD_VALUE);

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof BootsTravellerItem) {
            handleBootsEffects(player);
        } else {
            removeStepBoost(player);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof BootsTravellerItem) {
            player.push(0.0, 0.275, 0.0);
        }
    }

    private static void handleBootsEffects(Player player) {
        // Only apply speed when not flying and moving forward
        if (!player.getAbilities().flying && player.zza > 0.0F) {
            // Step height boost (client-side only)
            if (player.level().isClientSide && !player.isShiftKeyDown()) {
                addStepBoost(player);
            }

            // Ground speed boost
            if (player.onGround()) {
                float bonus = 0.055F;
                if (player.isInWater()) {
                    bonus /= 4.0F;
                }

                // TODO: add EnchantmentHaste bonus when ported
                // TC4 original: haste_level * 0.015F bonus added to bonus
                // Halved in air and water
                // EnchantmentHaste is a custom enchant, NOT vanilla Haste
                // Register as boots-only (slots=ARMOR_FEET), max level 3, via enchanting table

                player.moveRelative(bonus, new Vec3(0.0, 0.0, 1.0));
            }
        }

        // Fall damage reduction
        if (player.fallDistance > 0.0F) {
            player.fallDistance -= 0.25F;
        }

        // TODO: vis-based auto-repair (IRepairable)
        // When vis system is implemented: consume vis from player's wand every 40 ticks
        // to repair the boots when damaged. Implement IRepairable or equivalent.
    }

    private static void addStepBoost(Player player) {
        var attr = player.getAttribute(Attributes.STEP_HEIGHT);
        if (attr != null && !attr.hasModifier(STEP_HEIGHT_ID)) {
            attr.addTransientModifier(STEP_HEIGHT_BOOST);
        }
    }

    private static void removeStepBoost(Player player) {
        var attr = player.getAttribute(Attributes.STEP_HEIGHT);
        if (attr != null) {
            attr.removeModifier(STEP_HEIGHT_ID);
        }
    }
}
