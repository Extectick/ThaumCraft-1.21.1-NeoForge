package thaumictinkerer.common.items.equipment;


import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumictinkerer.common.menus.IchorPouchMenu;

public class IchorPouchItem extends Item {
    public IchorPouchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            int slot = usedHand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1;
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p) {
                    return new IchorPouchMenu(id, inventory, stack, slot);
                }
            }, buf -> {
                ItemStack.STREAM_CODEC.encode(buf, stack);
                buf.writeInt(slot);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

