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
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.registry.TCDataComponents;

public class JarBlockItem extends BlockItem {
    public JarBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (hasEssentiaData(stack)) {
            return Component.translatable("item.thaumcraft.jar_of_essentia");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        Aspect filter = stack.get(TCDataComponents.JAR_FILTER);

        if (!essentia.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.thaumcraft.jar_of_essentia.contents",
                    aspectName(essentia.aspect()), essentia.amount()).withStyle(ChatFormatting.GRAY));
        } else if (filter != null) {
            tooltipComponents.add(Component.translatable("tc.aspect.unknown").withStyle(ChatFormatting.GRAY));
        }

        if (filter != null && !essentia.isEmpty()) {
            tooltipComponents.add(aspectName(filter).copy().withStyle(ChatFormatting.DARK_PURPLE));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private static boolean hasEssentiaData(ItemStack stack) {
        return !stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY).isEmpty()
                || stack.has(TCDataComponents.JAR_FILTER);
    }

    private static Component aspectName(Aspect aspect) {
        return Component.translatable("tc.aspect." + aspect.getTag());
    }
}
