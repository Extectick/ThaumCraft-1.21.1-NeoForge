package thaumcraft.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.config.ThaumcraftConfig;
import thaumcraft.common.registry.TCBlocks;

public final class AuraNodeFeature extends Feature<NoneFeatureConfiguration> {
    public AuraNodeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!ThaumcraftConfig.GENERATE_AURA_NODES.get()) {
            return false;
        }

        RandomSource random = context.random();
        if (random.nextInt(ThaumcraftConfig.AURA_NODE_RARITY.get()) != 0) {
            return false;
        }

        WorldGenLevel level = context.level();
        int chunkX = context.origin().getX() & -16;
        int chunkZ = context.origin().getZ() & -16;
        int x = chunkX + random.nextInt(16);
        int z = chunkZ + random.nextInt(16);
        int y = firstUncoveredY(level, x, z);

        if (level.getBlockState(new BlockPos(x, y + 1, z)).isAir()) {
            y++;
        }

        int offset = random.nextInt(4);
        BlockPos offsetPos = new BlockPos(x, y + offset, z);
        if (level.getBlockState(offsetPos).isAir() || level.getBlockState(offsetPos).canBeReplaced()) {
            y += offset;
        }

        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
            return false;
        }

        BlockPos nodePos = new BlockPos(x, y, z);
        if (!level.setBlock(nodePos, TCBlocks.AURA_NODE.get().defaultBlockState(), Block.UPDATE_CLIENTS)) {
            return false;
        }
        AuraNodeGenerator.configureRandomNode(level, nodePos, random);
        return true;
    }

    private static int firstUncoveredY(WorldGenLevel level, int x, int z) {
        int y = Math.max(5, level.getMinBuildHeight());
        int maxY = level.getMaxBuildHeight() - 2;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, y + 1, z);
        while (y < maxY && !level.getBlockState(cursor).isAir()) {
            y++;
            cursor.setY(y + 1);
        }
        return y;
    }
}
