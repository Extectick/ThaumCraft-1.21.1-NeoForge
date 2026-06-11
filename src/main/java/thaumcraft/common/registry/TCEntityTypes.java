package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.common.entities.item.FollowingItemEntity;
import thaumcraft.common.entities.item.SpecialItemEntity;

public final class TCEntityTypes {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, Thaumcraft.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<FollowingItemEntity>> FOLLOWING_ITEM = REGISTRY.register("following_item", () -> {
        @SuppressWarnings("unchecked") EntityType<FollowingItemEntity> type = (EntityType<FollowingItemEntity>) (EntityType<?>) EntityType.Builder.of(
                FollowingItemEntity::create, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(2).build("following_item");
        return type;
    });

    public static final DeferredHolder<EntityType<?>, EntityType<SpecialItemEntity>> SPECIAL_ITEM = REGISTRY.register("special_item", () -> {
        @SuppressWarnings("unchecked") EntityType<SpecialItemEntity> type = (EntityType<SpecialItemEntity>) (EntityType<?>) EntityType.Builder.of(
                SpecialItemEntity::create, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(2).build("special_item");
        return type;
    });

    private TCEntityTypes() {
    }
}
