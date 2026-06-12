package thaumictinkerer.common.items.equipment;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;

public class AwakenedIchorAxeItem extends IchorAxeItem {

    public AwakenedIchorAxeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return KamiToolHelper.handleToolUse(level, player, hand, "item.thaumictinkerer.advanced_ichor_axe");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        KamiToolHelper.appendModeTooltip(stack, tooltipComponents, "item.thaumictinkerer.advanced_ichor_axe");
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide && entityLiving instanceof Player player && !player.isShiftKeyDown()) {
            int mode = stack.getOrDefault(TTDataComponents.TOOL_MODE, 0);
            if (mode == 1) {
                BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                KamiToolHelper.breakAOE(level, player, pos, hit.getDirection(), 1);
            } else if (mode == 2) {
                KamiToolHelper.breakTree(level, player, pos);
            }
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }
}

