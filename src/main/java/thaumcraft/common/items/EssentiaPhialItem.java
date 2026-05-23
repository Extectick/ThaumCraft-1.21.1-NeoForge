package thaumcraft.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.registry.TCDataComponents;

public class EssentiaPhialItem extends Item {
    public EssentiaPhialItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        EssentiaStorage essentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        if (essentia.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.thaumcraft.essentia_phial.empty")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("item.thaumcraft.essentia_phial.contents",
                    Component.translatable("tc.aspect." + essentia.aspect().getTag()), essentia.amount())
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
