package thaumcraft.common.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.OreScanPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.ThaumometerScanMessagePayload;
import thaumcraft.common.network.ThaumometerScanFxPayload;
import thaumcraft.common.network.WarpMessagePayload;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeType;

public interface ThaumcraftClientServices {
    final class Empty implements ThaumcraftClientServices {
    }

    default void handleResearchComplete(ResearchCompleteNotificationPayload payload) {
    }

    default void handleEssentiaSourceFx(EssentiaSourceFxPayload payload) {
    }

    default void handleInfusionSourceFx(InfusionSourceFxPayload payload) {
    }

    default void handleBlockZapFx(BlockZapFxPayload payload) {
    }

    default void handlePedestalSparkleFx(PedestalSparkleFxPayload payload) {
    }

    default void handleThaumometerScanFx(ThaumometerScanFxPayload payload) {
    }

    default void handleThaumometerScanMessage(ThaumometerScanMessagePayload payload) {
    }

    default void handleWarpMessage(WarpMessagePayload payload) {
    }

    default void handleOreScanFx(OreScanPayload payload) {
    }

    default void hungryNodeBlockFx(Level level, BlockPos source, BlockPos target, BlockState state) {
    }

    default void runeParticle(Level level, double x, double y, double z, float red, float green, float blue,
            int lifetime, float gravity) {
    }

    default void blockRunes(Level level, BlockPos pos) {
    }

    default void instabilityBolt(Level level, BlockPos pos, int instability) {
    }

    default void tickCrucible(CrucibleBlockEntity crucible) {
    }

    default void crucibleBlockEvent(CrucibleBlockEntity crucible, int eventId, int eventParam) {
    }

    default void trackAuraNode(Level level, BlockPos pos, NodeType type) {
    }

    default boolean isAspectDiscovered(Aspect aspect) {
        return false;
    }
}
