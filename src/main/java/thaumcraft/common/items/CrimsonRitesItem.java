package thaumcraft.common.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchManager;

import javax.annotation.ParametersAreNonnullByDefault;

public class CrimsonRitesItem extends Item {

    public CrimsonRitesItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!ResearchManager.isComplete(serverPlayer, "CRIMSON")) {
                ResearchManager.completeResearch(serverPlayer, "CRIMSON");
                level.playSound(null, player.blockPosition(), TCSoundEvents.LEARN.get(), SoundSource.PLAYERS, 0.75f, 1.0f);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
