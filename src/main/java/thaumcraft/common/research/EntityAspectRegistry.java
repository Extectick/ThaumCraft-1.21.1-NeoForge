package thaumcraft.common.research;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public final class EntityAspectRegistry {
    private static final Map<ResourceLocation, AspectList> ENTITY_ASPECTS = new HashMap<>();

    static {
        register("minecraft:zombie", aspects().add(Aspect.UNDEAD, 2).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));
        register("minecraft:zombie_villager", aspects().add(Aspect.UNDEAD, 2).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));
        register("minecraft:giant", aspects().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 3).add(Aspect.EARTH, 3));
        register("minecraft:skeleton", aspects().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));
        register("minecraft:wither_skeleton", aspects().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 1).add(Aspect.FIRE, 2));
        register("minecraft:creeper", aspects().add(Aspect.PLANT, 2).add(Aspect.FIRE, 2));
        register("minecraft:horse", aspects().add(Aspect.BEAST, 4).add(Aspect.EARTH, 1).add(Aspect.AIR, 1));
        register("minecraft:donkey", aspects().add(Aspect.BEAST, 4).add(Aspect.EARTH, 1).add(Aspect.AIR, 1));
        register("minecraft:mule", aspects().add(Aspect.BEAST, 4).add(Aspect.EARTH, 1).add(Aspect.AIR, 1));
        register("minecraft:pig", aspects().add(Aspect.BEAST, 2).add(Aspect.EARTH, 2));
        register("minecraft:experience_orb", aspects().add(Aspect.MIND, 5));
        register("minecraft:sheep", aspects().add(Aspect.BEAST, 2).add(Aspect.EARTH, 2));
        register("minecraft:cow", aspects().add(Aspect.BEAST, 3).add(Aspect.EARTH, 3));
        register("minecraft:mooshroom", aspects().add(Aspect.BEAST, 3).add(Aspect.PLANT, 1).add(Aspect.EARTH, 2));
        register("minecraft:snow_golem", aspects().add(Aspect.COLD, 3).add(Aspect.WATER, 1));
        register("minecraft:ocelot", aspects().add(Aspect.BEAST, 3).add(Aspect.ENTROPY, 3));
        register("minecraft:cat", aspects().add(Aspect.BEAST, 3).add(Aspect.ENTROPY, 3));
        register("minecraft:chicken", aspects().add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 2).add(Aspect.AIR, 1));
        register("minecraft:squid", aspects().add(Aspect.BEAST, 2).add(Aspect.WATER, 2));
        register("minecraft:glow_squid", aspects().add(Aspect.BEAST, 2).add(Aspect.WATER, 2).add(Aspect.LIGHT, 1));
        register("minecraft:wolf", aspects().add(Aspect.BEAST, 3).add(Aspect.EARTH, 3));
        register("minecraft:bat", aspects().add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 1).add(Aspect.AIR, 1));
        register("minecraft:boat", aspects().add(Aspect.MECHANISM, 2).add(Aspect.WATER, 2));
        register("minecraft:chest_boat", aspects().add(Aspect.MECHANISM, 2).add(Aspect.WATER, 2).add(Aspect.VOID, 1));
        register("minecraft:spider", aspects().add(Aspect.BEAST, 3).add(Aspect.ENTROPY, 2));
        register("minecraft:slime", aspects().add(Aspect.SLIME, 2).add(Aspect.WATER, 2));
        register("minecraft:ghast", aspects().add(Aspect.UNDEAD, 3).add(Aspect.FIRE, 2));
        register("minecraft:zombified_piglin", aspects().add(Aspect.UNDEAD, 4).add(Aspect.FIRE, 2));
        register("minecraft:enderman", aspects().add(Aspect.ELDRITCH, 4).add(Aspect.TRAVEL, 2).add(Aspect.AIR, 2));
        register("minecraft:cave_spider", aspects().add(Aspect.BEAST, 2).add(Aspect.POISON, 2).add(Aspect.EARTH, 1));
        register("minecraft:silverfish", aspects().add(Aspect.BEAST, 1).add(Aspect.EARTH, 1));
        register("minecraft:blaze", aspects().add(Aspect.ELDRITCH, 4).add(Aspect.FIRE, 1));
        register("minecraft:magma_cube", aspects().add(Aspect.SLIME, 3).add(Aspect.FIRE, 2));
        register("minecraft:ender_dragon", aspects().add(Aspect.ELDRITCH, 20).add(Aspect.BEAST, 20).add(Aspect.ENTROPY, 20));
        register("minecraft:wither", aspects().add(Aspect.UNDEAD, 20).add(Aspect.ENTROPY, 20).add(Aspect.FIRE, 15));
        register("minecraft:witch", aspects().add(Aspect.MAN, 3).add(Aspect.MAGIC, 2).add(Aspect.FIRE, 1));
        register("minecraft:villager", aspects().add(Aspect.MAN, 3).add(Aspect.AIR, 2));
        register("minecraft:iron_golem", aspects().add(Aspect.METAL, 4).add(Aspect.EARTH, 3));
        register("minecraft:minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 2));
        register("minecraft:chest_minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.VOID, 1));
        register("minecraft:furnace_minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.FIRE, 1));
        register("minecraft:tnt_minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.FIRE, 1));
        register("minecraft:hopper_minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.EXCHANGE, 1));
        register("minecraft:spawner_minecart", aspects().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.MAGIC, 1));
        register("minecraft:end_crystal", aspects().add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 3).add(Aspect.HEAL, 3));
        register("minecraft:item_frame", aspects().add(Aspect.SENSES, 3).add(Aspect.CLOTH, 1));
        register("minecraft:glow_item_frame", aspects().add(Aspect.SENSES, 3).add(Aspect.CLOTH, 1).add(Aspect.LIGHT, 1));
        register("minecraft:painting", aspects().add(Aspect.SENSES, 5).add(Aspect.CLOTH, 3));
    }

    private EntityAspectRegistry() {
    }

    public static AspectList getEntityAspects(Entity entity) {
        if (entity instanceof Player player) {
            return playerAspects(player);
        }
        if (entity instanceof Creeper creeper && creeper.isPowered()) {
            return aspects().add(Aspect.PLANT, 3).add(Aspect.FIRE, 3).add(Aspect.ENERGY, 3);
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        AspectList aspects = ENTITY_ASPECTS.get(id);
        return aspects == null ? AspectList.EMPTY : aspects.copy();
    }

    public static String entityKey(Entity entity) {
        if (entity instanceof Player player) {
            return "entity:player_" + player.getGameProfile().getName();
        }
        String key = "entity:" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entity instanceof Creeper creeper && creeper.isPowered()) {
            key += ":POWERED";
        }
        return key;
    }

    private static AspectList playerAspects(Player player) {
        String name = player.getGameProfile().getName();
        AspectList aspects = aspects().add(Aspect.MAN, 4);
        if ("azanor".equalsIgnoreCase(name)) {
            return aspects.add(Aspect.ELDRITCH, 20);
        }
        if ("direwolf20".equalsIgnoreCase(name)) {
            return aspects.add(Aspect.BEAST, 20);
        }
        if ("pahimar".equalsIgnoreCase(name)) {
            return aspects.add(Aspect.EXCHANGE, 20);
        }
        Random random = new Random(("player_" + name).hashCode());
        Aspect[] values = Aspect.values();
        aspects.add(values[random.nextInt(values.length)], 4);
        aspects.add(values[random.nextInt(values.length)], 4);
        aspects.add(values[random.nextInt(values.length)], 4);
        return aspects;
    }

    private static void register(String id, AspectList aspects) {
        ENTITY_ASPECTS.put(ResourceLocation.parse(id), aspects.copy());
    }

    private static AspectList aspects() {
        return new AspectList();
    }
}
