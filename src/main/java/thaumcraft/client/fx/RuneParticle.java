package thaumcraft.client.fx;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class RuneParticle extends SingleQuadParticle {

    private final int lifetimeTotal;

    private RuneParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, int lifetime, float gravity) {
        super(level, x, y, z);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.gravity = gravity;
        this.xd = this.yd = this.zd = 0;
        this.lifetime = 3 * lifetime;
        this.lifetimeTotal = this.lifetime;
        this.hasPhysics = false;
        this.setSize(0.01F, 0.01F);
        this.quadSize = 0.3F * (1.0F + (float) (level.random.nextGaussian() * 0.1));
        this.alpha = 0.0F;
    }

    public static RuneParticle createDirect(ClientLevel level, double x, double y, double z, float r, float g, float b, int lifetime, float gravity) {
        return new RuneParticle(level, x, y, z, r, g, b, lifetime, gravity);
    }

    public void setNoClip(boolean value) {
        this.hasPhysics = !value;
    }

    @Override
    protected float getU0() {

        int runeCol = (224 + this.age / 3) % 16;
        return runeCol / 16.0F;
    }

    @Override
    protected float getU1() {
        return getU0() + 0.0624375F;
    }

    @Override
    protected float getV0() {
        return 0.375F;
    }

    @Override
    protected float getV1() {
        return 0.375F + 0.0624375F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        float threshold = this.lifetimeTotal / 5.0F;
        if (this.age <= threshold) {
            this.alpha = this.age / threshold;
        } else {
            this.alpha = (float) (this.lifetimeTotal - this.age) / this.lifetimeTotal;
        }
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.908;
        this.yd *= 0.908;
        this.zd *= 0.908;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * ((float) (this.lifetimeTotal - this.age + 1) / this.lifetimeTotal);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240 << 16 | 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return SparkleParticle.SPARKLE_RENDER_TYPE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet ignored) {
        }

        @Override
        public RuneParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new RuneParticle(level, x, y, z, 1.0F, 1.0F, 1.0F, 20, 0.0F);
        }
    }
}


