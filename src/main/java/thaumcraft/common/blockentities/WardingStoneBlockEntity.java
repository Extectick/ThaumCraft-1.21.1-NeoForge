package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

import java.util.List;

public class WardingStoneBlockEntity extends BlockEntity {

    private int tickCounter = 0;

    public WardingStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.WARDING_STONE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WardingStoneBlockEntity be) {
        be.tickCounter++;

        if (be.tickCounter % 5 == 0) {
            pushMobs(level, pos);
        }

        if (be.tickCounter % 100 == 0) {
            placeBarriers(level, pos);
        }
    }

    private static void pushMobs(Level level, BlockPos pos) {
        if (level.hasNeighborSignal(pos)) {
            return;
        }

        AABB searchBox = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 3, pos.getZ() + 1).inflate(0.1, 0.1, 0.1);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> !(e instanceof Player));

        for (LivingEntity entity : targets) {
            if (!entity.onGround()) {
                float yawRad = (entity.getYRot() + 180.0F) * ((float) Math.PI / 180.0F);
                entity.push(-Mth.sin(yawRad) * 0.2, -0.1, Mth.cos(yawRad) * 0.2);
                entity.hurtMarked = true;
            }
        }
    }

    private static void placeBarriers(Level level, BlockPos pos) {
        Block barrier = TCBlocks.WARDING_BARRIER.get();
        for (int dy = 1; dy <= 2; dy++) {
            BlockPos above = pos.above(dy);
            BlockState current = level.getBlockState(above);
            if (current.isAir() || current.canBeReplaced()) {
                level.setBlock(above, barrier.defaultBlockState(), 3);
            }
        }
    }

    public static void onRemove(Level level, BlockPos pos) {

        Block barrier = TCBlocks.WARDING_BARRIER.get();
        for (int dy = 1; dy <= 2; dy++) {
            BlockPos above = pos.above(dy);
            if (level.getBlockState(above).is(barrier)) {
                level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}



