package thaumcraft.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.common.blockentities.NodeJarBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class NodeJarBlock extends SimpleJarBlock implements EntityBlock {
    public static final MapCodec<NodeJarBlock> CODEC = simpleCodec(NodeJarBlock::new);

    public NodeJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends NodeJarBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NodeJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide && blockEntityType == TCBlockEntities.NODE_IN_A_JAR.get()
                ? (tickLevel, pos, tickState, blockEntity) -> NodeJarBlockEntity.clientTick(tickLevel, pos, tickState,
                        (NodeJarBlockEntity) blockEntity)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof NodeJarBlockEntity jar) {
            NodeJarData data = stack.getOrDefault(TCDataComponents.NODE_JAR_DATA, NodeJarData.EMPTY);
            if (!data.isEmpty()) {
                jar.setNodeData(data);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof NodeJarBlockEntity jar && !jar.getNodeData().isEmpty()) {
            ItemStack drop = new ItemStack(TCItems.NODE_IN_A_JAR.get());
            drop.set(TCDataComponents.NODE_JAR_DATA, jar.getNodeData());
            return List.of(drop);
        }
        return List.of();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(TCItems.NODE_IN_A_JAR.get());
        if (level.getBlockEntity(pos) instanceof NodeJarBlockEntity jar && !jar.getNodeData().isEmpty()) {
            stack.set(TCDataComponents.NODE_JAR_DATA, jar.getNodeData());
        }
        return stack;
    }
}
