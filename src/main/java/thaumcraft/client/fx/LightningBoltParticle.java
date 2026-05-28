package thaumcraft.client.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;

public class LightningBoltParticle extends Particle {
    private static final ParticleRenderType LARGE_RENDER_TYPE = renderType("large", "textures/misc/p_large.png");
    private static final ParticleRenderType SMALL_RENDER_TYPE = renderType("small", "textures/misc/p_small.png");

    private final List<Segment> segments = new ArrayList<>();
    private final Map<Integer, Integer> splitParents = new HashMap<>();
    private final Vec start;
    private final Vec end;
    private final Random boltRandom;
    private final boolean smallPass;
    private final float multiplier;
    private final float length;
    private final int increment;
    private int numSegments0 = 1;
    private int numSplits;
    private int boltAge;
    private final int boltMaxAge;

    private LightningBoltParticle(ClientLevel level, double fromX, double fromY, double fromZ,
                                  double toX, double toY, double toZ, long seed, boolean smallPass) {
        super(level, fromX, fromY, fromZ);
        this.start = new Vec(fromX, fromY, fromZ);
        this.end = new Vec(toX, toY, toZ);
        this.boltRandom = new Random(seed);
        this.smallPass = smallPass;
        this.multiplier = 4.0F;
        this.increment = 5;
        this.length = this.end.copy().sub(this.start).length();
        this.boltMaxAge = 10 + this.boltRandom.nextInt(10) - 5;
        this.boltAge = -((int)(this.length * 3.0F));
        this.segments.add(new Segment(new BoltPoint(this.start, new Vec(0.0D, 0.0D, 0.0D)),
                new BoltPoint(this.end, new Vec(0.0D, 0.0D, 0.0D)), 1.0F, 0, 0));
        this.defaultFractal();
        this.finalizeBolt();
    }

    public static void spawn(ClientLevel level, double fromX, double fromY, double fromZ,
                             double toX, double toY, double toZ) {
        long seed = level.random.nextLong();
        Minecraft.getInstance().particleEngine.add(
                new LightningBoltParticle(level, fromX, fromY, fromZ, toX, toY, toZ, seed, false));
        Minecraft.getInstance().particleEngine.add(
                new LightningBoltParticle(level, fromX, fromY, fromZ, toX, toY, toZ, seed, true));
    }

    @Override
    public void tick() {
        this.boltAge += this.increment;
        if (this.boltAge > this.boltMaxAge) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float boltAgeFraction = this.boltAge >= 0 ? (float)this.boltAge / this.boltMaxAge : 0.0F;
        float alpha = this.smallPass ? 1.0F - boltAgeFraction * 0.5F : (1.0F - boltAgeFraction) * 0.4F;
        float red = this.smallPass ? 1.0F : 0.6F;
        float green = this.smallPass ? 0.6F : 0.3F;
        float blue = 1.0F;
        int renderLength = (int)((this.boltAge + partialTicks + (int)(this.length * 3.0F))
                / Math.max(1, (int)(this.length * 3.0F)) * this.numSegments0);
        Vec3 cameraPos = camera.getPosition();
        Vec look = new Vec(camera.getLookVector().x(), camera.getLookVector().y(), camera.getLookVector().z());

        for (Segment segment : this.segments) {
            if (segment.segmentNo > renderLength) {
                continue;
            }
            Vec diff = segment.diff.copy().normalize();
            Vec side = Vec.cross(look, diff);
            if (side.length() < 1.0E-4F) {
                side = Vec.cross(new Vec(0.0D, 1.0D, 0.0D), diff);
            }
            side.normalize();
            float width = (this.smallPass ? 0.03F : 0.045F)
                    * ((segment.start.point.copy().sub(new Vec(cameraPos.x, cameraPos.y, cameraPos.z)).length() / 5.0F) + 1.0F)
                    * (1.0F + segment.light) * 0.5F;
            Vec offset = side.scale(width);
            addBoltQuad(buffer, cameraPos, segment.start.point, segment.end.point, offset, red, green, blue,
                    alpha * segment.light);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.smallPass ? SMALL_RENDER_TYPE : LARGE_RENDER_TYPE;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return new AABB(this.start.x, this.start.y, this.start.z, this.end.x, this.end.y, this.end.z).inflate(2.0D);
    }

    private static void addBoltQuad(VertexConsumer buffer, Vec3 camera, Vec start, Vec end, Vec offset,
                                    float red, float green, float blue, float alpha) {
        addVertex(buffer, end.copy().sub(offset), camera, 0.5F, 0.0F, red, green, blue, alpha);
        addVertex(buffer, start.copy().sub(offset), camera, 0.5F, 0.0F, red, green, blue, alpha);
        addVertex(buffer, start.copy().add(offset), camera, 0.5F, 1.0F, red, green, blue, alpha);
        addVertex(buffer, end.copy().add(offset), camera, 0.5F, 1.0F, red, green, blue, alpha);
    }

    private static void addVertex(VertexConsumer buffer, Vec point, Vec3 camera, float u, float v,
                                  float red, float green, float blue, float alpha) {
        buffer.addVertex((float)(point.x - camera.x), (float)(point.y - camera.y), (float)(point.z - camera.z))
                .setUv(u, v)
                .setColor(red, green, blue, alpha)
                .setLight(LightTexture.FULL_BRIGHT);
    }

    private void defaultFractal() {
        this.fractal(2, this.length * this.multiplier / 8.0F, 0.7F, 0.1F, 45.0F);
        this.fractal(2, this.length * this.multiplier / 12.0F, 0.5F, 0.1F, 50.0F);
        this.fractal(2, this.length * this.multiplier / 17.0F, 0.5F, 0.1F, 55.0F);
        this.fractal(2, this.length * this.multiplier / 23.0F, 0.5F, 0.1F, 60.0F);
        this.fractal(2, this.length * this.multiplier / 30.0F, 0.0F, 0.0F, 0.0F);
        this.fractal(2, this.length * this.multiplier / 34.0F, 0.0F, 0.0F, 0.0F);
        this.fractal(2, this.length * this.multiplier / 40.0F, 0.0F, 0.0F, 0.0F);
    }

    private void fractal(int splits, float amount, float splitChance, float splitLength, float splitAngle) {
        List<Segment> oldSegments = new ArrayList<>(this.segments);
        this.segments.clear();
        Segment previous = null;
        for (Segment segment : oldSegments) {
            previous = segment.previous;
            Vec subSegment = segment.diff.copy().scale(1.0F / splits);
            BoltPoint[] points = new BoltPoint[splits + 1];
            points[0] = segment.start;
            points[splits] = segment.end;
            for (int i = 1; i < splits; i++) {
                Vec randomOffset = Vec.perpendicular(segment.diff).rotate(this.boltRandom.nextFloat() * 360.0F, segment.diff);
                randomOffset.scale((this.boltRandom.nextFloat() - 0.5F) * amount);
                Vec basePoint = segment.start.point.copy().add(subSegment.copy().scale(i));
                points[i] = new BoltPoint(basePoint, randomOffset);
            }
            for (int i = 0; i < splits; i++) {
                Segment next = new Segment(points[i], points[i + 1], segment.light, segment.segmentNo * splits + i, segment.splitNo);
                next.previous = previous;
                if (previous != null) {
                    previous.next = next;
                }
                if (i != 0 && this.boltRandom.nextFloat() < splitChance) {
                    Vec splitRotation = Vec.xCross(next.diff).rotate(this.boltRandom.nextFloat() * 360.0F, next.diff);
                    Vec splitDiff = next.diff.copy()
                            .rotate((this.boltRandom.nextFloat() * 0.66F + 0.33F) * splitAngle, splitRotation)
                            .scale(splitLength);
                    this.numSplits++;
                    this.splitParents.put(this.numSplits, next.splitNo);
                    Segment split = new Segment(points[i],
                            new BoltPoint(points[i + 1].basePoint, points[i + 1].offset.copy().add(splitDiff)),
                            segment.light / 2.0F, next.segmentNo, this.numSplits);
                    split.previous = previous;
                    this.segments.add(split);
                }
                previous = next;
                this.segments.add(next);
            }
            if (segment.next != null) {
                segment.next.previous = previous;
            }
        }
        this.numSegments0 *= splits;
    }

    private void finalizeBolt() {
        this.segments.sort(Comparator.comparingInt((Segment segment) -> segment.splitNo)
                .thenComparingInt(segment -> segment.segmentNo));
        Map<Integer, Integer> lastActiveSegment = new HashMap<>();
        int lastSplitCalc = 0;
        int lastActive = 0;
        for (Segment segment : this.segments) {
            if (segment.splitNo > lastSplitCalc) {
                lastActiveSegment.put(lastSplitCalc, lastActive);
                lastSplitCalc = segment.splitNo;
                lastActive = lastActiveSegment.getOrDefault(this.splitParents.get(segment.splitNo), 0);
            }
            lastActive = segment.segmentNo;
        }
        lastActiveSegment.put(lastSplitCalc, lastActive);
        lastSplitCalc = 0;
        lastActive = lastActiveSegment.getOrDefault(0, 0);
        Iterator<Segment> iterator = this.segments.iterator();
        while (iterator.hasNext()) {
            Segment segment = iterator.next();
            if (lastSplitCalc != segment.splitNo) {
                lastSplitCalc = segment.splitNo;
                lastActive = lastActiveSegment.getOrDefault(segment.splitNo, 0);
            }
            if (segment.segmentNo > lastActive) {
                iterator.remove();
            }
        }
        this.segments.sort(Comparator.comparing((Segment segment) -> segment.light).reversed());
    }

    private static ParticleRenderType renderType(String name, String texture) {
        return new ParticleRenderType() {
            @Nullable
            @Override
            public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, Thaumcraft.id(texture));
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.depthMask(false);
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public String toString() {
                return "THAUMCRAFT_LIGHTNING_BOLT_" + name;
            }
        };
    }

    private static class BoltPoint {
        private final Vec point;
        private final Vec basePoint;
        private final Vec offset;

        private BoltPoint(Vec basePoint, Vec offset) {
            this.basePoint = basePoint.copy();
            this.offset = offset.copy();
            this.point = basePoint.copy().add(offset);
        }
    }

    private static class Segment {
        private final BoltPoint start;
        private final BoltPoint end;
        private final Vec diff;
        private Segment previous;
        private Segment next;
        private final float light;
        private final int segmentNo;
        private final int splitNo;

        private Segment(BoltPoint start, BoltPoint end, float light, int segmentNo, int splitNo) {
            this.start = start;
            this.end = end;
            this.light = light;
            this.segmentNo = segmentNo;
            this.splitNo = splitNo;
            this.diff = end.point.copy().sub(start.point);
        }
    }

    private static class Vec {
        private double x;
        private double y;
        private double z;

        private Vec(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Vec copy() {
            return new Vec(this.x, this.y, this.z);
        }

        private Vec add(Vec vec) {
            this.x += vec.x;
            this.y += vec.y;
            this.z += vec.z;
            return this;
        }

        private Vec sub(Vec vec) {
            this.x -= vec.x;
            this.y -= vec.y;
            this.z -= vec.z;
            return this;
        }

        private Vec scale(double scale) {
            this.x *= scale;
            this.y *= scale;
            this.z *= scale;
            return this;
        }

        private Vec normalize() {
            double length = this.length();
            if (length > 1.0E-6D) {
                this.x /= length;
                this.y /= length;
                this.z /= length;
            }
            return this;
        }

        private float length() {
            return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }

        private Vec rotate(double degrees, Vec axis) {
            Vec normalizedAxis = axis.copy().normalize();
            double radians = Math.toRadians(degrees);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double dot = this.x * normalizedAxis.x + this.y * normalizedAxis.y + this.z * normalizedAxis.z;
            double newX = this.x * cos + (normalizedAxis.y * this.z - normalizedAxis.z * this.y) * sin
                    + normalizedAxis.x * dot * (1.0D - cos);
            double newY = this.y * cos + (normalizedAxis.z * this.x - normalizedAxis.x * this.z) * sin
                    + normalizedAxis.y * dot * (1.0D - cos);
            double newZ = this.z * cos + (normalizedAxis.x * this.y - normalizedAxis.y * this.x) * sin
                    + normalizedAxis.z * dot * (1.0D - cos);
            this.x = newX;
            this.y = newY;
            this.z = newZ;
            return this;
        }

        private static Vec cross(Vec left, Vec right) {
            return new Vec(left.y * right.z - left.z * right.y,
                    left.z * right.x - left.x * right.z,
                    left.x * right.y - left.y * right.x);
        }

        private static Vec xCross(Vec vec) {
            return new Vec(0.0D, vec.z, -vec.y);
        }

        private static Vec zCross(Vec vec) {
            return new Vec(-vec.y, vec.x, 0.0D);
        }

        private static Vec perpendicular(Vec vec) {
            return Math.abs(vec.z) < 1.0E-6D ? zCross(vec) : xCross(vec);
        }
    }
}
