package thaumcraft.api;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.aspects.IEssentiaTransport;

public final class ThaumcraftApiHelper {
    private ThaumcraftApiHelper() {
    }

    @Nullable
    public static IEssentiaTransport getConnectableTransport(Level level, BlockPos pos, Direction face) {
        BlockEntity blockEntity = level.getBlockEntity(pos.relative(face));
        if (blockEntity instanceof IEssentiaTransport transport && transport.isConnectable(face.getOpposite())) {
            return transport;
        }
        return null;
    }
}
