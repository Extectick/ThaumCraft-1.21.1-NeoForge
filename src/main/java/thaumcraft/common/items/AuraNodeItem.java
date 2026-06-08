package thaumcraft.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class AuraNodeItem extends BlockItem {
    public AuraNodeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.thaumcraft.aura_node.desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltipComponents.add(Component.translatable("item.thaumcraft.creative_only")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
