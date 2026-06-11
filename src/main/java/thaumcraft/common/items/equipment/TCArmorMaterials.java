package thaumcraft.common.items.equipment;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCItems;

import java.util.EnumMap;
import java.util.List;

public class TCArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTRAR = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Thaumcraft.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GOGGLES = REGISTRAR.register("goggles",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.GOLD_INGOT),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("goggles"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FORTRESS = REGISTRAR.register("fortress",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 4);
            }), 35, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("fortress"))), 2.0F, 0.1F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> THAUMIUM = REGISTRAR.register("thaumium",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }), 25, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("thaumium"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOID = REGISTRAR.register("void",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 3);
            }), 10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(TCItems.VOID_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("void"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ROBE = REGISTRAR.register("robe",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(TCItems.ENCHANTED_FABRIC.get()),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("robes"), "", true),
                            new ArmorMaterial.Layer(Thaumcraft.id("robes"), "_overlay", false)), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOID_ROBE = REGISTRAR.register("void_robe",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 3);
            }), 10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(TCItems.VOID_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("void_robe_armor"), "_overlay", true),
                            new ArmorMaterial.Layer(Thaumcraft.id("void_robe_armor"), "", false)), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CULTIST_CLOTH = REGISTRAR.register("cultist_cloth",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }), 9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Items.IRON_INGOT),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("crimson_robe_armor"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CULTIST_PLATE = REGISTRAR.register("cultist_plate",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }), 9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Items.IRON_INGOT),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("crimson_plate_armor"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CULTIST_LEADER = REGISTRAR.register("cultist_leader",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }), 25, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(Items.IRON_INGOT),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("crimson_leader_armor"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TRAVELLER = REGISTRAR.register("traveller",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.GOLD_INGOT),
                    List.of(new ArmorMaterial.Layer(Thaumcraft.id("bootstraveler"))), 0.0F, 0.0F));
}



