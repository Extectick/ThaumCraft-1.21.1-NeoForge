package thaumcraft.common.items.curios;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.IRunicArmor;
import thaumcraft.common.curios.TCSlots;

public class VisAmuletItem extends ThaumcraftCurioItem implements IRunicArmor {
    private final int maxVis;

    public VisAmuletItem(int maxVis, Properties properties) {
        super(TCSlots.NECKLACE, properties);
        this.maxVis = maxVis;
    }

    public int getMaxVis(ItemStack stack) {
        return this.maxVis;
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return 0;
    }
}
