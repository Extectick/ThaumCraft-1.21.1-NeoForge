package thaumcraft.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import thaumcraft.Thaumcraft;

public record CycleWandFocusPayload() implements CustomPacketPayload {
    public static final CycleWandFocusPayload INSTANCE = new CycleWandFocusPayload();
    public static final Type<CycleWandFocusPayload> TYPE = new Type<>(Thaumcraft.id("cycle_wand_focus"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleWandFocusPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
