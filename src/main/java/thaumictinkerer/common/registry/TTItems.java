package thaumictinkerer.common.registry;


import static thaumictinkerer.common.registry.TTBlocks.*;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import static net.minecraft.world.item.Tiers.DIAMOND;
import thaumictinkerer.common.items.equipment.BlackHoleTalismanItem;
import thaumictinkerer.common.items.equipment.BloodSwordItem;
import thaumictinkerer.common.items.equipment.CleansingTalismanItem;
import thaumictinkerer.common.items.equipment.IchorPouchItem;
import thaumictinkerer.common.items.equipment.ShareBookItem;
import thaumictinkerer.common.items.equipment.SpellClothItem;
import thaumictinkerer.common.items.equipment.XpTalismanItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.items.equipment.AwakenedIchorclothArmorItem;
import thaumictinkerer.common.items.equipment.AwakenedIchorAxeItem;
import thaumictinkerer.common.items.equipment.AwakenedIchorPickaxeItem;
import thaumictinkerer.common.items.equipment.AwakenedIchorShovelItem;
import thaumictinkerer.common.items.equipment.AwakenedIchorSwordItem;
import thaumictinkerer.common.items.equipment.IchorclothArmorItem;
import thaumictinkerer.common.items.equipment.IchorAxeItem;
import thaumictinkerer.common.items.equipment.IchorPickaxeItem;
import thaumictinkerer.common.items.equipment.IchorShovelItem;
import thaumictinkerer.common.items.equipment.IchorSwordItem;
import thaumictinkerer.common.items.equipment.TTArmorMaterials;

public class TTItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ThaumicTinkerer.MODID);

    public static final DeferredItem<BlockItem> DARK_QUARTZ_BLOCK = REGISTRY.registerSimpleBlockItem(TTBlocks.DARK_QUARTZ_BLOCK);

    public static final DeferredItem<AwakenedIchorAxeItem> ADVANCED_ICHOR_AXE = REGISTRY.registerItem("advanced_ichor_axe", AwakenedIchorAxeItem::new);
    public static final DeferredItem<AwakenedIchorclothArmorItem> ADVANCED_ICHOR_BOOTS = REGISTRY.registerItem("advanced_ichor_boots", properties -> new AwakenedIchorclothArmorItem(TTArmorMaterials.ADVANCED_ICHOR, ArmorItem.Type.BOOTS, properties));
    public static final DeferredItem<AwakenedIchorclothArmorItem> ADVANCED_ICHOR_CHESTPLATE = REGISTRY.registerItem("advanced_ichor_chestplate", properties -> new AwakenedIchorclothArmorItem(TTArmorMaterials.ADVANCED_ICHOR, ArmorItem.Type.CHESTPLATE, properties));
    public static final DeferredItem<AwakenedIchorclothArmorItem> ADVANCED_ICHOR_HELMET = REGISTRY.registerItem("advanced_ichor_helmet", properties -> new AwakenedIchorclothArmorItem(TTArmorMaterials.ADVANCED_ICHOR, ArmorItem.Type.HELMET, properties));
    public static final DeferredItem<AwakenedIchorclothArmorItem> ADVANCED_ICHOR_LEGGINGS = REGISTRY.registerItem("advanced_ichor_leggings", properties -> new AwakenedIchorclothArmorItem(TTArmorMaterials.ADVANCED_ICHOR, ArmorItem.Type.LEGGINGS, properties));
    public static final DeferredItem<AwakenedIchorPickaxeItem> ADVANCED_ICHOR_PICKAXE = REGISTRY.registerItem("advanced_ichor_pickaxe", AwakenedIchorPickaxeItem::new);
    public static final DeferredItem<AwakenedIchorShovelItem> ADVANCED_ICHOR_SHOVEL = REGISTRY.registerItem("advanced_ichor_shovel", AwakenedIchorShovelItem::new);
    public static final DeferredItem<AwakenedIchorSwordItem> ADVANCED_ICHOR_SWORD = REGISTRY.registerItem("advanced_ichor_sword", AwakenedIchorSwordItem::new);
    
    public static final DeferredItem<Item> AER_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("aer_condensed_mob_aspect");
    public static final DeferredItem<Item> AER_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("aer_infused_mob_aspect");
    public static final DeferredItem<Item> AER_MOB_ASPECT = REGISTRY.registerSimpleItem("aer_mob_aspect");
    public static final DeferredItem<Item> AIR_FRUIT = REGISTRY.registerSimpleItem("air_fruit");
    public static final DeferredItem<Item> ALIENIS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("alienis_condensed_mob_aspect");
    public static final DeferredItem<Item> ALIENIS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("alienis_infused_mob_aspect");
    public static final DeferredItem<Item> ALIENIS_MOB_ASPECT = REGISTRY.registerSimpleItem("alienis_mob_aspect");
    public static final DeferredItem<Item> AQUA_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("aqua_condensed_mob_aspect");
    public static final DeferredItem<Item> AQUA_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("aqua_infused_mob_aspect");
    public static final DeferredItem<Item> AQUA_MOB_ASPECT = REGISTRY.registerSimpleItem("aqua_mob_aspect");
    public static final DeferredItem<Item> BESTIA_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("bestia_condensed_mob_aspect");
    public static final DeferredItem<Item> BESTIA_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("bestia_infused_mob_aspect");
    public static final DeferredItem<Item> BESTIA_MOB_ASPECT = REGISTRY.registerSimpleItem("bestia_mob_aspect");
    public static final DeferredItem<Item> BLOCK_TALISMAN = REGISTRY.registerItem("block_talisman", BlackHoleTalismanItem::new);
    public static final DeferredItem<BloodSwordItem> BLOOD_SWORD = REGISTRY.registerItem("blood_sword", properties -> new BloodSwordItem(DIAMOND, properties));
    public static final DeferredItem<Item> BRIGHT_NITOR = REGISTRY.registerSimpleItem("bright_nitor");
    public static final DeferredItem<CleansingTalismanItem> CLEANSING_TALISMAN = REGISTRY.registerItem("cleansing_talisman", CleansingTalismanItem::new);
    public static final DeferredItem<Item> CORPUS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("corpus_condensed_mob_aspect");
    public static final DeferredItem<Item> CORPUS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("corpus_infused_mob_aspect");
    public static final DeferredItem<Item> CORPUS_MOB_ASPECT = REGISTRY.registerSimpleItem("corpus_mob_aspect");
    public static final DeferredItem<Item> DARK_QUARTZ = REGISTRY.registerSimpleItem("dark_quartz");
    public static final DeferredItem<ItemFocusBasic> DEFLECT_FOCUS = REGISTRY.registerItem("deflect_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<ItemFocusBasic> DISLOCATION_FOCUS = REGISTRY.registerItem("dislocation_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<Item> EARTH_FRUIT = REGISTRY.registerSimpleItem("earth_fruit");
    public static final DeferredItem<ItemFocusBasic> ENDER_CHEST_FOCUS = REGISTRY.registerItem("ender_chest_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<Item> ENDER_SHARD = REGISTRY.registerSimpleItem("ender_shard");
    public static final DeferredItem<Item> ENTROPY_FRUIT = REGISTRY.registerSimpleItem("entropy_fruit");
    public static final DeferredItem<Item> EXANIMIS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("exanimis_condensed_mob_aspect");
    public static final DeferredItem<Item> EXANIMIS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("exanimis_infused_mob_aspect");
    public static final DeferredItem<Item> EXANIMIS_MOB_ASPECT = REGISTRY.registerSimpleItem("exanimis_mob_aspect");
    public static final DeferredItem<Item> FIRE_FRUIT = REGISTRY.registerSimpleItem("fire_fruit");
    public static final DeferredItem<ItemFocusBasic> FLIGHT_FOCUS = REGISTRY.registerItem("flight_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<ItemFocusBasic> HEAL_FOCUS = REGISTRY.registerItem("heal_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<Item> HUMANUS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("humanus_condensed_mob_aspect");
    public static final DeferredItem<Item> HUMANUS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("humanus_infused_mob_aspect");
    public static final DeferredItem<Item> HUMANUS_MOB_ASPECT = REGISTRY.registerSimpleItem("humanus_mob_aspect");
    
    public static final DeferredItem<Item> ICHOR = REGISTRY.registerSimpleItem("ichor");
    public static final DeferredItem<IchorAxeItem> ICHOR_AXE = REGISTRY.registerItem("ichor_axe", IchorAxeItem::new);
    public static final DeferredItem<IchorclothArmorItem> ICHOR_BOOTS = REGISTRY.registerItem("ichor_boots", properties -> new IchorclothArmorItem(TTArmorMaterials.ICHOR, ArmorItem.Type.BOOTS, properties));
    public static final DeferredItem<Item> ICHOR_CAP = REGISTRY.registerSimpleItem("ichor_cap");
    public static final DeferredItem<IchorclothArmorItem> ICHOR_CHESTPLATE = REGISTRY.registerItem("ichor_chestplate", properties -> new IchorclothArmorItem(TTArmorMaterials.ICHOR, ArmorItem.Type.CHESTPLATE, properties));
    public static final DeferredItem<Item> ICHOR_CLOTH = REGISTRY.registerSimpleItem("ichor_cloth");
    public static final DeferredItem<IchorPouchItem> ICHOR_FOCUS_POUCH = REGISTRY.registerItem("ichor_focus_pouch", IchorPouchItem::new);
    public static final DeferredItem<IchorclothArmorItem> ICHOR_HELMET = REGISTRY.registerItem("ichor_helmet", properties -> new IchorclothArmorItem(TTArmorMaterials.ICHOR, ArmorItem.Type.HELMET, properties));
    public static final DeferredItem<IchorclothArmorItem> ICHOR_LEGGINGS = REGISTRY.registerItem("ichor_leggings", properties -> new IchorclothArmorItem(TTArmorMaterials.ICHOR, ArmorItem.Type.LEGGINGS, properties));
    public static final DeferredItem<Item> ICHOR_NUGGET = REGISTRY.registerSimpleItem("ichor_nugget");
    public static final DeferredItem<IchorPickaxeItem> ICHOR_PICKAXE = REGISTRY.registerItem("ichor_pickaxe", IchorPickaxeItem::new);
    public static final DeferredItem<Item> ICHOR_ROD = REGISTRY.registerSimpleItem("ichor_rod");
    public static final DeferredItem<IchorShovelItem> ICHOR_SHOVEL = REGISTRY.registerItem("ichor_shovel", IchorShovelItem::new);
    public static final DeferredItem<IchorSwordItem> ICHOR_SWORD = REGISTRY.registerItem("ichor_sword", IchorSwordItem::new);
    public static final DeferredItem<Item> ICHORIUM = REGISTRY.registerSimpleItem("ichorium");
    
    public static final DeferredItem<Item> IGNIS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("ignis_condensed_mob_aspect");
    public static final DeferredItem<Item> IGNIS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("ignis_infused_mob_aspect");
    public static final DeferredItem<Item> IGNIS_MOB_ASPECT = REGISTRY.registerSimpleItem("ignis_mob_aspect");
    public static final DeferredItem<Item> ITER_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("iter_condensed_mob_aspect");
    public static final DeferredItem<Item> ITER_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("iter_infused_mob_aspect");
    public static final DeferredItem<Item> ITER_MOB_ASPECT = REGISTRY.registerSimpleItem("iter_mob_aspect");
    public static final DeferredItem<Item> LIMUS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("limus_condensed_mob_aspect");
    public static final DeferredItem<Item> LIMUS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("limus_infused_mob_aspect");
    public static final DeferredItem<Item> LIMUS_MOB_ASPECT = REGISTRY.registerSimpleItem("limus_mob_aspect");
    public static final DeferredItem<Item> MESSIS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("messis_condensed_mob_aspect");
    public static final DeferredItem<Item> MESSIS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("messis_infused_mob_aspect");
    public static final DeferredItem<BlockItem> MOB_MAGNET = REGISTRY.registerItem("mob_magnet", properties -> new BlockItem(thaumictinkerer.common.registry.TTBlocks.MOB_MAGNET.get(), properties));
    public static final DeferredItem<BlockItem> ITEM_MAGNET = REGISTRY.registerItem("item_magnet", properties -> new BlockItem(thaumictinkerer.common.registry.TTBlocks.ITEM_MAGNET.get(), properties));
    public static final DeferredItem<ItemNameBlockItem> AIR_SEED = REGISTRY.registerItem("air_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.AIR_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> COMPOUND_SEED = REGISTRY.registerItem("compound_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.COMPOUND_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> EARTH_SEED = REGISTRY.registerItem("earth_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.EARTH_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> ENTROPY_SEED = REGISTRY.registerItem("entropy_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.ENTROPY_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> FIRE_SEED = REGISTRY.registerItem("fire_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.FIRE_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> ORDER_SEED = REGISTRY.registerItem("order_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.ORDER_CROP.get(), p));
    public static final DeferredItem<ItemNameBlockItem> WATER_SEED = REGISTRY.registerItem("water_seed", p -> new ItemNameBlockItem(thaumictinkerer.common.registry.TTBlocks.WATER_CROP.get(), p));
    public static final DeferredItem<Item> MESSIS_MOB_ASPECT = REGISTRY.registerSimpleItem("messis_mob_aspect");
    public static final DeferredItem<Item> METALLUM_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("metallum_condensed_mob_aspect");
    public static final DeferredItem<Item> METALLUM_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("metallum_infused_mob_aspect");
    public static final DeferredItem<Item> METALLUM_MOB_ASPECT = REGISTRY.registerSimpleItem("metallum_mob_aspect");
    public static final DeferredItem<Item> NETHER_SHARD = REGISTRY.registerSimpleItem("nether_shard");
    public static final DeferredItem<Item> ORDER_FRUIT = REGISTRY.registerSimpleItem("order_fruit");
    public static final DeferredItem<Item> PANNUS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("pannus_condensed_mob_aspect");
    public static final DeferredItem<Item> PANNUS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("pannus_infused_mob_aspect");
    public static final DeferredItem<Item> PANNUS_MOB_ASPECT = REGISTRY.registerSimpleItem("pannus_mob_aspect");
    public static final DeferredItem<Item> PRAECANTATIO_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("praecantatio_condensed_mob_aspect");
    public static final DeferredItem<Item> PRAECANTATIO_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("praecantatio_infused_mob_aspect");
    public static final DeferredItem<Item> PRAECANTATIO_MOB_ASPECT = REGISTRY.registerSimpleItem("praecantatio_mob_aspect");
    public static final DeferredItem<ShareBookItem> SHARE_BOOK = REGISTRY.registerItem("share_book", ShareBookItem::new);
    public static final DeferredItem<ItemFocusBasic> SMELT_FOCUS = REGISTRY.registerItem("smelt_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<SpellClothItem> SPELL_CLOTH = REGISTRY.registerItem("spell_cloth", SpellClothItem::new);
    public static final DeferredItem<ItemFocusBasic> TELEKINESIS_FOCUS = REGISTRY.registerItem("telekinesis_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<Item> TERRA_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("terra_condensed_mob_aspect");
    public static final DeferredItem<Item> TERRA_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("terra_infused_mob_aspect");
    public static final DeferredItem<Item> TERRA_MOB_ASPECT = REGISTRY.registerSimpleItem("terra_mob_aspect");
    public static final DeferredItem<Item> VENENUM_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("venenum_condensed_mob_aspect");
    public static final DeferredItem<Item> VENENUM_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("venenum_infused_mob_aspect");
    public static final DeferredItem<Item> VENENUM_MOB_ASPECT = REGISTRY.registerSimpleItem("venenum_mob_aspect");
    public static final DeferredItem<Item> VOLATUS_CONDENSED_MOB_ASPECT = REGISTRY.registerSimpleItem("volatus_condensed_mob_aspect");
    public static final DeferredItem<Item> VOLATUS_INFUSED_MOB_ASPECT = REGISTRY.registerSimpleItem("volatus_infused_mob_aspect");
    public static final DeferredItem<Item> VOLATUS_MOB_ASPECT = REGISTRY.registerSimpleItem("volatus_mob_aspect");
    public static final DeferredItem<Item> WATER_FRUIT = REGISTRY.registerSimpleItem("water_fruit");
    public static final DeferredItem<ItemFocusBasic> XP_DRAIN_FOCUS = REGISTRY.registerItem("xp_drain_focus", properties -> new ItemFocusBasic(new PrimalVisStorage(0, 0, 0, 0, 0, 0), 0xFFFFFF, 0, false, properties));
    public static final DeferredItem<XpTalismanItem> XP_TALISMAN = REGISTRY.registerItem("xp_talisman", XpTalismanItem::new);
}










