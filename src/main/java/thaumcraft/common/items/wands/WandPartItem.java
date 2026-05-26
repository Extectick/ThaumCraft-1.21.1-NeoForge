package thaumcraft.common.items.wands;

import net.minecraft.world.item.Item;

public class WandPartItem extends Item {
    private final Kind kind;
    private final String tag;

    public WandPartItem(Properties properties, Kind kind, String tag) {
        super(properties);
        this.kind = kind;
        this.tag = tag;
    }

    public Kind getKind() {
        return this.kind;
    }

    public String getTag() {
        return this.tag;
    }

    public enum Kind {
        CAP,
        ROD
    }
}
