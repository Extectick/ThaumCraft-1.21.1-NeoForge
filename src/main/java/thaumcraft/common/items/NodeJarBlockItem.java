package thaumcraft.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.services.ClientServices;

public class NodeJarBlockItem extends BlockItem {
    public NodeJarBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        NodeJarData data = stack.getOrDefault(TCDataComponents.NODE_JAR_DATA, NodeJarData.EMPTY);
        if (!data.isEmpty()) {
            Component type = Component.translatable("nodetype." + data.nodeType().name() + ".name");
            if (data.nodeModifier() == null) {
                tooltipComponents.add(type.copy().withStyle(ChatFormatting.BLUE));
            } else {
                tooltipComponents.add(Component.empty()
                        .append(type)
                        .append(", ")
                        .append(Component.translatable("nodemod." + data.nodeModifier().name() + ".name"))
                        .withStyle(ChatFormatting.BLUE));
            }
            for (Aspect aspect : data.aspects().getAspectsSorted()) {
                if (ClientServices.get().isAspectDiscovered(aspect)) {
                    tooltipComponents.add(Component.translatable("tc.aspect." + aspect.getTag())
                            .append(" x " + data.aspects().getAmount(aspect))
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    tooltipComponents.add(Component.translatable("tc.aspect.unknown")
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
