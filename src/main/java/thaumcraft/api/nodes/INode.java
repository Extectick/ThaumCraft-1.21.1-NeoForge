package thaumcraft.api.nodes;

import javax.annotation.Nullable;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface INode {
    String getNodeId();

    AspectList getAspects();

    AspectList getAspectsBase();

    NodeType getNodeType();

    void setNodeType(NodeType type);

    @Nullable
    NodeModifier getNodeModifier();

    void setNodeModifier(@Nullable NodeModifier modifier);

    int getNodeVisBase(Aspect aspect);

    void setNodeVisBase(Aspect aspect, int amount);

    int addToContainer(Aspect aspect, int amount);

    boolean takeFromContainer(Aspect aspect, int amount);
}
