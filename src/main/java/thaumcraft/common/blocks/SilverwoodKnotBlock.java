package thaumcraft.common.blocks;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class SilverwoodKnotBlock extends RotatedPillarBlock implements EntityBlock {
    public static final MapCodec<SilverwoodKnotBlock> CODEC = simpleCodec(SilverwoodKnotBlock::new);

    public SilverwoodKnotBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends SilverwoodKnotBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AuraNodeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.AURA_NODE.get()
                ? (tickLevel, pos, tickState, blockEntity) -> AuraNodeBlockEntity.tick(tickLevel, pos, tickState,
                        (AuraNodeBlockEntity) blockEntity)
                : null;
    }
}
