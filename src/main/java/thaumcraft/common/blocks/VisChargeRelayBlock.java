package thaumcraft.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.VisChargeRelayBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

public class VisChargeRelayBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = box(5.0D, 8.0D, 5.0D, 11.0D, 16.0D, 11.0D);

    public VisChargeRelayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, net.minecraft.world.phys.BlockHitResult hitResult) {
        return VisRelayBlock.tuneWithShard(stack, level, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VisChargeRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.VIS_CHARGE_RELAY.get()
                ? (tickLevel, pos, tickState, blockEntity) -> VisChargeRelayBlockEntity.tick(
                        tickLevel, pos, tickState, (VisChargeRelayBlockEntity) blockEntity)
                : null;
    }
}
