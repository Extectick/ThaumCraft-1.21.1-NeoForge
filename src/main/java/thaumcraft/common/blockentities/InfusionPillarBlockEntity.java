package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

public class InfusionPillarBlockEntity extends BlockEntity {
    public InfusionPillarBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.INFUSION_PILLAR.get(), pos, blockState);
    }
}
