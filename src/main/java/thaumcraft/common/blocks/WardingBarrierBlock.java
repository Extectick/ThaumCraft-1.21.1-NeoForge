package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.registry.TCBlocks;

import javax.annotation.ParametersAreNonnullByDefault;


public class WardingBarrierBlock extends Block {

    public static final MapCodec<WardingBarrierBlock> CODEC = simpleCodec(WardingBarrierBlock::new);

    private static final VoxelShape FULL = Block.box(0, 0, 0, 16, 16, 16);

    public WardingBarrierBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc) {
            Entity entity = ecc.getEntity();
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                for (int dy = 1; dy <= 2; dy++) {
                    BlockPos below = pos.below(dy);
                    if (level.getBlockState(below).is(TCBlocks.PAVING_STONE_WARDING.get())) {
                        if (level instanceof Level lvl) {
                            return lvl.hasNeighborSignal(below) ? Shapes.empty() : FULL;
                        }
                        return FULL;
                    }
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return 0.0F;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }
}




