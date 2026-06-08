package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.util.ServerCrucibleHooks;

public class CrucibleBlock extends Block implements EntityBlock {
    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);
    private static final VoxelShape COLLISION_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 2.0D, 13.6D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.6D, 2.0D),
            Block.box(14.0D, 0.0D, 0.0D, 16.0D, 13.6D, 16.0D),
            Block.box(0.0D, 0.0D, 14.0D, 16.0D, 13.6D, 16.0D));

    public CrucibleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends CrucibleBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, TCBlockEntities.CRUCIBLE.get());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            return (int) Math.floor((double) crucible.tagAmount() / CrucibleBlockEntity.MAX_TAGS * 14.0D)
                    + (crucible.tagAmount() > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.WATER_BUCKET) || !(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (crucible.getWaterAmount() >= CrucibleBlockEntity.MAX_WATER) {
            return ItemInteractionResult.CONSUME;
        }

        if (!level.isClientSide) {
            crucible.addWater(CrucibleBlockEntity.MAX_WATER);
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            }
            level.playSound(null, pos, SoundEvents.GENERIC_SWIM, SoundSource.BLOCKS, 0.33F,
                    1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.3F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            ServerCrucibleHooks.crucibleEntityInside(level, pos, state, crucible, entity);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            ServerCrucibleHooks.spillCrucibleRemnants(level, pos, crucible);
        }
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) || !crucible.isBoiling()) {
            return;
        }

        if (random.nextInt(10) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + crucible.getFluidHeight(), pos.getZ() + 0.5D,
                    SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.1F + random.nextFloat() * 0.1F,
                    1.2F + random.nextFloat() * 0.2F, false);
        }
    }

    @Nullable
    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> actual,
            BlockEntityType<CrucibleBlockEntity> expected) {
        if (actual != expected) {
            return null;
        }
        return level.isClientSide
                ? (tickLevel, pos, state, blockEntity) -> CrucibleBlockEntity.clientTick(tickLevel, pos, state,
                        (CrucibleBlockEntity) blockEntity)
                : (tickLevel, pos, state, blockEntity) -> CrucibleBlockEntity.serverTick(tickLevel, pos, state,
                        (CrucibleBlockEntity) blockEntity);
    }
}
