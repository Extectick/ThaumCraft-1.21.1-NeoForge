package thaumcraft.common.items.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.curios.TCSlots;

public class VisDiscountCurioItem extends ThaumcraftCurioItem implements IVisDiscountGear {
    private final Aspect aspect;
    private final int discount;

    public VisDiscountCurioItem(Aspect aspect, int discount, Properties properties) {
        super(TCSlots.RING, properties);
        this.aspect = aspect;
        this.discount = discount;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return this.aspect == aspect ? this.discount : 0;
    }

    public Aspect getAspect() {
        return this.aspect;
    }
}
