package thaumcraft.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.Mth;

public class PedestalSparkleParticle extends SingleQuadParticle {
    private final int delay;
    private final int startParticle;
    private final int numParticles;
    private final int particleInc;

    public PedestalSparkleParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd,
                                   float red, float green, float blue,
                                   int age, int delay, float scale) {
        super(level, x, y, z, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.alpha = 0.9F;
        this.hasPhysics = false;
        this.lifetime = age + delay;
        this.delay = delay;
        this.startParticle = 112;
        this.numParticles = 9;
        this.particleInc = 1;
        this.quadSize = 0.1F * scale;
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.age >= this.delay) {
            super.render(buffer, camera, partialTicks);
        }
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        if (this.age <= 1 || this.age >= this.lifetime - 1) {
            return this.quadSize * 0.5F;
        }
        return this.quadSize;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EssentiaTrailParticle.RENDER_TYPE;
    }

    @Override
    protected float getU0() {
        int index = this.currentParticleIndex();
        return (index % 16) / 16.0F;
    }

    @Override
    protected float getU1() {
        int index = this.currentParticleIndex();
        return (index % 16) / 16.0F + 0.0624375F;
    }

    @Override
    protected float getV0() {
        int index = this.currentParticleIndex();
        return (index / 16) / 16.0F;
    }

    @Override
    protected float getV1() {
        int index = this.currentParticleIndex();
        return (index / 16) / 16.0F + 0.0624375F;
    }

    private int currentParticleIndex() {
        float frame = (float)this.age / Math.max(1, this.lifetime);
        int offset = Mth.floor(Math.min(this.numParticles * frame, this.numParticles - 1));
        return this.startParticle + offset * this.particleInc;
    }
}
