package thaumcraft.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BlockRunesParticle extends Particle {
    private final double offsetX;
    private final double offsetY;
    private final float rotation;
    private final int runeIndex;
    private final float runeScale;

    public BlockRunesParticle(ClientLevel level, double x, double y, double z, float red, float green, float blue,
            int duration, float gravity) {
        super(level, x, y, z);
        this.rotation = this.random.nextInt(4) * 90.0F;
        this.rCol = red == 0.0F ? 1.0F : red;
        this.gCol = green;
        this.bCol = blue;
        this.gravity = gravity;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.lifetime = 3 * duration;
        this.hasPhysics = false;
        this.runeIndex = 224 + this.random.nextInt(16);
        this.offsetX = this.random.nextFloat() * 0.2D;
        this.offsetY = -0.3D + this.random.nextFloat() * 0.6D;
        this.runeScale = (float)(1.0D + this.random.nextGaussian() * 0.1D);
        this.alpha = 0.0F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float threshold = this.lifetime / 5.0F;
        if (this.age <= threshold) {
            this.alpha = this.age / threshold;
        } else {
            this.alpha = (float)(this.lifetime - this.age) / this.lifetime;
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd -= 0.04D * this.gravity;
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        double renderX = this.xo + (this.x - this.xo) * partialTicks;
        double renderY = this.yo + (this.y - this.yo) * partialTicks;
        double renderZ = this.zo + (this.z - this.zo) * partialTicks;
        float minU = (this.runeIndex % 16) / 16.0F;
        float maxU = minU + 0.0624375F;
        float minV = 0.375F;
        float maxV = minV + 0.0624375F;
        float size = 0.3F * this.runeScale;
        float half = size * 0.5F;
        float alpha = this.alpha / 2.0F;

        addRuneVertex(buffer, cameraPos, renderX, renderY, renderZ, -half, half, maxU, maxV, alpha);
        addRuneVertex(buffer, cameraPos, renderX, renderY, renderZ, half, half, maxU, minV, alpha);
        addRuneVertex(buffer, cameraPos, renderX, renderY, renderZ, half, -half, minU, minV, alpha);
        addRuneVertex(buffer, cameraPos, renderX, renderY, renderZ, -half, -half, minU, maxV, alpha);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EssentiaTrailParticle.RENDER_TYPE;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return new AABB(this.x - 1.0D, this.y - 1.0D, this.z - 1.0D,
                this.x + 1.0D, this.y + 1.0D, this.z + 1.0D);
    }

    private void addRuneVertex(VertexConsumer buffer, Vec3 cameraPos, double renderX, double renderY, double renderZ,
            float x, float y, float u, float v, float alpha) {
        Vector3f point = new Vector3f((float)(x + this.offsetX), (float)(y + this.offsetY), -0.51F);
        rotateZ(point, 90.0F);
        rotateY(point, this.rotation);
        buffer.addVertex((float)(renderX + point.x - cameraPos.x),
                        (float)(renderY + point.y - cameraPos.y),
                        (float)(renderZ + point.z - cameraPos.z))
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, alpha)
                .setLight(LightTexture.FULL_BRIGHT);
    }

    private static void rotateY(Vector3f point, float degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float x = point.x * cos + point.z * sin;
        float z = point.z * cos - point.x * sin;
        point.x = x;
        point.z = z;
    }

    private static void rotateZ(Vector3f point, float degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float x = point.x * cos - point.y * sin;
        float y = point.x * sin + point.y * cos;
        point.x = x;
        point.y = y;
    }
}
