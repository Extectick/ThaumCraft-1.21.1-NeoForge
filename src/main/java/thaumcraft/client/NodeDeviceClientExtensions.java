package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.BlockTextureBreakParticle;
import thaumcraft.common.registry.TCBlocks;

public final class NodeDeviceClientExtensions implements IClientBlockExtensions {
    public static final NodeDeviceClientExtensions INSTANCE = new NodeDeviceClientExtensions();
    private static final ResourceLocation STABILIZER_TEXTURE = Thaumcraft.id("models/node_stabilizer");
    private static final ResourceLocation TRANSDUCER_TEXTURE = Thaumcraft.id("models/node_converter");

    private NodeDeviceClientExtensions() {
    }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (!(level instanceof ClientLevel clientLevel) || !(target instanceof BlockHitResult blockHit)) {
            return false;
        }

        BlockPos pos = blockHit.getBlockPos();
        TextureAtlasSprite sprite = spriteFor(state);
        double x = pos.getX() + 0.5D + blockHit.getDirection().getStepX() * 0.51D;
        double y = pos.getY() + 0.5D + blockHit.getDirection().getStepY() * 0.51D;
        double z = pos.getZ() + 0.5D + blockHit.getDirection().getStepZ() * 0.51D;
        manager.add(new BlockTextureBreakParticle(clientLevel, x, y, z,
                blockHit.getDirection().getStepX() * 0.02D,
                blockHit.getDirection().getStepY() * 0.02D,
                blockHit.getDirection().getStepZ() * 0.02D,
                sprite));
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (!(level instanceof ClientLevel clientLevel)) {
            return false;
        }

        TextureAtlasSprite sprite = spriteFor(state);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    double px = pos.getX() + (x + 0.5D) / 4.0D;
                    double py = pos.getY() + (y + 0.5D) / 4.0D;
                    double pz = pos.getZ() + (z + 0.5D) / 4.0D;
                    double dx = px - pos.getX() - 0.5D;
                    double dy = py - pos.getY() - 0.5D;
                    double dz = pz - pos.getZ() - 0.5D;
                    double speed = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
                    manager.add(new BlockTextureBreakParticle(clientLevel, px, py, pz,
                            dx / speed * 0.05D,
                            dy / speed * 0.05D + 0.02D,
                            dz / speed * 0.05D,
                            sprite));
                }
            }
        }
        return true;
    }

    private static TextureAtlasSprite spriteFor(BlockState state) {
        ResourceLocation texture = state.is(TCBlocks.NODE_TRANSDUCER.get())
                ? TRANSDUCER_TEXTURE
                : STABILIZER_TEXTURE;
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }
}
