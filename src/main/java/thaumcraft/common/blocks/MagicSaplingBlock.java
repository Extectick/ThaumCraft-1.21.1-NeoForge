package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.world.trees.GreatwoodTreeGenerator;
import thaumcraft.common.world.trees.SilverwoodTreeGenerator;

public class MagicSaplingBlock extends Block implements BonemealableBlock {
    public static final MapCodec<MagicSaplingBlock> CODEC = simpleCodec(properties -> new MagicSaplingBlock(properties, TreeKind.GREATWOOD));
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    private final TreeKind treeKind;

    public MagicSaplingBlock(BlockBehaviour.Properties properties, TreeKind treeKind) {
        super(properties);
        this.treeKind = treeKind;
    }

    @Override
    protected MapCodec<? extends MagicSaplingBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1) || level.getMaxLocalRawBrightness(pos.above()) < 9) {
            return;
        }
        int chance = this.treeKind == TreeKind.GREATWOOD ? 25 : 50;
        if (random.nextInt(chance) == 0) {
            this.growTree(level, pos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.growTree(level, pos, random);
    }

    private void growTree(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState sapling = level.getBlockState(pos);
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        boolean generated = this.treeKind == TreeKind.GREATWOOD
                ? GreatwoodTreeGenerator.generate(level, random, pos, false)
                : SilverwoodTreeGenerator.generate(level, random, pos, 7, 5);
        if (!generated) {
            level.setBlock(pos, sapling, 3);
        }
    }

    public enum TreeKind {
        GREATWOOD,
        SILVERWOOD
    }
}
