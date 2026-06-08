package thaumcraft.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import thaumcraft.common.registry.TCItems;

public final class ThaumometerFirstPersonHandler {
    private ThaumometerFirstPersonHandler() {
    }

    public static void onRenderHand(RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !isHoldingThaumometer(player)) {
            return;
        }

        if (!event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            event.setCanceled(true);
            return;
        }

        if (!player.isInvisible()) {
            renderHoldingArms(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), player,
                    event.getEquipProgress());
        }
    }

    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !isHoldingThaumometer(player)) {
            return;
        }

        if (event.isAttack()) {
            event.setSwingHand(false);
            event.setCanceled(true);
            return;
        }

        if (event.isUseItem()) {
            event.setSwingHand(false);
            if (!player.getItemInHand(event.getHand()).is(TCItems.THAUMOMETER.get())) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean isHoldingThaumometer(LocalPlayer player) {
        return player.getMainHandItem().is(TCItems.THAUMOMETER.get())
                || player.getOffhandItem().is(TCItems.THAUMOMETER.get());
    }

    private static void renderHoldingArms(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            AbstractClientPlayer player, float equipProgress) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.12F + equipProgress * -0.18F, -0.82F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-8.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderArm(poseStack, buffer, packedLight, player, HumanoidArm.RIGHT);
        renderArm(poseStack, buffer, packedLight, player, HumanoidArm.LEFT);
        poseStack.popPose();
    }

    private static void renderArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            AbstractClientPlayer player, HumanoidArm arm) {
        EntityRenderer<? super AbstractClientPlayer> renderer = Minecraft.getInstance()
                .getEntityRenderDispatcher()
                .getRenderer(player);
        if (!(renderer instanceof PlayerRenderer playerRenderer)) {
            return;
        }

        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * -41.0F));
        poseStack.translate(side * 0.34F, -1.06F, 0.42F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * 8.0F));

        if (arm == HumanoidArm.RIGHT) {
            playerRenderer.renderRightHand(poseStack, buffer, packedLight, player);
        } else {
            playerRenderer.renderLeftHand(poseStack, buffer, packedLight, player);
        }
        poseStack.popPose();
    }

    public static boolean isUsingThaumometer(ItemStack stack) {
        return stack.is(TCItems.THAUMOMETER.get());
    }
}
