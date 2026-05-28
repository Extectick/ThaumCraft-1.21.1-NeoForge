package thaumcraft.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;

public class EssentiaTrailParticle extends SingleQuadParticle {
    static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, Thaumcraft.id("textures/misc/particles.png"));
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMCRAFT_ESSENTIA_TRAIL";
        }
    };

    private static final float U0 = 0.5625F;
    private static final float U1 = 0.625F;
    private static final float V0 = 0.0625F;
    private static final float V1 = 0.125F;

    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final int count;
    private float trailScale;

    public EssentiaTrailParticle(ClientLevel level, double x, double y, double z,
                                 double targetX, double targetY, double targetZ,
                                 int count, int color, float scale) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.count = count;
        this.trailScale = (Mth.sin(count / 2.0F) * 0.1F + 1.0F) * scale;
        this.quadSize = 0.1F;
        this.gravity = 0.2F;
        this.hasPhysics = true;
        this.alpha = 0.5F;

        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 30.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + this.random.nextInt(base);

        this.xd = Mth.sin(count / 4.0F) * 0.015F + this.random.nextGaussian() * 0.002D;
        this.yd = 0.1F + Mth.sin(count / 3.0F) * 0.01F;
        this.zd = Mth.sin(count / 2.0F) * 0.015F + this.random.nextGaussian() * 0.002D;

        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float mr = red * 0.2F;
        float mg = green * 0.2F;
        float mb = blue * 0.2F;
        this.rCol = red - mr + this.random.nextFloat() * mr;
        this.gCol = green - mg + this.random.nextFloat() * mg;
        this.bCol = blue - mb + this.random.nextFloat() * mb;

        if (Minecraft.getInstance().cameraEntity != null) {
            int visibleDistance = Minecraft.getInstance().options.graphicsMode().get().getKey().equals("fast") ? 32 : 64;
            if (Minecraft.getInstance().cameraEntity.distanceToSqr(x, y, z) > visibleDistance * visibleDistance) {
                this.lifetime = 0;
            }
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd += 0.01D * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;
        this.xd = Mth.clamp(this.xd, -0.05D, 0.05D);
        this.yd = Mth.clamp(this.yd, -0.05D, 0.05D);
        this.zd = Mth.clamp(this.zd, -0.05D, 0.05D);

        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 2.0D) {
            this.trailScale *= 0.98F;
        }

        if (this.trailScale < 0.2F || distance <= 1.0E-6D) {
            this.remove();
            return;
        }

        dx /= distance;
        dy /= distance;
        dz /= distance;
        double pull = 0.01D / Math.min(1.0D, distance);
        this.xd += dx * pull;
        this.yd += dy * pull;
        this.zd += dz * pull;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float pulse = Mth.sin((this.age - this.count) / 5.0F) * 0.25F + 1.0F;
        return 0.1F * this.trailScale * pulse;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    protected float getU0() {
        return U0;
    }

    @Override
    protected float getU1() {
        return U1;
    }

    @Override
    protected float getV0() {
        return V0;
    }

    @Override
    protected float getV1() {
        return V1;
    }
}
