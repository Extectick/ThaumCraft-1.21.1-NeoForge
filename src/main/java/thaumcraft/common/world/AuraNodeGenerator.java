package thaumcraft.common.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;

public final class AuraNodeGenerator {
    private AuraNodeGenerator() {
    }

    public static void configureRandomNode(Level level, BlockPos pos, RandomSource random) {
        configureRandomNode((LevelAccessor) level, pos, random);
    }

    public static void configureRandomNode(LevelAccessor level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AuraNodeBlockEntity node)) {
            return;
        }

        NodeType type = randomType(random);
        NodeModifier modifier = randomModifier(random);
        AspectList aspects = randomAspects(level, pos, random, type);
        node.configure(type, modifier, aspects);
    }

    public static void configureSilverwoodNode(Level level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AuraNodeBlockEntity node)) {
            return;
        }

        NodeModifier modifier = randomModifier(random);
        AspectList aspects = randomAspects(level, pos, random, NodeType.PURE, true);
        node.configure(NodeType.PURE, modifier, aspects);
    }

    private static NodeType randomType(RandomSource random) {
        if (random.nextInt(specialNodeRarity()) != 0) {
            return NodeType.NORMAL;
        }
        return switch (random.nextInt(10)) {
            case 0, 1, 2 -> NodeType.DARK;
            case 3, 4, 5 -> NodeType.UNSTABLE;
            case 6, 7, 8 -> NodeType.PURE;
            default -> NodeType.HUNGRY;
        };
    }

    private static NodeModifier randomModifier(RandomSource random) {
        if (random.nextInt(specialNodeRarity() / 2) != 0) {
            return null;
        }
        return NodeModifier.values()[random.nextInt(NodeModifier.values().length)];
    }

    private static AspectList randomAspects(LevelAccessor level, BlockPos pos, RandomSource random, NodeType type) {
        return randomAspects(level, pos, random, type, false);
    }

    private static AspectList randomAspects(LevelAccessor level, BlockPos pos, RandomSource random, NodeType type,
            boolean silverwood) {
        List<Aspect> primals = Aspect.getPrimalAspects();
        List<Aspect> compounds = Aspect.getCompoundAspects();
        var biome = level.getBiome(pos);
        int biomeAura = AuraNodeBiomeRules.aura(biome);
        if (silverwood) {
            biomeAura /= 4;
        }
        int value = random.nextInt(Math.max(1, biomeAura / 2)) + biomeAura / 2;
        AspectList weights = new AspectList();
        Aspect biomeAspect = AuraNodeBiomeRules.randomTag(biome, random);
        if (biomeAspect != null) {
            weights.add(biomeAspect, 2);
        } else {
            weights.add(compounds.get(random.nextInt(compounds.size())), 1);
            weights.add(primals.get(random.nextInt(primals.size())), 1);
        }

        for (int i = 0; i < 3; i++) {
            if (random.nextBoolean()) {
                List<Aspect> source = random.nextInt(specialNodeRarity()) == 0 ? compounds : primals;
                weights.merge(source.get(random.nextInt(source.size())), 1);
            }
        }

        addTypeAspects(weights, type, random);
        addEnvironmentAspects(level, pos, weights);

        List<Aspect> aspects = new ArrayList<>(weights.getAspectsSorted());
        int[] spread = new int[aspects.size()];
        int total = 0;
        for (int i = 0; i < aspects.size(); i++) {
            spread[i] = weights.getAmount(aspects.get(i)) == 2 ? 50 + random.nextInt(25) : 25 + random.nextInt(50);
            total += spread[i];
        }

        AspectList result = weights.copy();
        for (int i = 0; i < aspects.size(); i++) {
            result.merge(aspects.get(i), spread[i] * value / total);
        }
        return result;
    }

    private static void addTypeAspects(AspectList aspects, NodeType type, RandomSource random) {
        if (type == NodeType.HUNGRY) {
            aspects.merge(Aspect.HUNGER, 2);
            if (random.nextBoolean()) {
                aspects.merge(Aspect.GREED, 1);
            }
        } else if (type == NodeType.PURE) {
            aspects.merge(random.nextBoolean() ? Aspect.LIFE : Aspect.ORDER, 2);
        } else if (type == NodeType.DARK) {
            if (random.nextBoolean()) aspects.merge(Aspect.DEATH, 1);
            if (random.nextBoolean()) aspects.merge(Aspect.UNDEAD, 1);
            if (random.nextBoolean()) aspects.merge(Aspect.ENTROPY, 1);
            if (random.nextBoolean()) aspects.merge(Aspect.DARKNESS, 1);
        }
    }

    private static void addEnvironmentAspects(LevelAccessor level, BlockPos pos, AspectList aspects) {
        int water = 0;
        int lava = 0;
        int stone = 0;
        int foliage = 0;
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
            BlockState state = level.getBlockState(nearby);
            if (state.getFluidState().is(FluidTags.WATER)) water++;
            else if (state.getFluidState().is(FluidTags.LAVA)) lava++;
            else if (state.is(Blocks.STONE)) stone++;
            if (state.is(BlockTags.LEAVES)) foliage++;
        }
        if (water > 100) aspects.merge(Aspect.WATER, 1);
        if (lava > 100) {
            aspects.merge(Aspect.FIRE, 1);
            aspects.merge(Aspect.EARTH, 1);
        }
        if (stone > 500) aspects.merge(Aspect.EARTH, 1);
        if (foliage > 100) aspects.merge(Aspect.PLANT, 1);
    }

    private static int specialNodeRarity() {
        return thaumcraft.common.config.ThaumcraftConfig.SPECIAL_AURA_NODE_RARITY.get();
    }
}
