package thaumcraft.client.fx;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BlockTextureBreakParticle extends TextureSheetParticle {
    private final float uo;
    private final float vo;

    public BlockTextureBreakParticle(ClientLevel level, double x, double y, double z,
            double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd);
        this.setSprite(sprite);
        this.gravity = 1.0F;
        this.quadSize *= 0.6F;
        this.lifetime = 10 + this.random.nextInt(10);
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }
}
