package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;

public class CrucibleBubbleParticle extends SingleQuadParticle {
    private int particle = 16;
    private double bubbleSpeed = 0.002D;
    private final boolean growWithAge;

    public CrucibleBubbleParticle(ClientLevel level, double x, double y, double z, int age, boolean growWithAge) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.growWithAge = growWithAge;
        this.rCol = 1.0F;
        this.gCol = 0.0F;
        this.bCol = 0.5F;
        this.hasPhysics = false;
        this.quadSize = this.random.nextFloat() * 0.3F + 0.2F;
        this.xd = (this.random.nextDouble() * 2.0D - 1.0D) * 0.02D;
        this.yd = this.random.nextDouble() * 0.02D;
        this.zd = (this.random.nextDouble() * 2.0D - 1.0D) * 0.02D;
        this.lifetime = (int) (age + 2 + 8.0D / (this.random.nextDouble() * 0.8D + 0.2D));

        if (Minecraft.getInstance().cameraEntity != null) {
            int visibleDistance = Minecraft.getInstance().options.graphicsMode().get().getKey().equals("fast") ? 25 : 50;
            if (Minecraft.getInstance().cameraEntity.distanceToSqr(x, y, z) > visibleDistance * visibleDistance) {
                this.lifetime = 0;
            }
        }
    }

    public static CrucibleBubbleParticle bubble(ClientLevel level, double x, double y, double z, int age,
            float red, float green, float blue) {
        CrucibleBubbleParticle particle = new CrucibleBubbleParticle(level, x, y, z, age, false);
        particle.setColor(red, green, blue);
        return particle;
    }

    public static CrucibleBubbleParticle froth(ClientLevel level, double x, double y, double z) {
        CrucibleBubbleParticle particle = new CrucibleBubbleParticle(level, x, y, z, -4, false);
        particle.setColor(0.5F, 0.5F, 0.7F);
        particle.quadSize *= 0.75F;
        particle.lifetime = 4 + particle.random.nextInt(3);
        particle.bubbleSpeed = -0.001D;
        particle.xd /= 5.0D;
        particle.yd /= 10.0D;
        particle.zd /= 5.0D;
        return particle;
    }

    public static CrucibleBubbleParticle frothDown(ClientLevel level, double x, double y, double z) {
        CrucibleBubbleParticle particle = new CrucibleBubbleParticle(level, x, y, z, -4, false);
        particle.setColor(0.5F, 0.5F, 0.7F);
        particle.quadSize *= 0.75F;
        particle.lifetime = 12 + particle.random.nextInt(12);
        particle.bubbleSpeed = -0.005D;
        particle.xd /= 5.0D;
        particle.yd /= 10.0D;
        particle.zd /= 5.0D;
        return particle;
    }

    public void setBubbleSpeed(double bubbleSpeed) {
        this.bubbleSpeed = bubbleSpeed;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd += this.bubbleSpeed;
        if (this.bubbleSpeed > 0.0D) {
            this.xd += (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.01F;
            this.zd += (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.01F;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.85D;
        this.yd *= 0.85D;
        this.zd *= 0.85D;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else if (this.lifetime - this.age <= 2) {
            this.particle++;
        }
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        if (!this.growWithAge) {
            return 0.1F * this.quadSize;
        }
        return 0.2F * this.quadSize * ((float) this.age / Math.max(1, this.lifetime));
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    @Override
    public net.minecraft.client.particle.ParticleRenderType getRenderType() {
        return EssentiaTrailParticle.RENDER_TYPE;
    }

    @Override
    protected float getU0() {
        return this.particle % 16 / 16.0F;
    }

    @Override
    protected float getU1() {
        return this.particle % 16 / 16.0F + 0.0624375F;
    }

    @Override
    protected float getV0() {
        return this.particle / 16 / 16.0F;
    }

    @Override
    protected float getV1() {
        return this.particle / 16 / 16.0F + 0.0624375F;
    }
}
