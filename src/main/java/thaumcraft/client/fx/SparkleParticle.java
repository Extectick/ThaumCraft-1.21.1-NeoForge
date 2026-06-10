package thaumcraft.client.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;


public class SparkleParticle extends SingleQuadParticle {
    private final int baseParticleIndex;
    private final int multiplier;

    private SparkleParticle(ClientLevel level, double x, double y, double z, float scale, float r, float g, float b, int lifetime) {
        super(level, x, y, z);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.gravity = 0;
        this.xd = this.yd = this.zd = 0;
        this.quadSize *= scale;
        this.lifetime = 3 * lifetime;
        this.multiplier = lifetime;
        this.baseParticleIndex = 16;
        this.hasPhysics = false;
        this.setSize(0.01F, 0.01F);
    }

    private SparkleParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float scale, float r, float g, float b,
                            int lifetime) {
        super(level, x, y, z);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.gravity = 0;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.quadSize *= scale;
        this.lifetime = 3 * lifetime;
        this.multiplier = lifetime;
        this.baseParticleIndex = 16;
        this.hasPhysics = false;
        this.setSize(0.01F, 0.01F);
    }

    private SparkleParticle(ClientLevel level, double x, double y, double z, float scale, int colorType, int lifetime) {
        this(level, x, y, z, scale, 0, 0, 0, lifetime);
        switch (colorType) {
            case 0 -> {
                this.rCol = 0.75F + level.random.nextFloat() * 0.25F;
                this.gCol = 0.25F + level.random.nextFloat() * 0.25F;
                this.bCol = 0.75F + level.random.nextFloat() * 0.25F;
            }
            case 1 -> {
                this.rCol = 0.5F + level.random.nextFloat() * 0.3F;
                this.gCol = 0.5F + level.random.nextFloat() * 0.3F;
                this.bCol = 0.2F;
            }
            case 2 -> {
                this.rCol = 0.2F;
                this.gCol = 0.2F;
                this.bCol = 0.7F + level.random.nextFloat() * 0.3F;
            }
            case 3 -> {
                this.rCol = 0.2F;
                this.gCol = 0.7F + level.random.nextFloat() * 0.3F;
                this.bCol = 0.2F;
            }
            case 4 -> {
                this.rCol = 0.7F + level.random.nextFloat() * 0.3F;
                this.gCol = 0.2F;
                this.bCol = 0.2F;
            }
            case 5 -> {
                this.rCol = level.random.nextFloat() * 0.1F;
                this.gCol = level.random.nextFloat() * 0.1F;
                this.bCol = level.random.nextFloat() * 0.1F;
            }
            case 6 -> {
                this.rCol = 0.8F + level.random.nextFloat() * 0.2F;
                this.gCol = 0.8F + level.random.nextFloat() * 0.2F;
                this.bCol = 0.8F + level.random.nextFloat() * 0.2F;
            }
            case 7 -> {
                this.rCol = 0.2F;
                this.gCol = 0.5F + level.random.nextFloat() * 0.3F;
                this.bCol = 0.6F + level.random.nextFloat() * 0.3F;
            }
        }
    }

    public static SparkleParticle createDirect(ClientLevel level, double x, double y, double z, float scale, int colorType, int lifetime) {
        return new SparkleParticle(level, x, y, z, scale, colorType, lifetime);
    }

    public static SparkleParticle createDirect(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float scale, int color,
                                               int lifetime) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        return new SparkleParticle(level, x, y, z, dx, dy, dz, scale, r, g, b, lifetime);
    }

    public void setGravity(float value) {
        this.gravity = value;
    }

    public void setNoClip(boolean value) {
        this.hasPhysics = !value;
    }

    @Override
    protected float getU0() {
        int part = this.baseParticleIndex + this.age / this.multiplier;
        return (part % 4) / 16.0F;
    }

    @Override
    protected float getU1() {
        return getU0() + 0.0624375F;
    }

    @Override
    protected float getV0() {
        return 0.25F;
    }

    @Override
    protected float getV1() {
        return 0.25F + 0.0624375F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }
        this.xd *= 0.908;
        this.yd *= 0.908;
        this.zd *= 0.908;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float size = this.quadSize;

        size *= (float) (this.lifetime - this.age + 1) / this.lifetime;
        return size;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240 << 16 | 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return SPARKLE_RENDER_TYPE;
    }

    public static final ParticleRenderType SPARKLE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, thaumcraft.Thaumcraft.id("textures/misc/particles.png"));
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMIC_SPARKLE";
        }
    };

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet ignored) {
        }

        @Override
        public SparkleParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
                                              double zSpeed) {
            return new SparkleParticle(level, x, y, z, 1.5F, 6, 6);
        }
    }
}


