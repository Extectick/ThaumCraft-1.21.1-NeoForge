package thaumictinkerer.common.items.equipment;


import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.registry.TTItems;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

public class TTArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTRAR = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, ThaumicTinkerer.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ICHOR = REGISTRAR.register("ichor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }), 20, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(TTItems.ICHOR_CLOTH.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "ichor"))), 2.0F, 0.1F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ADVANCED_ICHOR = REGISTRAR.register("advanced_ichor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }), 20, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(TTItems.ICHOR_CLOTH.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThaumicTinkerer.MODID, "advanced_ichor"))), 2.0F, 0.1F));
}

