package thaumcraft.common.research;

import net.minecraft.network.chat.Component;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;

public final class AuraNodeScan {
    private AuraNodeScan() {
    }

    public static String key(INode node) {
        return "node:" + node.getNodeId();
    }

    public static Component name(INode node) {
        Component type = Component.translatable("nodetype." + node.getNodeType().name() + ".name");
        NodeModifier modifier = node.getNodeModifier();
        return modifier == null ? type
                : Component.translatable("nodemod." + modifier.name() + ".name")
                        .append(", ")
                        .append(type);
    }

    public static AspectList aspects(INode node) {
        AspectList result = new AspectList();
        for (Aspect aspect : node.getAspects().getAspectsSorted()) {
            result.add(aspect, Math.max(4, node.getAspects().getAmount(aspect) / 10));
        }

        switch (node.getNodeType()) {
            case UNSTABLE -> result.merge(Aspect.ENTROPY, 4);
            case HUNGRY -> result.merge(Aspect.HUNGER, 4);
            case TAINTED -> result.merge(Aspect.TAINT, 4);
            case PURE -> {
                result.merge(Aspect.HEAL, 2);
                result.add(Aspect.ORDER, 2);
            }
            case DARK -> {
                result.merge(Aspect.DEATH, 2);
                result.add(Aspect.DARKNESS, 2);
            }
            case NORMAL -> {
            }
        }
        return result;
    }
}
