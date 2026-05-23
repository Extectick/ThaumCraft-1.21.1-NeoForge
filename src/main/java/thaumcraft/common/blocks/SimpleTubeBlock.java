package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimpleTubeBlock extends Block {
    public static final MapCodec<SimpleTubeBlock> CODEC = simpleCodec(SimpleTubeBlock::new);
    private static final VoxelShape CORE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final VoxelShape SHAPE = Shapes.or(
            CORE,
            Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 16.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D),
            Block.box(0.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D));

    public SimpleTubeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends SimpleTubeBlock> codec() {
        return CODEC;
    }
}
