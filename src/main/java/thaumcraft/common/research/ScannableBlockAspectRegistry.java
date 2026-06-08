package thaumcraft.common.research;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public final class ScannableBlockAspectRegistry {
    private ScannableBlockAspectRegistry() {
    }

    public static Optional<Entry> get(BlockState state) {
        Optional<Entry> fluid = get(state.getFluidState());
        if (fluid.isPresent()) {
            return fluid;
        }
        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return Optional.of(new Entry("object:minecraft:fire:-1", Component.translatable("block.minecraft.fire"),
                    new AspectList().add(Aspect.FIRE, 4)));
        }
        return Optional.empty();
    }

    private static Optional<Entry> get(FluidState fluidState) {
        if (!fluidState.isSource()) {
            return Optional.empty();
        }
        if (fluidState.is(Fluids.WATER) || fluidState.is(Fluids.FLOWING_WATER)) {
            return Optional.of(new Entry("object:minecraft:water:-1", Component.translatable("block.minecraft.water"),
                    new AspectList().add(Aspect.WATER, 4)));
        }
        if (fluidState.is(Fluids.LAVA) || fluidState.is(Fluids.FLOWING_LAVA)) {
            return Optional.of(new Entry("object:minecraft:lava:-1", Component.translatable("block.minecraft.lava"),
                    new AspectList().add(Aspect.FIRE, 3).add(Aspect.EARTH, 1)));
        }
        return Optional.empty();
    }

    public record Entry(String key, Component name, AspectList aspects) {
        public Entry {
            aspects = aspects == null ? AspectList.EMPTY : aspects.copy();
        }
    }
}
