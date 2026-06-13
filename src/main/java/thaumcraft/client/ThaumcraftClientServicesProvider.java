package thaumcraft.client;

import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.fx.CrucibleClientFx;
import thaumcraft.client.fx.InfusionMatrixClientFx;
import thaumcraft.client.fx.InfusionBoreParticle;
import thaumcraft.client.fx.RuneParticle;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.client.network.TCClientPayloadHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.EssentiaSourceFxPayload;
import thaumcraft.common.network.OreScanPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.network.PedestalSparkleFxPayload;
import thaumcraft.common.network.ResearchCompleteNotificationPayload;
import thaumcraft.common.network.ThaumometerScanMessagePayload;
import thaumcraft.common.network.ThaumometerScanFxPayload;
import thaumcraft.common.network.WarpMessagePayload;
import thaumcraft.common.services.ThaumcraftClientServices;
import thaumcraft.common.registry.TCDataAttachments;

public final class ThaumcraftClientServicesProvider implements ThaumcraftClientServices {
    @Override
    public void handleResearchComplete(ResearchCompleteNotificationPayload payload) {
        TCClientPayloadHandler.handleResearchComplete(payload);
    }

    @Override
    public void handleEssentiaSourceFx(EssentiaSourceFxPayload payload) {
        TCClientPayloadHandler.handleEssentiaSourceFx(payload);
    }

    @Override
    public void handleInfusionSourceFx(InfusionSourceFxPayload payload) {
        TCClientPayloadHandler.handleInfusionSourceFx(payload);
    }

    @Override
    public void handleBlockZapFx(BlockZapFxPayload payload) {
        TCClientPayloadHandler.handleBlockZapFx(payload);
    }

    @Override
    public void handlePedestalSparkleFx(PedestalSparkleFxPayload payload) {
        TCClientPayloadHandler.handlePedestalSparkleFx(payload);
    }

    @Override
    public void handleThaumometerScanFx(ThaumometerScanFxPayload payload) {
        TCClientPayloadHandler.handleThaumometerScanFx(payload);
    }

    @Override
    public void handleThaumometerScanMessage(ThaumometerScanMessagePayload payload) {
        TCClientPayloadHandler.handleThaumometerScanMessage(payload);
    }

    @Override
    public void handleWarpMessage(WarpMessagePayload payload) {
        TCClientPayloadHandler.handleWarpMessage(payload);
    }

    @Override
    public void handleOreScanFx(OreScanPayload payload) {
        TCClientPayloadHandler.handleOreScanFx(payload);
    }

    @Override
    public void hungryNodeBlockFx(Level level, BlockPos source, BlockPos target, BlockState state) {
        if (Minecraft.getInstance().particleEngine == null
                || !(level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel)) {
            return;
        }
        Minecraft.getInstance().particleEngine.add(InfusionBoreParticle.block(clientLevel,
                source.getX() + level.random.nextFloat(),
                source.getY() + level.random.nextFloat(),
                source.getZ() + level.random.nextFloat(),
                target.getX() + 0.5D,
                target.getY() + 0.5D,
                target.getZ() + 0.5D,
                state,
                source));
    }

    @Override
    public void runeParticle(Level level, double x, double y, double z, float red, float green, float blue,
            int lifetime, float gravity) {
        if (Minecraft.getInstance().particleEngine == null
                || !(level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel)) {
            return;
        }
        RuneParticle rune = RuneParticle.createDirect(
                clientLevel, x, y, z, red, green, blue, lifetime, gravity);
        rune.setNoClip(true);
        Minecraft.getInstance().particleEngine.add(rune);
    }

    @Override
    public void blockRunes(Level level, BlockPos pos) {
        InfusionMatrixClientFx.blockRunes(level, pos);
    }

    @Override
    public void instabilityBolt(Level level, BlockPos pos, int instability) {
        InfusionMatrixClientFx.instabilityBolt(level, pos, instability);
    }

    @Override
    public void tickCrucible(CrucibleBlockEntity crucible) {
        CrucibleClientFx.tick(crucible);
    }

    @Override
    public void crucibleBlockEvent(CrucibleBlockEntity crucible, int eventId, int eventParam) {
        CrucibleClientFx.blockEvent(crucible, eventId, eventParam);
    }

    @Override
    public void trackAuraNode(Level level, BlockPos pos, NodeType type) {
        SinisterLodestoneClientTracker.track(level, pos, type);
    }

    @Override
    public boolean isAspectDiscovered(Aspect aspect) {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getData(TCDataAttachments.ASPECT_POOL).isDiscovered(aspect);
    }
}
