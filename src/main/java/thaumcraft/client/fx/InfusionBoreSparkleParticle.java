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

public class InfusionBoreSparkleParticle extends SingleQuadParticle {
    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
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
            return "THAUMCRAFT_INFUSION_BORE_SPARKLE";
        }
    };

    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public InfusionBoreSparkleParticle(ClientLevel level, double x, double y, double z,
                                       double targetX, double targetY, double targetZ) {
        this(level, x, y, z, targetX, targetY, targetZ,
                0.4F + level.random.nextFloat() * 0.2F, 0.2F, 0.6F + level.random.nextFloat() * 0.3F);
    }

    public InfusionBoreSparkleParticle(ClientLevel level, double x, double y, double z,
                                       double targetX, double targetY, double targetZ,
                                       float red, float green, float blue) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F);
        this.gravity = 0.2F;
        this.hasPhysics = true;

        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + this.random.nextInt(base);

        float startJitter = 0.01F;
        this.xd = this.random.nextGaussian() * startJitter;
        this.yd = this.random.nextGaussian() * startJitter;
        this.zd = this.random.nextGaussian() * startJitter;

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
        if (this.age++ >= this.lifetime
                || (Mth.floor(this.x) == Mth.floor(this.targetX)
                && Mth.floor(this.y) == Mth.floor(this.targetY)
                && Mth.floor(this.z) == Mth.floor(this.targetZ))) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;

        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= 1.0E-6D) {
            this.remove();
            return;
        }

        double pull = 0.3D;
        if (distance < 4.0D) {
            this.quadSize *= 0.9F;
            pull = 0.6D;
        }

        this.xd += dx / distance * pull;
        this.yd += dy / distance * pull;
        this.zd += dz / distance * pull;
        this.xd = Mth.clamp(this.xd, -0.35D, 0.35D);
        this.yd = Mth.clamp(this.yd, -0.35D, 0.35D);
        this.zd = Mth.clamp(this.zd, -0.35D, 0.35D);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float bob = Mth.sin(this.age / 3.0F) * 0.5F + 1.0F;
        return this.quadSize * bob;
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
        int part = this.age % 4;
        return (part + 1.0F) / 16.0F - 0.0000625F;
    }

    @Override
    protected float getU1() {
        return (this.age % 4) / 16.0F;
    }

    @Override
    protected float getV0() {
        return 0.25F;
    }

    @Override
    protected float getV1() {
        return 0.3124375F;
    }
}
