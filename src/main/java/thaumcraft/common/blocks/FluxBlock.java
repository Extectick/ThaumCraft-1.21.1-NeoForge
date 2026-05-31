package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.util.ServerFluxHooks;

public class FluxBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 7);
    public static final int FULL_LEVEL = 7;
    public static final int FLOW_TICK_RATE = 20;
    private final boolean gas;
    private final MapCodec<FluxBlock> codec;

    public FluxBlock(Properties properties, boolean gas) {
        super(properties);
        this.gas = gas;
        this.codec = simpleCodec(blockProperties -> new FluxBlock(blockProperties, gas));
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, FULL_LEVEL));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return this.codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) {
        return state.getValue(LEVEL) < 2;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.isCreative() ? super.getDestroyProgress(state, player, level, pos) : 0.0F;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        int fluxLevel = state.getValue(LEVEL);
        if (this.gas) {
            ServerFluxHooks.entityInside(this, state, level, pos, entity);
            return;
        }

        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D - (fluxLevel / 8.0D), 1.0D,
                1.0D - (fluxLevel / 8.0D)));
        ServerFluxHooks.entityInside(this, state, level, pos, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ServerFluxHooks.tickFlux(this, state, level, pos, random);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, FLOW_TICK_RATE);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos,
            boolean movedByPiston) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, FLOW_TICK_RATE);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    public static int getQuantaValue(BlockState state) {
        return state.getValue(LEVEL) + 1;
    }

    public boolean isGas() {
        return this.gas;
    }

    public static BlockState fluxState(boolean gas, int metadata) {
        return (gas ? TCBlocks.FLUX_GAS.get() : TCBlocks.FLUX_GOO.get()).defaultBlockState()
                .setValue(LEVEL, Math.clamp(metadata, 0, FULL_LEVEL));
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas) {
        placeFlux(level, pos, gas, FULL_LEVEL);
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
        ServerFluxHooks.placeFlux(level, pos, gas, metadata);
    }

    public static void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
        ServerFluxHooks.addFlux(level, pos, gas, amount);
    }
}
