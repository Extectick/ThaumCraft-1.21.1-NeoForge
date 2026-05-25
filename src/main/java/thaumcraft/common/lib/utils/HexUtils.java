package thaumcraft.common.lib.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class HexUtils {
    private static final int[][] NEIGHBOURS = { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };

    private HexUtils() {
    }

    public static int getDistance(Hex a, Hex b) {
        return (Math.abs(a.q() - b.q()) + Math.abs(a.r() - b.r())
                + Math.abs(a.q() + a.r() - b.q() - b.r())) / 2;
    }

    public static Hex getRoundedHex(double q, double r) {
        return getRoundedCubicHex(q, r, -q - r).toHex();
    }

    public static CubicHex getRoundedCubicHex(double x, double y, double z) {
        int rx = (int) Math.round(x);
        int ry = (int) Math.round(y);
        int rz = (int) Math.round(z);
        double xDiff = Math.abs(rx - x);
        double yDiff = Math.abs(ry - y);
        double zDiff = Math.abs(rz - z);
        if (xDiff > yDiff && xDiff > zDiff) {
            rx = -ry - rz;
        } else if (yDiff > zDiff) {
            ry = -rx - rz;
        } else {
            rz = -rx - ry;
        }
        return new CubicHex(rx, ry, rz);
    }

    public static ArrayList<Hex> getRing(int radius) {
        Hex hex = new Hex(0, 0);
        for (int i = 0; i < radius; i++) {
            hex = hex.getNeighbour(4);
        }

        ArrayList<Hex> ring = new ArrayList<>();
        for (int direction = 0; direction < 6; direction++) {
            for (int step = 0; step < radius; step++) {
                ring.add(hex);
                hex = hex.getNeighbour(direction);
            }
        }
        return ring;
    }

    public static ArrayList<Hex> distributeRingRandomly(int radius, int entries, Random random) {
        ArrayList<Hex> ring = getRing(radius);
        ArrayList<Hex> results = new ArrayList<>();
        float spacing = (float) ring.size() / entries;
        float pos = random.nextInt(ring.size());
        for (int i = 0; i < entries; i++) {
            results.add(ring.get(Math.round(pos) % ring.size()));
            pos += spacing;
        }
        return results;
    }

    public static Map<String, Hex> generateHexes(int radius) {
        Map<String, Hex> results = new HashMap<>();
        Hex hex = new Hex(0, 0);
        results.put(hex.toString(), hex);

        for (int ring = 0; ring < radius; ring++) {
            hex = hex.getNeighbour(4);
            Hex cursor = new Hex(hex.q(), hex.r());
            for (int direction = 0; direction < 6; direction++) {
                for (int step = 0; step <= ring; step++) {
                    results.put(cursor.toString(), cursor);
                    cursor = cursor.getNeighbour(direction);
                }
            }
        }
        return results;
    }

    public record CubicHex(int x, int y, int z) {
        public Hex toHex() {
            return new Hex(this.x, this.z);
        }
    }

    public record Hex(int q, int r) {
        public static final Codec<Hex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("q").forGetter(Hex::q),
                Codec.INT.fieldOf("r").forGetter(Hex::r))
                .apply(instance, Hex::new));
        public static final StreamCodec<ByteBuf, Hex> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Hex::q,
                ByteBufCodecs.VAR_INT, Hex::r,
                Hex::new);

        public CubicHex toCubicHex() {
            return new CubicHex(this.q, this.r, -this.q - this.r);
        }

        public Pixel toPixel(int size) {
            return new Pixel(size * 1.5D * this.q, size * Math.sqrt(3.0D) * (this.r + this.q / 2.0D));
        }

        public Hex getNeighbour(int direction) {
            int[] offset = NEIGHBOURS[Math.floorMod(direction, NEIGHBOURS.length)];
            return new Hex(this.q + offset[0], this.r + offset[1]);
        }

        @Override
        public String toString() {
            return this.q + ":" + this.r;
        }
    }

    public record Pixel(double x, double y) {
        public Hex toHex(int size) {
            double q = 2.0D / 3.0D * this.x / size;
            double r = (1.0D / 3.0D * Math.sqrt(3.0D) * -this.y - 1.0D / 3.0D * this.x) / size;
            return HexUtils.getRoundedHex(q, r);
        }
    }
}
