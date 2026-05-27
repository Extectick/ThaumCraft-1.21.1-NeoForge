package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.BrainInAJarBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class BrainInAJarBlock extends SimpleJarBlock implements EntityBlock {
    public static final MapCodec<BrainInAJarBlock> CODEC = simpleCodec(BrainInAJarBlock::new);

    public BrainInAJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BrainInAJarBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrainInAJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.BRAIN_IN_A_JAR.get()
                ? (tickLevel, pos, tickState, blockEntity) -> BrainInAJarBlockEntity.tick(tickLevel, pos, tickState,
                        (BrainInAJarBlockEntity) blockEntity)
                : null;
    }
}
