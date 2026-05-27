package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;

public class WardedJarBlock extends SimpleJarBlock implements EntityBlock {
    public static final MapCodec<WardedJarBlock> CODEC = simpleCodec(WardedJarBlock::new);
    public static final int PHIAL_AMOUNT = 8;

    public WardedJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends WardedJarBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WardedJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTicker(blockEntityType, TCBlockEntities.WARDED_JAR.get());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WardedJarBlockEntity jar)) {
            return;
        }

        if (placer != null) {
            jar.setFacing(placer.getDirection().getOpposite());
        }

        EssentiaStorage stored = stack.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        if (!stored.isEmpty()) {
            jar.setEssentia(stored);
        }

        Aspect filter = stack.get(TCDataComponents.JAR_FILTER);
        if (filter != null) {
            jar.setFilterAspect(filter);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof WardedJarBlockEntity jar) {
            ItemStack drop = createJarDrop(state, jar);
            return List.of(drop);
        }
        return super.getDrops(state, params);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WardedJarBlockEntity jar)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(TCItems.JAR_LABEL.get())) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : applyLabel(stack, jar, player, hand);
        }

        if (stack.is(TCItems.GLASS_PHIAL.get())) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : fillPhialFromJar(stack, jar, player, hand);
        }

        if (stack.is(TCItems.ESSENTIA_PHIAL.get())) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : emptyPhialIntoJar(stack, jar, player, hand);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WardedJarBlockEntity jar) || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (jar.getFilterAspect() != null && hitResult.getDirection() == jar.getFacing()) {
            if (!level.isClientSide) {
                jar.setFilterAspect(null);
                Direction side = jar.getFacing();
                Block.popResource(level, pos.relative(side), new ItemStack(TCItems.JAR_LABEL.get()));
                level.playSound(null, pos, TCSoundEvents.PAGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            jar.clearEssentia();
            level.playSound(null, pos, TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 0.4F, 1.0F);
            level.playSound(null, pos, TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.3F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static ItemInteractionResult applyLabel(ItemStack label, WardedJarBlockEntity jar, Player player,
            InteractionHand hand) {
        if (jar.getFilterAspect() != null) {
            return ItemInteractionResult.CONSUME;
        }

        EssentiaStorage stored = jar.getEssentia();
        EssentiaStorage labelEssentia = label.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        Aspect filter = stored.isEmpty() ? (labelEssentia.isEmpty() ? null : labelEssentia.aspect()) : stored.aspect();
        if (filter == null) {
            return ItemInteractionResult.CONSUME;
        }

        jar.setFacing(player.getDirection().getOpposite());
        jar.setFilterAspect(filter);
        if (!player.getAbilities().instabuild) {
            label.shrink(1);
            if (label.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        player.level().playSound(null, jar.getBlockPos(), TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 0.4F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemStack createJarDrop(BlockState state, WardedJarBlockEntity jar) {
        ItemStack drop = new ItemStack(state.getBlock());
        EssentiaStorage stored = jar.getEssentia();
        if (!stored.isEmpty()) {
            drop.set(TCDataComponents.ESSENTIA, stored);
        }
        Aspect filter = jar.getFilterAspect();
        if (filter != null) {
            drop.set(TCDataComponents.JAR_FILTER, filter);
        }
        return drop;
    }

    private static ItemInteractionResult fillPhialFromJar(ItemStack glassPhial, WardedJarBlockEntity jar, Player player,
            InteractionHand hand) {
        EssentiaStorage stored = jar.getEssentia();
        if (stored.amount() < PHIAL_AMOUNT) {
            player.displayClientMessage(Component.translatable("block.thaumcraft.warded_jar.not_enough_essentia"), true);
            player.level().playSound(null, player.blockPosition(), TCSoundEvents.WANDFAIL.get(), SoundSource.BLOCKS, 0.25F, 1.2F);
            return ItemInteractionResult.CONSUME;
        }

        jar.drainEssentia(stored.aspect(), PHIAL_AMOUNT, false);
        ItemStack filled = new ItemStack(TCItems.ESSENTIA_PHIAL.get());
        filled.set(TCDataComponents.ESSENTIA, new EssentiaStorage(stored.aspect(), PHIAL_AMOUNT));
        consumeHeldAndGiveRemainder(player, hand, glassPhial, filled);
        player.level().playSound(null, player.blockPosition(), TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 0.45F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult emptyPhialIntoJar(ItemStack essentiaPhial, WardedJarBlockEntity jar,
            Player player, InteractionHand hand) {
        EssentiaStorage phialEssentia = essentiaPhial.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        if (phialEssentia.isEmpty()) {
            player.displayClientMessage(Component.translatable("item.thaumcraft.essentia_phial.empty"), true);
            player.level().playSound(null, player.blockPosition(), TCSoundEvents.WANDFAIL.get(), SoundSource.BLOCKS, 0.25F, 1.2F);
            return ItemInteractionResult.CONSUME;
        }

        int accepted = jar.fillEssentia(phialEssentia.aspect(), phialEssentia.amount(), true);
        if (accepted < phialEssentia.amount()) {
            player.displayClientMessage(Component.translatable("block.thaumcraft.warded_jar.cannot_accept"), true);
            player.level().playSound(null, player.blockPosition(), TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
            return ItemInteractionResult.CONSUME;
        }

        jar.fillEssentia(phialEssentia.aspect(), phialEssentia.amount(), false);
        consumeHeldAndGiveRemainder(player, hand, essentiaPhial, new ItemStack(TCItems.GLASS_PHIAL.get()));
        player.level().playSound(null, player.blockPosition(), TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 0.45F, 0.9F);
        return ItemInteractionResult.CONSUME;
    }

    private static void consumeHeldAndGiveRemainder(Player player, InteractionHand hand, ItemStack held,
            ItemStack remainder) {
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        if (held.isEmpty()) {
            player.setItemInHand(hand, remainder);
        } else if (!player.getInventory().add(remainder)) {
            player.drop(remainder, false);
        }
    }

    @Nullable
    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<T> actual,
            BlockEntityType<WardedJarBlockEntity> expected) {
        return actual == expected ? (level, pos, state, blockEntity) -> WardedJarBlockEntity.serverTick(level, pos,
                state, (WardedJarBlockEntity) blockEntity) : null;
    }
}
