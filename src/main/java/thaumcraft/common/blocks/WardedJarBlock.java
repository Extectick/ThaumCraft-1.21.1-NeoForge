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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

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

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WardedJarBlockEntity jar)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(TCItems.GLASS_PHIAL.get())) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : fillPhialFromJar(stack, jar, player, hand);
        }

        if (stack.is(TCItems.ESSENTIA_PHIAL.get())) {
            return level.isClientSide ? ItemInteractionResult.SUCCESS : emptyPhialIntoJar(stack, jar, player, hand);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static ItemInteractionResult fillPhialFromJar(ItemStack glassPhial, WardedJarBlockEntity jar, Player player,
            InteractionHand hand) {
        EssentiaStorage stored = jar.getEssentia();
        if (stored.amount() < PHIAL_AMOUNT) {
            player.displayClientMessage(Component.translatable("block.thaumcraft.warded_jar.not_enough_essentia"), true);
            return ItemInteractionResult.CONSUME;
        }

        jar.drainEssentia(stored.aspect(), PHIAL_AMOUNT, false);
        ItemStack filled = new ItemStack(TCItems.ESSENTIA_PHIAL.get());
        filled.set(TCDataComponents.ESSENTIA, new EssentiaStorage(stored.aspect(), PHIAL_AMOUNT));
        consumeHeldAndGiveRemainder(player, hand, glassPhial, filled);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult emptyPhialIntoJar(ItemStack essentiaPhial, WardedJarBlockEntity jar,
            Player player, InteractionHand hand) {
        EssentiaStorage phialEssentia = essentiaPhial.getOrDefault(TCDataComponents.ESSENTIA, EssentiaStorage.EMPTY);
        if (phialEssentia.isEmpty()) {
            player.displayClientMessage(Component.translatable("item.thaumcraft.essentia_phial.empty"), true);
            return ItemInteractionResult.CONSUME;
        }

        int accepted = jar.fillEssentia(phialEssentia.aspect(), phialEssentia.amount(), true);
        if (accepted < phialEssentia.amount()) {
            player.displayClientMessage(Component.translatable("block.thaumcraft.warded_jar.cannot_accept"), true);
            return ItemInteractionResult.CONSUME;
        }

        jar.fillEssentia(phialEssentia.aspect(), phialEssentia.amount(), false);
        consumeHeldAndGiveRemainder(player, hand, essentiaPhial, new ItemStack(TCItems.GLASS_PHIAL.get()));
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
}
