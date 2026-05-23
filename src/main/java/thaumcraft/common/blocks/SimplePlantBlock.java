package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SimplePlantBlock extends Block {
    public static final MapCodec<SimplePlantBlock> CODEC = simpleCodec(SimplePlantBlock::new);

    public SimplePlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SimplePlantBlock> codec() {
        return CODEC;
    }
}
