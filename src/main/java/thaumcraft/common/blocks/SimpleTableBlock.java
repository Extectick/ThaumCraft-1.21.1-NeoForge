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

public class SimpleTableBlock extends Block {
    public static final MapCodec<SimpleTableBlock> CODEC = simpleCodec(SimpleTableBlock::new);
    private static final VoxelShape TOP = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape LEG_NW = Block.box(2.0, 0.0, 2.0, 5.0, 12.0, 5.0);
    private static final VoxelShape LEG_NE = Block.box(11.0, 0.0, 2.0, 14.0, 12.0, 5.0);
    private static final VoxelShape LEG_SW = Block.box(2.0, 0.0, 11.0, 5.0, 12.0, 14.0);
    private static final VoxelShape LEG_SE = Block.box(11.0, 0.0, 11.0, 14.0, 12.0, 14.0);
    private static final VoxelShape SHAPE = Shapes.or(TOP, LEG_NW, LEG_NE, LEG_SW, LEG_SE);

    public SimpleTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SimpleTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
