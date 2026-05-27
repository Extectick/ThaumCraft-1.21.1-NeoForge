package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.blockentities.ArcaneAlembicBlockEntity;
import thaumcraft.common.registry.TCSoundEvents;

public class ArcaneAlembicBlock extends Block implements EntityBlock {
    public static final MapCodec<ArcaneAlembicBlock> CODEC = simpleCodec(ArcaneAlembicBlock::new);

    public ArcaneAlembicBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ArcaneAlembicBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneAlembicBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ArcaneAlembicBlockEntity alembic) {
            Direction facing = placer.getDirection().getOpposite();
            if (!facing.getAxis().isVertical()) {
                alembic.setFacing(facing);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ArcaneAlembicBlockEntity alembic)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            EssentiaStorage stored = alembic.getEssentia();
            Component message = stored.isEmpty()
                    ? Component.literal("Alembic empty")
                    : Component.literal(stored.aspect().getTag() + " " + stored.amount() + "/"
                            + alembic.getEssentiaCapacity());
            player.displayClientMessage(message, true);
            level.playSound(null, pos, TCSoundEvents.ALEMBICKNOCK.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
