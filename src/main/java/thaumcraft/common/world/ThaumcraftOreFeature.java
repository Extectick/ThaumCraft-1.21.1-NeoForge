package thaumcraft.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.registry.TCBlocks;

public final class ThaumcraftOreFeature extends Feature<NoneFeatureConfiguration> {
    public ThaumcraftOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        int chunkX = context.origin().getX() & -16;
        int chunkZ = context.origin().getZ() & -16;
        boolean placed = false;

        if (ThaumcraftConfig.GENERATE_CINNABAR.get()) {
            int minY = level.getMinBuildHeight();
            int lowerWorldFifth = Math.max(1, level.getHeight() / 5);
            for (int i = 0; i < 18; i++) {
                int x = chunkX + random.nextInt(16);
                int y = minY + random.nextInt(lowerWorldFifth);
                int z = chunkZ + random.nextInt(16);
                placed |= replaceOverworldOreHost(level, new BlockPos(x, y, z), TCBlocks.CINNABAR_ORE.get());
            }
        }

        if (ThaumcraftConfig.GENERATE_AMBER.get()) {
            for (int i = 0; i < 20; i++) {
                int x = chunkX + random.nextInt(16);
                int z = chunkZ + random.nextInt(16);
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - random.nextInt(25);
                placed |= replaceOverworldOreHost(level, new BlockPos(x, y, z), TCBlocks.AMBER_ORE.get());
            }
        }

        if (ThaumcraftConfig.GENERATE_INFUSED_STONE.get()) {
            int minY = level.getMinBuildHeight();
            for (int i = 0; i < 8; i++) {
                int x = chunkX + random.nextInt(16);
                int z = chunkZ + random.nextInt(16);
                int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                int availableHeight = Math.max(5, surface - 5 - minY);
                int y = minY + random.nextInt(availableHeight);
                Block ore = randomInfusedOre(level, new BlockPos(x, y, z), random);
                placed |= generateOldVein(level, random, new BlockPos(x, y, z), ore, 6);
            }
        }
        return placed;
    }

    private static Block randomInfusedOre(WorldGenLevel level, BlockPos pos, RandomSource random) {
        int metadata = random.nextInt(6) + 1;
        if (random.nextInt(3) == 0) {
            Aspect aspect = AuraNodeBiomeRules.randomTag(level.getBiome(pos), random);
            metadata = switch (aspect == null ? Aspect.VOID : aspect) {
                case Aspect.AIR -> 1;
                case Aspect.FIRE -> 2;
                case Aspect.WATER -> 3;
                case Aspect.EARTH -> 4;
                case Aspect.ORDER -> 5;
                case Aspect.ENTROPY -> 6;
                default -> 1 + random.nextInt(6);
            };
        }
        return switch (metadata) {
            case 1 -> TCBlocks.INFUSED_AIR_ORE.get();
            case 2 -> TCBlocks.INFUSED_FIRE_ORE.get();
            case 3 -> TCBlocks.INFUSED_WATER_ORE.get();
            case 4 -> TCBlocks.INFUSED_EARTH_ORE.get();
            case 5 -> TCBlocks.INFUSED_ORDER_ORE.get();
            default -> TCBlocks.INFUSED_ENTROPY_ORE.get();
        };
    }

    private static boolean replaceOverworldOreHost(WorldGenLevel level, BlockPos pos, Block replacement) {
        if (!isOverworldOreHost(level, pos)) {
            return false;
        }
        return level.setBlock(pos, replacement.defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    private static boolean isOverworldOreHost(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.STONE_ORE_REPLACEABLES)
                || level.getBlockState(pos).is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    }

    /**
     * Direct port of the 1.7.10 WorldGenMinable ellipsoid algorithm.
     */
    private static boolean generateOldVein(WorldGenLevel level, RandomSource random, BlockPos origin,
            Block replacement, int veinSize) {
        float angle = random.nextFloat() * Mth.PI;
        double startX = origin.getX() + 8 + Mth.sin(angle) * veinSize / 8.0F;
        double endX = origin.getX() + 8 - Mth.sin(angle) * veinSize / 8.0F;
        double startZ = origin.getZ() + 8 + Mth.cos(angle) * veinSize / 8.0F;
        double endZ = origin.getZ() + 8 - Mth.cos(angle) * veinSize / 8.0F;
        double startY = origin.getY() + random.nextInt(3) - 2;
        double endY = origin.getY() + random.nextInt(3) - 2;
        boolean placed = false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int step = 0; step <= veinSize; step++) {
            double centerX = startX + (endX - startX) * step / veinSize;
            double centerY = startY + (endY - startY) * step / veinSize;
            double centerZ = startZ + (endZ - startZ) * step / veinSize;
            double randomScale = random.nextDouble() * veinSize / 16.0D;
            double diameterXZ = (Mth.sin(step * Mth.PI / veinSize) + 1.0F) * randomScale + 1.0D;
            double diameterY = (Mth.sin(step * Mth.PI / veinSize) + 1.0F) * randomScale + 1.0D;
            int minX = Mth.floor(centerX - diameterXZ / 2.0D);
            int minY = Mth.floor(centerY - diameterY / 2.0D);
            int minZ = Mth.floor(centerZ - diameterXZ / 2.0D);
            int maxX = Mth.floor(centerX + diameterXZ / 2.0D);
            int maxY = Mth.floor(centerY + diameterY / 2.0D);
            int maxZ = Mth.floor(centerZ + diameterXZ / 2.0D);

            for (int x = minX; x <= maxX; x++) {
                double normalizedX = (x + 0.5D - centerX) / (diameterXZ / 2.0D);
                if (normalizedX * normalizedX >= 1.0D) {
                    continue;
                }
                for (int y = minY; y <= maxY; y++) {
                    double normalizedY = (y + 0.5D - centerY) / (diameterY / 2.0D);
                    if (normalizedX * normalizedX + normalizedY * normalizedY >= 1.0D) {
                        continue;
                    }
                    for (int z = minZ; z <= maxZ; z++) {
                        double normalizedZ = (z + 0.5D - centerZ) / (diameterXZ / 2.0D);
                        if (normalizedX * normalizedX + normalizedY * normalizedY
                                + normalizedZ * normalizedZ >= 1.0D) {
                            continue;
                        }
                        cursor.set(x, y, z);
                        if (isOverworldOreHost(level, cursor)) {
                            placed |= level.setBlock(cursor, replacement.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                }
            }
        }
        return placed;
    }
}
