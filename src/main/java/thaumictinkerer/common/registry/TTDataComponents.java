package thaumictinkerer.common.registry;


import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumictinkerer.ThaumicTinkerer;

import java.util.function.Supplier;

public class TTDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ThaumicTinkerer.MODID);

    public static final Supplier<DataComponentType<Boolean>> AWAKENED_ARMOR_MODE = REGISTRY.register("awakened_armor_mode",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<Integer>> TOOL_MODE = REGISTRY.register("tool_mode",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final Supplier<DataComponentType<Boolean>> TALISMAN_ACTIVE = REGISTRY.register("talisman_active",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<ResourceLocation>> STORED_BLOCK = REGISTRY.register("stored_block",
            () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Integer>> STORED_AMOUNT = REGISTRY.register("stored_amount",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final Supplier<DataComponentType<Integer>> STORED_XP = REGISTRY.register("stored_xp",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final Supplier<DataComponentType<java.util.UUID>> OWNER_UUID = REGISTRY.register("owner_uuid",
            () -> DataComponentType.<java.util.UUID>builder()
                    .persistent(net.minecraft.core.UUIDUtil.CODEC)
                    .networkSynchronized(net.minecraft.core.UUIDUtil.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<String>> OWNER_NAME = REGISTRY.register("owner_name",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
}







