package thaumcraft.api.wands;

import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.PrimalVisStorage;

public class ItemFocusBasic extends Item {
    private final PrimalVisStorage visCost;
    private final int color;
    private final int activationCooldown;
    private final boolean costPerTick;
    private final ResourceLocation depthLayerTexture;
    private final ResourceLocation ornamentTexture;
    private final WandFocusAnimation animation;

    public ItemFocusBasic(PrimalVisStorage visCost, int color, int activationCooldown, boolean costPerTick,
            Properties properties) {
        this(visCost, color, activationCooldown, costPerTick, null, null, WandFocusAnimation.CHARGE, properties);
    }

    public ItemFocusBasic(PrimalVisStorage visCost, int color, int activationCooldown, boolean costPerTick,
            ResourceLocation depthLayerTexture, ResourceLocation ornamentTexture, WandFocusAnimation animation,
            Properties properties) {
        super(properties.stacksTo(1));
        this.visCost = visCost;
        this.color = color;
        this.activationCooldown = activationCooldown;
        this.costPerTick = costPerTick;
        this.depthLayerTexture = depthLayerTexture;
        this.ornamentTexture = ornamentTexture;
        this.animation = animation;
    }

    public PrimalVisStorage getVisCost(ItemStack focusStack) {
        return this.visCost;
    }

    public int getFocusColor(ItemStack focusStack) {
        return this.color;
    }

    public int getActivationCooldown(ItemStack focusStack) {
        return this.activationCooldown;
    }

    public boolean isVisCostPerTick(ItemStack focusStack) {
        return this.costPerTick;
    }

    public ResourceLocation getFocusDepthLayerTexture(ItemStack focusStack) {
        return this.depthLayerTexture;
    }

    public ResourceLocation getOrnamentTexture(ItemStack focusStack) {
        return this.ornamentTexture;
    }

    public WandFocusAnimation getAnimation(ItemStack focusStack) {
        return this.animation;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        if (!this.visCost.isEmpty()) {
            tooltipComponents.add(Component.translatable(this.costPerTick ? "item.thaumcraft.focus.cost_per_tick"
                    : "item.thaumcraft.focus.cost").withStyle(ChatFormatting.DARK_PURPLE));
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                int cost = this.visCost.get(aspect);
                if (cost > 0) {
                    tooltipComponents.add(Component.translatable("item.thaumcraft.focus.cost.line",
                            Component.translatable("tc.aspect." + aspect.getTag()), formatVis(cost))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    private static String formatVis(int amount) {
        if (amount % 100 == 0) {
            return Integer.toString(amount / 100);
        }
        return String.format(Locale.ROOT, "%.2f", amount / 100.0D).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    public enum WandFocusAnimation {
        CHARGE,
        WAVE
    }
}
