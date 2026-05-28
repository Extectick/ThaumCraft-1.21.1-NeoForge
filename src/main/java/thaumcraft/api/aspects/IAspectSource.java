package thaumcraft.api.aspects;

public interface IAspectSource extends IEssentiaContainer {
    default boolean takeFromContainer(Aspect aspect, int amount) {
        return this.drainEssentia(aspect, amount, false) == amount;
    }

    default boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return this.drainEssentia(aspect, amount, true) == amount;
    }
}
