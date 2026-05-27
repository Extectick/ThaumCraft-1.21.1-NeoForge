package thaumcraft.api.aspects;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;

public interface IEssentiaTransport {
    boolean isConnectable(Direction face);

    boolean canInputFrom(Direction face);

    boolean canOutputTo(Direction face);

    void setSuction(@Nullable Aspect aspect, int amount);

    @Nullable
    Aspect getSuctionType(@Nullable Direction face);

    int getSuctionAmount(@Nullable Direction face);

    int takeEssentia(Aspect aspect, int amount, Direction face);

    int addEssentia(Aspect aspect, int amount, Direction face);

    @Nullable
    Aspect getEssentiaType(@Nullable Direction face);

    int getEssentiaAmount(@Nullable Direction face);

    int getMinimumSuction();

    boolean renderExtendedTube();
}
