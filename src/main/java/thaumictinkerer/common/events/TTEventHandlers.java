package thaumictinkerer.common.events;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.items.equipment.BloodSwordItem;
import thaumictinkerer.common.registry.TTDataComponents;
import thaumictinkerer.common.registry.TTItems;

import java.util.List;

@EventBusSubscriber(modid = ThaumicTinkerer.MODID)
public class TTEventHandlers {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof BloodSwordItem) {
                boolean active = weapon.getOrDefault(TTDataComponents.TALISMAN_ACTIVE, false);
                if (active) {
                    event.getDrops().clear();
                    LivingEntity entity = event.getEntity();
                    List<Item> aspects = List.of(
                            TTItems.AER_MOB_ASPECT.get(), TTItems.TERRA_MOB_ASPECT.get(),
                            TTItems.IGNIS_MOB_ASPECT.get(), TTItems.AQUA_MOB_ASPECT.get(),
                            TTItems.BESTIA_MOB_ASPECT.get(), TTItems.ALIENIS_MOB_ASPECT.get(),
                            TTItems.PRAECANTATIO_MOB_ASPECT.get()
                    );
                    
                    Item randomAspect = aspects.get(entity.level().random.nextInt(aspects.size()));
                    
                    ItemEntity itemEntity = new ItemEntity(
                            entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                            new ItemStack(randomAspect)
                    );
                    event.getDrops().add(itemEntity);
                }
            }
        }
    }
}





