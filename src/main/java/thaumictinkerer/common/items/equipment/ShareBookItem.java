package thaumictinkerer.common.items.equipment;


import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumictinkerer.common.registry.TTDataComponents;

import java.util.List;
import java.util.UUID;

public class ShareBookItem extends Item {
    public ShareBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        UUID owner = stack.get(TTDataComponents.OWNER_UUID);

        if (player.isShiftKeyDown()) {
            if (owner != null) {
                stack.remove(TTDataComponents.OWNER_UUID);
                stack.remove(TTDataComponents.OWNER_NAME);
                level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("tt.message.book_cleared"), true);
                }
                return InteractionResultHolder.success(stack);
            }
        } else {
            if (owner == null) {
                stack.set(TTDataComponents.OWNER_UUID, player.getUUID());
                stack.set(TTDataComponents.OWNER_NAME, player.getName().getString());
                level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("tt.message.book_bound"), true);
                }
                return InteractionResultHolder.success(stack);
            } else if (!owner.equals(player.getUUID())) {
                // TODO: Implement Thaumcraft API integration for research sharing.
                // Currently, the Thaumonomicon API for NeoForge 1.21.1 is not fully public/available.
                // When the API is ready, we need to:
                // 1. Fetch all completed research from the owner UUID.
                // 2. Sync and grant those research entries to the current player.
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("tt.message.book_shared"), true);
                }
                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String ownerName = stack.get(TTDataComponents.OWNER_NAME);
        if (ownerName != null) {
            tooltipComponents.add(Component.translatable("tt.tooltip.owner").append(": ").append(ownerName));
        } else {
            tooltipComponents.add(Component.translatable("tt.tooltip.unbound"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(TTDataComponents.OWNER_UUID) || super.isFoil(stack);
    }
}

