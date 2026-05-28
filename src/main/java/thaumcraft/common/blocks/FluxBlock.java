package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public class FluxBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 7);
    public static final int FULL_LEVEL = 7;
    private static final int QUANTA_PER_BLOCK = 8;
    private static final int FLOW_TICK_RATE = 20;
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
            if (!level.isClientSide && level.random.nextInt(10) == 0 && entity instanceof LivingEntity living
                    && !living.hasEffect(MobEffects.DIG_SLOWDOWN) && !living.hasEffect(MobEffects.CONFUSION)) {
                if (level.random.nextBoolean()) {
                    living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, fluxLevel / 3, true, true));
                } else {
                    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80 + fluxLevel * 20, 0, false,
                            true));
                }
                reduceOrRemove(level, pos, state);
            }
            return;
        }

        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D - (fluxLevel / 8.0D), 1.0D,
                1.0D - (fluxLevel / 8.0D)));
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, fluxLevel / 3, true, true));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        boolean flowed = flowFinite(level, pos, random);
        BlockState current = level.getBlockState(pos);
        if (current.is(this) && !this.gas) {
            oldGooUpdate(level, pos, current, random);
        }

        if (level.getBlockState(pos).is(this)) {
            level.scheduleTick(pos, this, flowed ? FLOW_TICK_RATE : 30 + random.nextInt(20));
        }
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

    private void oldGooUpdate(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int meta = state.getValue(LEVEL);
        if (meta >= 2 && meta < 6 && level.isEmptyBlock(pos.above()) && random.nextInt(25) == 0) {
            // EntityThaumicSlime is not ported yet; keep old volume consumption and gore sound hook.
            level.removeBlock(pos, false);
            level.playSound(null, pos, TCSoundEvents.GORE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (meta >= 6 && level.isEmptyBlock(pos.above())) {
            if (random.nextInt(25) == 0) {
                level.removeBlock(pos, false);
                level.playSound(null, pos, TCSoundEvents.GORE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            // Original may convert biome to taint here; taint biome/fibre blocks are not ported yet.
        } else if (random.nextInt(30) == 0) {
            reduceOrRemove(level, pos, state);
            if (random.nextBoolean() && level.isEmptyBlock(pos.above())) {
                setFlux(level, pos.above(), true, 0);
            }
        }
    }

    private boolean flowFinite(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos);
        int quantaRemaining = getQuantaValue(state);
        int previous = quantaRemaining;
        quantaRemaining = tryFlowVertically(level, pos, quantaRemaining);
        if (quantaRemaining < 1 || !level.getBlockState(pos).is(this)) {
            return true;
        }

        boolean changed = quantaRemaining != previous;
        if (quantaRemaining == 1) {
            if (changed) {
                setFlux(level, pos, this.gas, quantaRemaining - 1);
            }
            return changed;
        }

        int lowerThan = quantaRemaining - 1;
        Direction[] directions = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
        int[] neighbourQuanta = new int[directions.length];
        int total = quantaRemaining;
        int count = 1;
        for (int i = 0; i < directions.length; i++) {
            BlockPos neighbour = pos.relative(directions[i]);
            clearDisplaceable(level, neighbour);
            neighbourQuanta[i] = getQuantaValueBelow(level, neighbour, lowerThan);
            if (neighbourQuanta[i] >= 0) {
                count++;
                total += neighbourQuanta[i];
            }
        }

        if (count == 1) {
            if (changed) {
                setFlux(level, pos, this.gas, quantaRemaining - 1);
            }
            return changed;
        }

        int each = total / count;
        int remainder = total % count;
        boolean flowed = changed;
        for (int i = 0; i < directions.length; i++) {
            if (neighbourQuanta[i] < 0) {
                continue;
            }
            int newValue = each;
            if (remainder == count || remainder > 1 && random.nextInt(count - remainder) != 0) {
                newValue++;
                remainder--;
            }

            BlockPos neighbour = pos.relative(directions[i]);
            if (newValue != neighbourQuanta[i]) {
                if (newValue <= 0) {
                    level.removeBlock(neighbour, false);
                } else {
                    setFlux(level, neighbour, this.gas, newValue - 1);
                    level.scheduleTick(neighbour, this, FLOW_TICK_RATE);
                }
                flowed = true;
            }
            count--;
        }

        if (remainder > 0) {
            each++;
        }
        setFlux(level, pos, this.gas, each - 1);
        return flowed || each != quantaRemaining;
    }

    private int tryFlowVertically(ServerLevel level, BlockPos pos, int amountToInput) {
        Direction vertical = this.gas ? Direction.UP : Direction.DOWN;
        BlockPos otherPos = pos.relative(vertical);
        if (otherPos.getY() < level.getMinBuildHeight() || otherPos.getY() >= level.getMaxBuildHeight()) {
            level.removeBlock(pos, false);
            return 0;
        }

        int otherAmount = getQuantaValueBelow(level, otherPos, QUANTA_PER_BLOCK);
        if (otherAmount >= 0) {
            int total = otherAmount + amountToInput;
            if (total > QUANTA_PER_BLOCK) {
                setFlux(level, otherPos, this.gas, FULL_LEVEL);
                level.scheduleTick(otherPos, this, FLOW_TICK_RATE);
                return total - QUANTA_PER_BLOCK;
            }
            if (total > 0) {
                setFlux(level, otherPos, this.gas, total - 1);
                level.scheduleTick(otherPos, this, FLOW_TICK_RATE);
                level.removeBlock(pos, false);
                return 0;
            }
            return amountToInput;
        }

        if (canDisplace(level, otherPos)) {
            setFlux(level, otherPos, this.gas, amountToInput - 1);
            level.scheduleTick(otherPos, this, FLOW_TICK_RATE);
            level.removeBlock(pos, false);
            return 0;
        }
        return amountToInput;
    }

    private void clearDisplaceable(ServerLevel level, BlockPos pos) {
        if (canDisplace(level, pos)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private boolean canDisplace(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }
        if (state.is(this)) {
            return false;
        }
        if (state.getPistonPushReaction() == PushReaction.BLOCK) {
            return false;
        }
        if (state.blocksMotion() || state.liquid()) {
            return false;
        }
        MapColor color = state.getMapColor(level, pos);
        return color != MapColor.NONE;
    }

    private int getQuantaValueBelow(ServerLevel level, BlockPos pos, int belowThis) {
        int value = getQuantaValue(level, pos);
        return value >= belowThis ? -1 : value;
    }

    private int getQuantaValue(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return 0;
        }
        if (!state.is(this)) {
            return -1;
        }
        return getQuantaValue(state);
    }

    private static int getQuantaValue(BlockState state) {
        return state.getValue(LEVEL) + 1;
    }

    private static void setFlux(ServerLevel level, BlockPos pos, boolean gas, int meta) {
        level.setBlock(pos, (gas ? TCBlocks.FLUX_GAS.get() : TCBlocks.FLUX_GOO.get()).defaultBlockState()
                .setValue(LEVEL, Math.clamp(meta, 0, FULL_LEVEL)), 3);
    }

    private static void reduceOrRemove(Level level, BlockPos pos, BlockState state) {
        int fluxLevel = state.getValue(LEVEL);
        if (fluxLevel <= 0) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(LEVEL, fluxLevel - 1), 3);
        }
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas) {
        placeFlux(level, pos, gas, FULL_LEVEL);
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
        if (level.isClientSide) {
            return;
        }
        level.setBlock(pos, (gas ? TCBlocks.FLUX_GAS.get() : TCBlocks.FLUX_GOO.get()).defaultBlockState()
                .setValue(LEVEL, Math.clamp(metadata, 0, FULL_LEVEL)), 3);
        SoundEvent sound = gas ? SoundEvents.FIRE_EXTINGUISH : SoundEvents.GENERIC_SWIM;
        if (gas) {
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.3F, 1.0F);
            return;
        }
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.3F, 1.0F);
    }

    public static void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
        if (level.isClientSide || amount <= 0) {
            return;
        }
        Block fluxBlock = gas ? TCBlocks.FLUX_GAS.get() : TCBlocks.FLUX_GOO.get();
        BlockState state = level.getBlockState(pos);
        if (state.is(fluxBlock)) {
            int metadata = Math.min(FULL_LEVEL, state.getValue(LEVEL) + amount);
            level.setBlock(pos, state.setValue(LEVEL, metadata), 3);
        } else if (state.isAir()) {
            placeFlux(level, pos, gas, Math.min(FULL_LEVEL, amount - 1));
        }
    }
}
