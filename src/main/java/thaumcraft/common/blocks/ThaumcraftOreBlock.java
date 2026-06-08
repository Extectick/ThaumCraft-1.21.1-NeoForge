package thaumcraft.common.blocks;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class ThaumcraftOreBlock extends Block {
    private final OreType oreType;
    private final MapCodec<ThaumcraftOreBlock> codec;

    public ThaumcraftOreBlock(Properties properties, OreType oreType) {
        super(properties);
        this.oreType = oreType;
        this.codec = simpleCodec(blockProperties -> new ThaumcraftOreBlock(blockProperties, oreType));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return this.codec;
    }

    public OreType oreType() {
        return this.oreType;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack tool = params.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null || !tool.isCorrectToolForDrops(state)) {
            return List.of();
        }

        ServerLevel level = params.getLevel();
        if (enchantmentLevel(level, tool, Enchantments.SILK_TOUCH) > 0 || this.oreType == OreType.CINNABAR) {
            return List.of(new ItemStack(this));
        }

        int fortune = enchantmentLevel(level, tool, Enchantments.FORTUNE);
        if (this.oreType == OreType.AMBER) {
            return List.of(new ItemStack(this.oreType.drop(), 1 + level.random.nextInt(fortune + 1)));
        }

        int count = 1 + level.random.nextInt(2 + fortune);
        List<ItemStack> drops = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            drops.add(new ItemStack(this.oreType.drop()));
        }
        return drops;
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity,
            Entity breaker, ItemStack tool) {
        if (level instanceof ServerLevel serverLevel
                && enchantmentLevel(serverLevel, tool, Enchantments.SILK_TOUCH) > 0) {
            return 0;
        }
        RandomSource random = level.getRandom();
        return switch (this.oreType) {
            case CINNABAR -> 0;
            case AMBER -> 1 + random.nextInt(4);
            default -> random.nextInt(4);
        };
    }

    private static int enchantmentLevel(ServerLevel level, ItemStack stack,
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), stack);
    }

    public enum OreType {
        CINNABAR,
        AIR,
        FIRE,
        WATER,
        EARTH,
        ORDER,
        ENTROPY,
        AMBER;

        public Item drop() {
            return switch (this) {
                case AIR -> thaumcraft.common.registry.TCItems.AIR_SHARD.get();
                case FIRE -> thaumcraft.common.registry.TCItems.FIRE_SHARD.get();
                case WATER -> thaumcraft.common.registry.TCItems.WATER_SHARD.get();
                case EARTH -> thaumcraft.common.registry.TCItems.EARTH_SHARD.get();
                case ORDER -> thaumcraft.common.registry.TCItems.ORDER_SHARD.get();
                case ENTROPY -> thaumcraft.common.registry.TCItems.ENTROPY_SHARD.get();
                case AMBER -> thaumcraft.common.registry.TCItems.AMBER.get();
                case CINNABAR -> throw new IllegalStateException("Cinnabar drops its block");
            };
        }
    }
}
