package thaumcraft.server.flux;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public final class ServerFluxService {
    private static final int QUANTA_PER_BLOCK = 8;

    private ServerFluxService() {
    }

    public static void tickFlux(FluxBlock block, BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        if (!level.getBlockState(pos).is(block)) {
            return;
        }

        boolean flowed = flowFinite(block, level, pos, random);
        BlockState current = level.getBlockState(pos);
        if (current.is(block) && !block.isGas()) {
            oldGooUpdate(level, pos, current, random);
        }

        if (level.getBlockState(pos).is(block)) {
            level.scheduleTick(pos, block, flowed ? FluxBlock.FLOW_TICK_RATE : 30 + random.nextInt(20));
        }
    }

    public static void entityInside(FluxBlock block, BlockState state, Level level, BlockPos pos, Entity entity) {
        int fluxLevel = state.getValue(FluxBlock.LEVEL);
        if (block.isGas()) {
            if (level.random.nextInt(10) == 0 && entity instanceof LivingEntity living
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

        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, fluxLevel / 3, true, true));
        }
    }

    public static void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
        level.setBlock(pos, FluxBlock.fluxState(gas, metadata), 3);
        SoundEvent sound = gas ? SoundEvents.FIRE_EXTINGUISH : SoundEvents.GENERIC_SWIM;
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.3F, 1.0F);
    }

    public static void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
        Block fluxBlock = gas ? TCBlocks.FLUX_GAS.get() : TCBlocks.FLUX_GOO.get();
        BlockState state = level.getBlockState(pos);
        if (state.is(fluxBlock)) {
            int metadata = Math.min(FluxBlock.FULL_LEVEL, state.getValue(FluxBlock.LEVEL) + amount);
            level.setBlock(pos, state.setValue(FluxBlock.LEVEL, metadata), 3);
        } else if (state.isAir()) {
            placeFlux(level, pos, gas, Math.min(FluxBlock.FULL_LEVEL, amount - 1));
        }
    }

    private static void oldGooUpdate(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int meta = state.getValue(FluxBlock.LEVEL);
        if (meta >= 2 && meta < 6 && level.isEmptyBlock(pos.above()) && random.nextInt(25) == 0) {
            level.removeBlock(pos, false);
            level.playSound(null, pos, TCSoundEvents.GORE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (meta >= 6 && level.isEmptyBlock(pos.above())) {
            if (random.nextInt(25) == 0) {
                level.removeBlock(pos, false);
                level.playSound(null, pos, TCSoundEvents.GORE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (random.nextInt(30) == 0) {
            reduceOrRemove(level, pos, state);
            if (random.nextBoolean() && level.isEmptyBlock(pos.above())) {
                setFlux(level, pos.above(), true, 0);
            }
        }
    }

    private static boolean flowFinite(FluxBlock block, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos);
        int quantaRemaining = FluxBlock.getQuantaValue(state);
        int previous = quantaRemaining;
        quantaRemaining = tryFlowVertically(block, level, pos, quantaRemaining);
        if (quantaRemaining < 1 || !level.getBlockState(pos).is(block)) {
            return true;
        }

        boolean changed = quantaRemaining != previous;
        if (quantaRemaining == 1) {
            if (changed) {
                setFlux(level, pos, block.isGas(), quantaRemaining - 1);
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
            clearDisplaceable(block, level, neighbour);
            neighbourQuanta[i] = getQuantaValueBelow(block, level, neighbour, lowerThan);
            if (neighbourQuanta[i] >= 0) {
                count++;
                total += neighbourQuanta[i];
            }
        }

        if (count == 1) {
            if (changed) {
                setFlux(level, pos, block.isGas(), quantaRemaining - 1);
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
                    setFlux(level, neighbour, block.isGas(), newValue - 1);
                    level.scheduleTick(neighbour, block, FluxBlock.FLOW_TICK_RATE);
                }
                flowed = true;
            }
            count--;
        }

        if (remainder > 0) {
            each++;
        }
        setFlux(level, pos, block.isGas(), each - 1);
        return flowed || each != quantaRemaining;
    }

    private static int tryFlowVertically(FluxBlock block, ServerLevel level, BlockPos pos, int amountToInput) {
        Direction vertical = block.isGas() ? Direction.UP : Direction.DOWN;
        BlockPos otherPos = pos.relative(vertical);
        if (otherPos.getY() < level.getMinBuildHeight() || otherPos.getY() >= level.getMaxBuildHeight()) {
            level.removeBlock(pos, false);
            return 0;
        }

        int otherAmount = getQuantaValueBelow(block, level, otherPos, QUANTA_PER_BLOCK);
        if (otherAmount >= 0) {
            int total = otherAmount + amountToInput;
            if (total > QUANTA_PER_BLOCK) {
                setFlux(level, otherPos, block.isGas(), FluxBlock.FULL_LEVEL);
                level.scheduleTick(otherPos, block, FluxBlock.FLOW_TICK_RATE);
                return total - QUANTA_PER_BLOCK;
            }
            if (total > 0) {
                setFlux(level, otherPos, block.isGas(), total - 1);
                level.scheduleTick(otherPos, block, FluxBlock.FLOW_TICK_RATE);
                level.removeBlock(pos, false);
                return 0;
            }
            return amountToInput;
        }

        if (canDisplace(block, level, otherPos)) {
            setFlux(level, otherPos, block.isGas(), amountToInput - 1);
            level.scheduleTick(otherPos, block, FluxBlock.FLOW_TICK_RATE);
            level.removeBlock(pos, false);
            return 0;
        }
        return amountToInput;
    }

    private static void clearDisplaceable(FluxBlock block, ServerLevel level, BlockPos pos) {
        if (canDisplace(block, level, pos)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static boolean canDisplace(FluxBlock block, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }
        if (state.is(block)) {
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

    private static int getQuantaValueBelow(FluxBlock block, ServerLevel level, BlockPos pos, int belowThis) {
        int value = getQuantaValue(block, level, pos);
        return value >= belowThis ? -1 : value;
    }

    private static int getQuantaValue(FluxBlock block, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return 0;
        }
        if (!state.is(block)) {
            return -1;
        }
        return FluxBlock.getQuantaValue(state);
    }

    private static void setFlux(ServerLevel level, BlockPos pos, boolean gas, int meta) {
        level.setBlock(pos, FluxBlock.fluxState(gas, meta), 3);
    }

    private static void reduceOrRemove(Level level, BlockPos pos, BlockState state) {
        int fluxLevel = state.getValue(FluxBlock.LEVEL);
        if (fluxLevel <= 0) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(FluxBlock.LEVEL, fluxLevel - 1), 3);
        }
    }
}
