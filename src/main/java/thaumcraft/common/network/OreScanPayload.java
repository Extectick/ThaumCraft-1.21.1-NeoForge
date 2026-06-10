package thaumcraft.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import thaumcraft.Thaumcraft;

public record OreScanPayload(BlockPos center, int range) implements CustomPacketPayload {
    public static final Type<OreScanPayload> TYPE = new Type<>(Thaumcraft.id("ore_scan"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OreScanPayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, OreScanPayload::center,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT, OreScanPayload::range, OreScanPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


