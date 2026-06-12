package thaumictinkerer.common.blocks;

import thaumictinkerer.common.registry.TTItems;
import static net.minecraft.world.level.block.Blocks.FARMLAND;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Supplier;

public class InfusedCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    
    private final Supplier<? extends ItemLike> seedSupplier;

    public InfusedCropBlock(Properties properties, Supplier<? extends ItemLike> seedSupplier) {
        super(properties);
        this.seedSupplier = seedSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return 1;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedSupplier.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(FARMLAND);
    }
}









