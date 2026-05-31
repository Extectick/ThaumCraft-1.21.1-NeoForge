package thaumcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.services.ServerServices;

public final class ServerEssentiaTransportHooks {
    private ServerEssentiaTransportHooks() {
    }

    public static void tickTube(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        ServerServices.get().tickTube(level, pos, state, tube);
    }

    public static void tickAlchemicalFurnace(Level level, BlockPos pos, BlockState state,
            AlchemicalFurnaceBlockEntity furnace) {
        ServerServices.get().tickAlchemicalFurnace(level, pos, state, furnace);
    }

    public static void tickWardedJar(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
        ServerServices.get().tickWardedJar(level, pos, state, jar);
    }

    public static int takeBufferEssentia(EssentiaTubeBlockEntity tube, Aspect aspect, int amount, Direction face) {
        return ServerServices.get().takeBufferEssentia(tube, aspect, amount, face);
    }
}
