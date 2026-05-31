package thaumcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.services.ServerServices;

public final class ServerFluxHooks {
    private ServerFluxHooks() {
    }

    public static void tickFlux(FluxBlock block, BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        ServerServices.get().tickFlux(block, state, level, pos, random);
    }

    public static void entityInside(FluxBlock block, BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        ServerServices.get().fluxEntityInside(block, state, level, pos, entity);
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
        if (level.isClientSide) {
            return;
        }
        ServerServices.get().placeFlux(level, pos, gas, metadata);
    }

    public static void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
        if (level.isClientSide || amount <= 0) {
            return;
        }
        ServerServices.get().addFlux(level, pos, gas, amount);
    }
}
