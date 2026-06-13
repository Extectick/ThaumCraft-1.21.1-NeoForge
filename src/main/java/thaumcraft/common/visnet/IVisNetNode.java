package thaumcraft.common.visnet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;

public interface IVisNetNode {
    BlockPos getVisNetPos();

    int getRange();

    boolean isSource();

    byte getAttunement();

    @Nullable
    BlockPos getParentPos();

    void setParentPos(@Nullable BlockPos parentPos);

    int consumeVis(Aspect aspect, int amount);

    default void triggerConsumeEffect(Aspect aspect) {
    }

    default boolean isValidVisNode(Level level) {
        return true;
    }
}
