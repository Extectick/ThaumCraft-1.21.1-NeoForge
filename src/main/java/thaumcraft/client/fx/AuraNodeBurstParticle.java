package thaumcraft.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
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
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;

public class AuraNodeBurstParticle extends SingleQuadParticle {
    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, Thaumcraft.id("textures/misc/nodes.png"));
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMCRAFT_AURA_NODE_BURST";
        }
    };

    public AuraNodeBurstParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.quadSize = 1.0F;
        this.lifetime = 31;
        this.hasPhysics = false;
    }

    public static void spawn(ClientLevel level, double x, double y, double z) {
        Minecraft.getInstance().particleEngine.add(new AuraNodeBurstParticle(level, x, y, z));
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    @Override
    protected float getU0() {
        return this.age / 32.0F;
    }

    @Override
    protected float getU1() {
        return (this.age + 1.0F) / 32.0F;
    }

    @Override
    protected float getV0() {
        return 31.0F / 32.0F;
    }

    @Override
    protected float getV1() {
        return 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }
}
