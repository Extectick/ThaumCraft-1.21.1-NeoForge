package thaumcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.services.ServerServices;

public final class ServerCrucibleHooks {
    private ServerCrucibleHooks() {
    }

    public static void tickCrucible(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        ServerServices.get().tickCrucible(level, pos, state, crucible);
    }

    public static void crucibleEntityInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible,
            Entity entity) {
        if (!level.isClientSide) {
            ServerServices.get().crucibleEntityInside(level, pos, state, crucible, entity);
        }
    }

    public static void spillCrucibleRemnants(Level level, BlockPos pos, CrucibleBlockEntity crucible) {
        if (!level.isClientSide) {
            ServerServices.get().spillCrucibleRemnants(level, pos, crucible);
        }
    }
}
