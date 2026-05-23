package thaumcraft.api.aspects;

public interface IEssentiaContainer {
    EssentiaStorage getEssentia();

    int getEssentiaCapacity();

    int fillEssentia(Aspect aspect, int amount, boolean simulate);

    int drainEssentia(Aspect aspect, int amount, boolean simulate);

    default boolean canAccept(Aspect aspect) {
        EssentiaStorage stored = this.getEssentia();
        return stored.isEmpty() || stored.aspect() == aspect;
    }
}
