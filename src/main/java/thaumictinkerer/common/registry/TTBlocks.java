package thaumictinkerer.common.registry;


import thaumictinkerer.common.registry.TTItems;

import static net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK;
import static net.minecraft.world.level.block.Blocks.WHEAT;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.blocks.InfusedCropBlock;

import thaumictinkerer.common.blocks.MagnetBlock;

import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK;

public class TTBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(ThaumicTinkerer.MODID);

    public static final DeferredBlock<Block> DARK_QUARTZ_BLOCK = REGISTRY.register("dark_quartz_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(QUARTZ_BLOCK)));

    public static final DeferredBlock<MagnetBlock> MOB_MAGNET = REGISTRY.registerBlock("mob_magnet", properties -> new MagnetBlock(true, properties.strength(2.0f)));
    public static final DeferredBlock<MagnetBlock> ITEM_MAGNET = REGISTRY.registerBlock("item_magnet", properties -> new MagnetBlock(false, properties.strength(2.0f)));

    public static final DeferredBlock<InfusedCropBlock> AIR_CROP = REGISTRY.registerBlock("air_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.AIR_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> FIRE_CROP = REGISTRY.registerBlock("fire_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.FIRE_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> WATER_CROP = REGISTRY.registerBlock("water_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.WATER_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> EARTH_CROP = REGISTRY.registerBlock("earth_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.EARTH_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> ORDER_CROP = REGISTRY.registerBlock("order_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.ORDER_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> ENTROPY_CROP = REGISTRY.registerBlock("entropy_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.ENTROPY_SEED::get));
    public static final DeferredBlock<InfusedCropBlock> COMPOUND_CROP = REGISTRY.registerBlock("compound_crop", p -> new InfusedCropBlock(BlockBehaviour.Properties.ofFullCopy(WHEAT).noCollission(), TTItems.COMPOUND_SEED::get));
}









