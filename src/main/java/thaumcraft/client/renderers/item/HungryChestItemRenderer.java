package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.blockentities.HungryChestBlockEntity;
import thaumcraft.common.registry.TCBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

public class HungryChestItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final HungryChestItemRenderer INSTANCE = new HungryChestItemRenderer();
    private final HungryChestBlockEntity fakeBE = new HungryChestBlockEntity(BlockPos.ZERO, TCBlocks.HUNGRY_CHEST.get().defaultBlockState());

    public HungryChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight,
                             int combinedOverlay) {
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(fakeBE, poseStack, buffer, combinedLight, combinedOverlay);
    }

    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HungryChestItemRenderer.this;
            }
        };
    }
}



