package thaumcraft.common.world.trees;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlocks;

public final class GreatwoodTreeGenerator {
    private static final byte[] OTHER_COORD_PAIRS = new byte[] { 2, 0, 0, 1, 2, 1 };

    private final Random random;
    private final Level level;
    private final BlockState log = TCBlocks.GREATWOOD_LOG.get().defaultBlockState();
    private final BlockState leaves = TCBlocks.GREATWOOD_LEAVES.get().defaultBlockState();

    private int[] basePos = new int[] { 0, 0, 0 };
    private int heightLimit;
    private int height;
    private double heightAttenuation = 0.618D;
    private double branchSlope = 0.38D;
    private double scaleWidth = 1.2D;
    private double leafDensity = 0.9D;
    private int trunkSize = 2;
    private int heightLimitLimit = 11;
    private int leafDistanceLimit = 4;
    private int[][] leafNodes;

    private GreatwoodTreeGenerator(Level level, Random random) {
        this.level = level;
        this.random = random;
    }

    public static boolean generate(Level level, RandomSource random, BlockPos pos, boolean spiders) {
        return new GreatwoodTreeGenerator(level, new Random(random.nextLong())).generate(pos, spiders);
    }

    private boolean generate(BlockPos pos, boolean spiders) {
        this.basePos[0] = pos.getX();
        this.basePos[1] = pos.getY();
        this.basePos[2] = pos.getZ();
        if (this.heightLimit == 0) {
            this.heightLimit = this.heightLimitLimit + this.random.nextInt(this.heightLimitLimit);
        }

        boolean valid = false;
        for (int a = -1; a < 2; a++) {
            for (int b = -1; b < 2; b++) {
                boolean locationValid = true;
                for (int x = 0; x < this.trunkSize; x++) {
                    for (int z = 0; z < this.trunkSize; z++) {
                        if (!this.validTreeLocation(x + a, z + b)) {
                            locationValid = false;
                            break;
                        }
                    }
                    if (!locationValid) {
                        break;
                    }
                }
                if (!locationValid) {
                    continue;
                }
                valid = true;
                this.basePos[0] += a;
                this.basePos[2] += b;
                a = 2;
                break;
            }
        }

        if (!valid) {
            return false;
        }

        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateLeafNodeBases();
        this.generateTrunk();

        this.scaleWidth = 1.66D;
        this.basePos[0] = pos.getX();
        this.basePos[1] = pos.getY() + this.height;
        this.basePos[2] = pos.getZ();
        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateLeafNodeBases();
        this.generateTrunk();

        if (spiders) {
            this.level.setBlock(pos.below(), Blocks.SPAWNER.defaultBlockState(), 3);
        }
        return true;
    }

    private void generateLeafNodeList() {
        this.height = (int) (this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int nodeCount = (int) (1.382D + Math.pow(this.leafDensity * this.heightLimit / 13.0D, 2.0D));
        if (nodeCount < 1) {
            nodeCount = 1;
        }

        int[][] nodes = new int[nodeCount * this.heightLimit][4];
        int y = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int count = 1;
        int trunkTop = this.basePos[1] + this.height;
        int layer = y - this.basePos[1];
        nodes[0][0] = this.basePos[0];
        nodes[0][1] = y;
        nodes[0][2] = this.basePos[2];
        nodes[0][3] = trunkTop;
        y--;

        while (layer >= 0) {
            float layerSize = this.layerSize(layer);
            if (layerSize < 0.0F) {
                y--;
                layer--;
                continue;
            }

            for (int i = 0; i < nodeCount; i++) {
                double radius = this.scaleWidth * layerSize * (this.random.nextFloat() + 0.328D);
                double angle = this.random.nextFloat() * 2.0D * Math.PI;
                int x = floor(radius * Math.sin(angle) + this.basePos[0] + 0.5D);
                int z = floor(radius * Math.cos(angle) + this.basePos[2] + 0.5D);
                int[] leafStart = new int[] { x, y, z };
                int[] leafEnd = new int[] { x, y + this.leafDistanceLimit, z };
                if (this.checkBlockLine(leafStart, leafEnd) == -1) {
                    int[] branchBase = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
                    double horizontalDistance = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - leafStart[0]), 2.0D)
                            + Math.pow(Math.abs(this.basePos[2] - leafStart[2]), 2.0D));
                    double branchDrop = horizontalDistance * this.branchSlope;
                    branchBase[1] = leafStart[1] - branchDrop > trunkTop ? trunkTop : (int) (leafStart[1] - branchDrop);
                    if (this.checkBlockLine(branchBase, leafStart) == -1) {
                        nodes[count][0] = x;
                        nodes[count][1] = y;
                        nodes[count][2] = z;
                        nodes[count][3] = branchBase[1];
                        count++;
                    }
                }
            }
            y--;
            layer--;
        }

        this.leafNodes = new int[count][4];
        System.arraycopy(nodes, 0, this.leafNodes, 0, count);
    }

    private void genTreeLayer(int x, int y, int z, float radius, byte axis) {
        int diameter = (int) (radius + 0.618D);
        byte axisA = OTHER_COORD_PAIRS[axis];
        byte axisB = OTHER_COORD_PAIRS[axis + 3];
        int[] center = new int[] { x, y, z };
        int[] cursor = new int[] { 0, 0, 0 };
        cursor[axis] = center[axis];

        for (int a = -diameter; a <= diameter; a++) {
            cursor[axisA] = center[axisA] + a;
            for (int b = -diameter; b <= diameter; b++) {
                double distance = Math.pow(Math.abs(a) + 0.5D, 2.0D) + Math.pow(Math.abs(b) + 0.5D, 2.0D);
                if (distance <= radius * radius) {
                    cursor[axisB] = center[axisB] + b;
                    BlockPos leafPos = pos(cursor);
                    BlockState state = this.level.getBlockState(leafPos);
                    if (state.isAir()
                            || state.is(TCBlocks.GREATWOOD_LEAVES.get())
                            || state.is(TCBlocks.SILVERWOOD_LEAVES.get())) {
                        TreeGenerationUtil.setLeaf(this.level, leafPos, this.leaves);
                    }
                }
            }
        }
    }

    private float layerSize(int layer) {
        if (layer < this.heightLimit * 0.3D) {
            return -1.618F;
        }

        float half = this.heightLimit / 2.0F;
        float offset = this.heightLimit / 2.0F - layer;
        float size;
        if (offset == 0.0F) {
            size = half;
        } else if (Math.abs(offset) >= half) {
            size = 0.0F;
        } else {
            size = (float) Math.sqrt(Math.pow(Math.abs(half), 2.0D) - Math.pow(Math.abs(offset), 2.0D));
        }
        return size * 0.5F;
    }

    private float leafSize(int layer) {
        return layer >= 0 && layer < this.leafDistanceLimit
                ? (layer != 0 && layer != this.leafDistanceLimit - 1 ? 3.0F : 2.0F)
                : -1.0F;
    }

    private void generateLeafNode(int x, int y, int z) {
        for (int currentY = y; currentY < y + this.leafDistanceLimit; currentY++) {
            this.genTreeLayer(x, currentY, z, this.leafSize(currentY - y), (byte) 1);
        }
    }

    private void placeBlockLine(int[] start, int[] end) {
        int[] delta = new int[] { end[0] - start[0], end[1] - start[1], end[2] - start[2] };
        byte mainAxis = 0;
        for (byte axis = 0; axis < 3; axis++) {
            if (Math.abs(delta[axis]) > Math.abs(delta[mainAxis])) {
                mainAxis = axis;
            }
        }
        if (delta[mainAxis] == 0) {
            return;
        }

        byte axisA = OTHER_COORD_PAIRS[mainAxis];
        byte axisB = OTHER_COORD_PAIRS[mainAxis + 3];
        byte step = (byte) (delta[mainAxis] > 0 ? 1 : -1);
        double slopeA = (double) delta[axisA] / delta[mainAxis];
        double slopeB = (double) delta[axisB] / delta[mainAxis];
        int[] cursor = new int[] { 0, 0, 0 };
        for (int offset = 0, endOffset = delta[mainAxis] + step; offset != endOffset; offset += step) {
            cursor[mainAxis] = floor(start[mainAxis] + offset + 0.5D);
            cursor[axisA] = floor(start[axisA] + offset * slopeA + 0.5D);
            cursor[axisB] = floor(start[axisB] + offset * slopeB + 0.5D);
            Direction.Axis logAxis = Direction.Axis.Y;
            int dx = Math.abs(cursor[0] - start[0]);
            int dz = Math.abs(cursor[2] - start[2]);
            int horizontal = Math.max(dx, dz);
            if (horizontal > 0) {
                logAxis = dx == horizontal ? Direction.Axis.X : Direction.Axis.Z;
            }
            TreeGenerationUtil.setLog(this.level, pos(cursor), this.log, logAxis);
        }
    }

    private void generateLeaves() {
        for (int[] node : this.leafNodes) {
            this.generateLeafNode(node[0], node[1], node[2]);
        }
    }

    private boolean leafNodeNeedsBase(int heightFromBase) {
        return heightFromBase >= this.heightLimit * 0.2D;
    }

    private void generateTrunk() {
        int x = this.basePos[0];
        int y = this.basePos[1];
        int topY = this.basePos[1] + this.height;
        int z = this.basePos[2];
        int[] start = new int[] { x, y, z };
        int[] end = new int[] { x, topY, z };
        this.placeBlockLine(start, end);
        if (this.trunkSize == 2) {
            start[0]++;
            end[0]++;
            this.placeBlockLine(start, end);
            start[2]++;
            end[2]++;
            this.placeBlockLine(start, end);
            start[0]--;
            end[0]--;
            this.placeBlockLine(start, end);
        }
    }

    private void generateLeafNodeBases() {
        int[] base = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
        for (int[] node : this.leafNodes) {
            int[] leaf = new int[] { node[0], node[1], node[2] };
            base[1] = node[3];
            int heightFromBase = base[1] - this.basePos[1];
            if (this.leafNodeNeedsBase(heightFromBase)) {
                this.placeBlockLine(base, leaf);
            }
        }
    }

    private int checkBlockLine(int[] start, int[] end) {
        int[] delta = new int[] { end[0] - start[0], end[1] - start[1], end[2] - start[2] };
        byte mainAxis = 0;
        for (byte axis = 0; axis < 3; axis++) {
            if (Math.abs(delta[axis]) > Math.abs(delta[mainAxis])) {
                mainAxis = axis;
            }
        }
        if (delta[mainAxis] == 0) {
            return -1;
        }

        byte axisA = OTHER_COORD_PAIRS[mainAxis];
        byte axisB = OTHER_COORD_PAIRS[mainAxis + 3];
        byte step = (byte) (delta[mainAxis] > 0 ? 1 : -1);
        double slopeA = (double) delta[axisA] / delta[mainAxis];
        double slopeB = (double) delta[axisB] / delta[mainAxis];
        int[] cursor = new int[] { 0, 0, 0 };
        int offset = 0;
        int endOffset = delta[mainAxis] + step;
        for (; offset != endOffset; offset += step) {
            cursor[mainAxis] = start[mainAxis] + offset;
            cursor[axisA] = floor(start[axisA] + offset * slopeA);
            cursor[axisB] = floor(start[axisB] + offset * slopeB);
            BlockState state = this.level.getBlockState(pos(cursor));
            if (!state.isAir()
                    && !state.is(TCBlocks.GREATWOOD_LEAVES.get())
                    && !state.is(TCBlocks.SILVERWOOD_LEAVES.get())) {
                break;
            }
        }
        return offset == endOffset ? -1 : Math.abs(offset);
    }

    private boolean validTreeLocation(int offsetX, int offsetZ) {
        BlockPos soil = new BlockPos(this.basePos[0] + offsetX, this.basePos[1] - 1, this.basePos[2] + offsetZ);
        if (!TreeGenerationUtil.isSoil(this.level, soil)) {
            return false;
        }
        int[] start = new int[] { soil.getX(), this.basePos[1], soil.getZ() };
        int[] end = new int[] { soil.getX(), this.basePos[1] + this.heightLimit - 1, soil.getZ() };
        int clear = this.checkBlockLine(start, end);
        if (clear == -1) {
            return true;
        }
        if (clear < 6) {
            return false;
        }
        this.heightLimit = clear;
        return true;
    }

    private static BlockPos pos(int[] xyz) {
        return new BlockPos(xyz[0], xyz[1], xyz[2]);
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }
}
