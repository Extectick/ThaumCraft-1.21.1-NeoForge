package thaumictinkerer.common.items.equipment;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;


import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;

public class AwakenedIchorSwordItem extends IchorSwordItem {

    public AwakenedIchorSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return KamiToolHelper.handleToolUse(level, player, hand, "item.thaumictinkerer.advanced_ichor_sword");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        KamiToolHelper.appendModeTooltip(stack, tooltipComponents, "item.thaumictinkerer.advanced_ichor_sword");
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            int mode = stack.getOrDefault(TTDataComponents.TOOL_MODE, 0);
            float baseDamage = (float) player.getAttributeValue(ATTACK_DAMAGE);
            if (mode == 1) {
                List<LivingEntity> entities = attacker.level().getEntitiesOfClass(LivingEntity.class, 
                    new AABB(target.getX() - 3, target.getY() - 3, target.getZ() - 3, 
                             target.getX() + 3, target.getY() + 3, target.getZ() + 3));
                for (LivingEntity e : entities) {
                    if (e != player && e != target && e.getClass() == target.getClass()) {
                        e.hurt(player.damageSources().playerAttack(player), baseDamage);
                    }
                }
            } else if (mode == 2) {
                target.hurt(player.damageSources().playerAttack(player), baseDamage / 2.0F);
                player.setAbsorptionAmount(player.getAbsorptionAmount() + 1.0F);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}





