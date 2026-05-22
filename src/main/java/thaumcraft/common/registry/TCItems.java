package thaumcraft.common.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.Thaumcraft;
import thaumcraft.common.curios.TCSlots;
import thaumcraft.common.items.curios.FocusPouchCurioItem;
import thaumcraft.common.items.curios.HoverGirdleItem;
import thaumcraft.common.items.curios.RunicCurioItem;
import thaumcraft.common.items.curios.ThaumcraftCurioItem;
import thaumcraft.common.items.curios.VisAmuletItem;
import thaumcraft.common.items.curios.VisDiscountCurioItem;

public final class TCItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final DeferredItem<Item> THAUMONOMICON = REGISTRY.registerSimpleItem("thaumonomicon", new Item.Properties());
    public static final DeferredItem<Item> RUNIC_RING_LESSER = REGISTRY.registerItem("runic_ring_lesser",
            properties -> new RunicCurioItem(TCSlots.RING, 1, properties));
    public static final DeferredItem<Item> RUNIC_RING = REGISTRY.registerItem("runic_ring",
            properties -> new RunicCurioItem(TCSlots.RING, 5, properties));
    public static final DeferredItem<Item> RUNIC_RING_CHARGED = REGISTRY.registerItem("runic_ring_charged",
            properties -> new RunicCurioItem(TCSlots.RING, 4, properties));
    public static final DeferredItem<Item> RUNIC_RING_REGEN = REGISTRY.registerItem("runic_ring_regen",
            properties -> new RunicCurioItem(TCSlots.RING, 4, properties));
    public static final DeferredItem<Item> RUNIC_AMULET = REGISTRY.registerItem("runic_amulet",
            properties -> new RunicCurioItem(TCSlots.NECKLACE, 8, properties));
    public static final DeferredItem<Item> RUNIC_AMULET_EMERGENCY = REGISTRY.registerItem("runic_amulet_emergency",
            properties -> new RunicCurioItem(TCSlots.NECKLACE, 7, properties));
    public static final DeferredItem<Item> RUNIC_GIRDLE = REGISTRY.registerItem("runic_girdle",
            properties -> new RunicCurioItem(TCSlots.BELT, 10, properties));
    public static final DeferredItem<Item> RUNIC_GIRDLE_KINETIC = REGISTRY.registerItem("runic_girdle_kinetic",
            properties -> new RunicCurioItem(TCSlots.BELT, 9, properties));
    public static final DeferredItem<Item> VIS_AMULET_LESSER = REGISTRY.registerItem("vis_amulet_lesser",
            properties -> new VisAmuletItem(2500, properties));
    public static final DeferredItem<Item> VIS_AMULET = REGISTRY.registerItem("vis_amulet",
            properties -> new VisAmuletItem(25000, properties));
    public static final DeferredItem<Item> HOVER_GIRDLE = REGISTRY.registerItem("hover_girdle", HoverGirdleItem::new);
    public static final DeferredItem<Item> BAUBLE_AMULET = REGISTRY.registerItem("bauble_amulet",
            properties -> new ThaumcraftCurioItem(TCSlots.NECKLACE, properties));
    public static final DeferredItem<Item> BAUBLE_RING = REGISTRY.registerItem("bauble_ring",
            properties -> new ThaumcraftCurioItem(TCSlots.RING, properties));
    public static final DeferredItem<Item> BAUBLE_BELT = REGISTRY.registerItem("bauble_belt",
            properties -> new ThaumcraftCurioItem(TCSlots.BELT, properties));
    public static final DeferredItem<Item> BAUBLE_RING_IRON = REGISTRY.registerItem("bauble_ring_iron",
            properties -> new ThaumcraftCurioItem(TCSlots.RING, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_AIR = REGISTRY.registerItem("vis_discount_ring_air",
            properties -> new VisDiscountCurioItem(Aspect.AIR, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_FIRE = REGISTRY.registerItem("vis_discount_ring_fire",
            properties -> new VisDiscountCurioItem(Aspect.FIRE, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_WATER = REGISTRY.registerItem("vis_discount_ring_water",
            properties -> new VisDiscountCurioItem(Aspect.WATER, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_EARTH = REGISTRY.registerItem("vis_discount_ring_earth",
            properties -> new VisDiscountCurioItem(Aspect.EARTH, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_ORDER = REGISTRY.registerItem("vis_discount_ring_order",
            properties -> new VisDiscountCurioItem(Aspect.ORDER, 1, properties));
    public static final DeferredItem<Item> VIS_DISCOUNT_RING_ENTROPY = REGISTRY.registerItem("vis_discount_ring_entropy",
            properties -> new VisDiscountCurioItem(Aspect.ENTROPY, 1, properties));
    public static final DeferredItem<Item> FOCUS_POUCH = REGISTRY.registerItem("focus_pouch", FocusPouchCurioItem::new);

    private TCItems() {
    }
}
