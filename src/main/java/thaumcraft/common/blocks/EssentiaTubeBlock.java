package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class EssentiaTubeBlock extends SimpleTubeBlock implements EntityBlock {
    public static final MapCodec<EssentiaTubeBlock> CODEC = simpleCodec(EssentiaTubeBlock::new);
    private final TubeMode mode;

    public EssentiaTubeBlock(BlockBehaviour.Properties properties) {
        this(properties, TubeMode.NORMAL);
    }

    public EssentiaTubeBlock(BlockBehaviour.Properties properties, TubeMode mode) {
        super(properties);
        this.mode = mode;
    }

    @Override
    protected MapCodec<? extends EssentiaTubeBlock> codec() {
        return CODEC;
    }

    public TubeMode getMode() {
        return this.mode;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EssentiaTubeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTicker(blockEntityType, TCBlockEntities.ESSENTIA_TUBE.get());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EssentiaTubeBlockEntity tube)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (this.mode == TubeMode.VALVE && stack.isEmpty()) {
            if (!level.isClientSide) {
                tube.toggleEnabled();
                player.displayClientMessage(Component.translatable(tube.isEnabled()
                        ? "block.thaumcraft.essentia_valve.open"
                        : "block.thaumcraft.essentia_valve.closed"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (this.mode == TubeMode.FILTERED && stack.is(TCItems.ESSENTIA_PHIAL.get())) {
            EssentiaStorage phialEssentia = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
            if (!phialEssentia.isEmpty()) {
                if (!level.isClientSide) {
                    tube.setFilterAspect(phialEssentia.aspect());
                    player.displayClientMessage(Component.translatable("block.thaumcraft.filtered_essentia_tube.filter",
                            Component.translatable("tc.aspect." + phialEssentia.aspect().getTag())), true);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (this.mode == TubeMode.FILTERED && stack.isEmpty() && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                tube.clearFilterAspect();
                player.displayClientMessage(Component.translatable("block.thaumcraft.filtered_essentia_tube.clear"),
                        true);
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<T> actual,
            BlockEntityType<EssentiaTubeBlockEntity> expected) {
        return actual == expected ? (level, pos, state, blockEntity) -> EssentiaTubeBlockEntity.serverTick(level, pos,
                state, (EssentiaTubeBlockEntity) blockEntity) : null;
    }

    public enum TubeMode {
        NORMAL,
        VALVE,
        FILTERED,
        BUFFER,
        RESTRICTED,
        DIRECTIONAL
    }
}
