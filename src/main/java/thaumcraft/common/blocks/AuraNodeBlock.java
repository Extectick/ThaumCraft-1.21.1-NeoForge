package thaumcraft.common.blocks;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.blockentities.NodeTransducerBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.world.AuraNodeGenerator;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.registry.TCDataComponents;

public class AuraNodeBlock extends Block implements EntityBlock {
    public static final MapCodec<AuraNodeBlock> CODEC = simpleCodec(AuraNodeBlock::new);
    private static final VoxelShape SELECTION_SHAPE = Block.box(4.8D, 4.8D, 4.8D, 11.2D, 11.2D, 11.2D);

    public AuraNodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AuraNodeBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AuraNodeBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        if (!level.isClientSide && stack.is(TCItems.AURA_NODE.get())) {
            AuraNodeGenerator.configureRandomNode(level, pos, level.random);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return blockEntityType == TCBlockEntities.AURA_NODE.get()
                ? (tickLevel, pos, tickState, blockEntity) -> AuraNodeBlockEntity.tick(tickLevel, pos, tickState,
                        (AuraNodeBlockEntity) blockEntity)
                : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SELECTION_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof AuraNodeBlockEntity node)
                || !node.isEnergized()) {
            return;
        }

        if (!hasActiveStabilizer(level, pos) || !(level.getBlockEntity(pos.above()) instanceof NodeTransducerBlockEntity)) {
            NodeTransducerBlockEntity.explodifyEnergizedNode(level, pos);
        }
    }

    private static boolean hasActiveStabilizer(Level level, BlockPos nodePos) {
        BlockPos stabilizerPos = nodePos.below();
        if (level.hasNeighborSignal(stabilizerPos)) {
            return false;
        }
        BlockState stabilizer = level.getBlockState(stabilizerPos);
        return stabilizer.is(TCBlocks.NODE_STABILIZER.get())
                || stabilizer.is(TCBlocks.ADVANCED_NODE_STABILIZER.get());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof AuraNodeBlockEntity node) {
                AspectList aspects = node.getAspects();
                for (Aspect aspect : aspects.getAspects()) {
                    int amount = aspects.getAmount(aspect);
                    if (amount >= 5) {
                        int dropCount = amount / 10;
                        for (int i = 0; i <= dropCount; i++) {
                            ItemStack essence = new ItemStack(TCItems.ETHEREAL_ESSENCE.get());
                            essence.set(TCDataComponents.ESSENTIA, new EssentiaStorage(aspect, 2));
                            Block.popResource(level, pos, essence);
                        }
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
