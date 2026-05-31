package thaumcraft.server.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.blockentities.ArcaneAlembicBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.blocks.AlchemicalFurnaceBlock;
import thaumcraft.common.blocks.EssentiaTubeBlock.TubeMode;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;

public final class ServerEssentiaTransportService {
    private ServerEssentiaTransportService() {
    }

    public static void tickTube(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        if (tube.decrementVenting() > 0) {
            return;
        }
        if (tube.getCount() == 0) {
            tube.setCount(level.random.nextInt(10));
        }
        tickValvePower(level, tube);

        if (tube.getMode() == TubeMode.BUFFER) {
            if (tube.incrementCount() % 5 == 0 && tube.getBufferVisSize() < tube.getEssentiaCapacity()) {
                fillBuffer(level, tube);
            }
            return;
        }

        if (tube.incrementCount() % 2 == 0) {
            calculateSuction(level, tube, tube.getMode() == TubeMode.FILTERED ? tube.getFilterAspect() : null,
                    tube.getMode() == TubeMode.RESTRICTED, tube.getMode() == TubeMode.DIRECTIONAL);
            checkVenting(level, tube);
            tube.clearEmptyEssentiaType();
        }

        if (tube.getCount() % 5 == 0 && tube.getSuctionAmount(null) > 0) {
            equalizeWithNeighbours(level, tube, tube.getMode() == TubeMode.DIRECTIONAL);
        }
    }

    public static void tickAlchemicalFurnace(Level level, BlockPos pos, BlockState state,
            AlchemicalFurnaceBlockEntity furnace) {
        boolean wasBurning = furnace.isBurning();
        boolean hadVis = furnace.hasStoredVis();
        boolean changed = false;
        int count = furnace.incrementCount();

        furnace.decrementBurnTime();

        if (count % (furnace.isSpeedBoosted() ? 20 : 40) == 0 && furnace.hasStoredAspects()) {
            changed |= transferEssentiaUp(level, pos, furnace);
        }

        if (furnace.getBurnTimeRemaining() == 0 && canSmelt(furnace)) {
            ItemStack fuel = furnace.getItem(AlchemicalFurnaceBlockEntity.FUEL_SLOT);
            int burnTime = fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
            furnace.setBurnFromFuel(burnTime);
            if (burnTime > 0) {
                changed = true;
                furnace.setSpeedBoost(fuel.is(TCItems.ALUMENTUM.get()));
                furnace.consumeFuel();
            }
        }

        if (furnace.isBurning() && canSmelt(furnace)) {
            furnace.setCookTime(furnace.getCookTime() + 1);
            if (furnace.getCookTime() >= furnace.getSmeltTime()) {
                furnace.setCookTime(0);
                smeltItem(furnace);
                changed = true;
            }
        } else {
            furnace.setCookTime(0);
        }

        if (wasBurning != furnace.isBurning()) {
            changed = true;
        }
        if (changed) {
            furnace.setChanged();
        }
        if (wasBurning != furnace.isBurning() || hadVis != furnace.hasStoredVis()) {
            level.setBlock(pos, state.setValue(AlchemicalFurnaceBlock.LIT, furnace.isBurning())
                    .setValue(AlchemicalFurnaceBlock.FILLED, furnace.hasStoredVis()), 3);
        }
    }

    public static void tickWardedJar(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
        if (jar.incrementCount() % 5 == 0 && (jar.isVoidJar() || jar.getEssentiaAmount(null) < jar.getEssentiaCapacity())) {
            fillJar(level, pos, jar);
        }
    }

    public static int takeBufferEssentia(EssentiaTubeBlockEntity tube, Aspect aspect, int amount, Direction face) {
        if (!tube.canOutputTo(face)) {
            return 0;
        }
        int suction = 0;
        IEssentiaTransport requestingTransport = ThaumcraftApiHelper.getConnectableTransport(tube.getLevel(),
                tube.getBlockPos(), face);
        if (requestingTransport != null) {
            suction = requestingTransport.getSuctionAmount(face.getOpposite());
        }
        for (Direction direction : Direction.values()) {
            if (!tube.canOutputTo(direction) || direction == face) {
                continue;
            }
            IEssentiaTransport transport = ThaumcraftApiHelper.getConnectableTransport(tube.getLevel(),
                    tube.getBlockPos(), direction);
            if (transport == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            int sideSuction = transport.getSuctionAmount(opposite);
            Aspect sideSuctionType = transport.getSuctionType(opposite);
            if ((sideSuctionType == aspect || sideSuctionType == null) && suction < sideSuction
                    && tube.getSuctionAmount(direction) < sideSuction) {
                return 0;
            }
        }
        var storage = tube.getEssentia();
        int drained = Math.min(amount, !storage.isEmpty() && storage.aspect() == aspect ? storage.amount() : 0);
        return drained > 0 && tube.drainEssentia(aspect, drained, false) == drained ? drained : 0;
    }

    private static void calculateSuction(Level level, EssentiaTubeBlockEntity tube, Aspect filter, boolean restrict,
            boolean directional) {
        tube.setSuction(null, 0);
        for (Direction direction : Direction.values()) {
            if ((directional && tube.getFacing() != direction.getOpposite()) || !tube.isConnectable(direction)) {
                continue;
            }
            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(level, tube.getBlockPos(),
                    direction);
            if (neighbor == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            Aspect neighborSuctionType = neighbor.getSuctionType(opposite);
            if ((filter == null || neighborSuctionType == null || neighborSuctionType == filter)
                    && (filter != null || tube.getEssentiaAmount(direction) <= 0 || neighborSuctionType == null
                            || tube.getEssentiaType(direction) == neighborSuctionType)
                    && (filter == null || tube.getEssentiaAmount(direction) <= 0
                            || tube.getEssentiaType(direction) == null || neighborSuctionType == null
                            || tube.getEssentiaType(direction) == neighborSuctionType)) {
                int neighborSuction = neighbor.getSuctionAmount(opposite);
                if (neighborSuction > 0 && neighborSuction > tube.getSuctionAmount(null) + 1) {
                    Aspect newSuctionType = neighborSuctionType == null ? filter : neighborSuctionType;
                    tube.setSuction(newSuctionType, restrict ? neighborSuction / 2 : neighborSuction - 1);
                }
            }
        }
    }

    private static void checkVenting(Level level, EssentiaTubeBlockEntity tube) {
        for (Direction direction : Direction.values()) {
            if (!tube.isConnectable(direction)) {
                continue;
            }
            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(level, tube.getBlockPos(),
                    direction);
            if (neighbor == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            int neighborSuction = neighbor.getSuctionAmount(opposite);
            if (tube.getSuctionAmount(null) > 0
                    && (neighborSuction == tube.getSuctionAmount(null) || neighborSuction == tube.getSuctionAmount(null) - 1)
                    && tube.getSuctionType(null) != neighbor.getSuctionType(opposite)) {
                tube.setVenting(40);
                return;
            }
        }
    }

    private static void equalizeWithNeighbours(Level level, EssentiaTubeBlockEntity tube, boolean directional) {
        if (tube.getEssentiaAmount(null) > 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if ((directional && tube.getFacing() == direction.getOpposite()) || !tube.isConnectable(direction)) {
                continue;
            }
            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(level, tube.getBlockPos(),
                    direction);
            if (neighbor == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            if (!neighbor.canOutputTo(opposite)
                    || (tube.getSuctionType(null) != null && tube.getSuctionType(null) != neighbor.getEssentiaType(opposite)
                            && neighbor.getEssentiaType(opposite) != null)
                    || tube.getSuctionAmount(null) <= neighbor.getSuctionAmount(opposite)
                    || tube.getSuctionAmount(null) < neighbor.getMinimumSuction()) {
                continue;
            }
            Aspect aspect = tube.getSuctionType(null);
            if (aspect == null) {
                aspect = neighbor.getEssentiaType(opposite);
            }
            if (aspect == null) {
                aspect = neighbor.getEssentiaType(null);
            }
            if (aspect == null) {
                continue;
            }
            int moved = tube.addEssentia(aspect, neighbor.takeEssentia(aspect, 1, opposite), direction);
            if (moved > 0) {
                return;
            }
        }
    }

    private static void fillBuffer(Level level, EssentiaTubeBlockEntity tube) {
        for (Direction direction : Direction.values()) {
            IEssentiaTransport transport = ThaumcraftApiHelper.getConnectableTransport(level, tube.getBlockPos(),
                    direction);
            if (transport == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            if (transport.getEssentiaAmount(opposite) > 0
                    && transport.getSuctionAmount(opposite) < tube.getSuctionAmount(direction)
                    && tube.getSuctionAmount(direction) >= transport.getMinimumSuction()) {
                Aspect aspect = transport.getEssentiaType(opposite);
                tube.addEssentia(aspect, transport.takeEssentia(aspect, 1, opposite), direction);
                return;
            }
        }
    }

    private static void tickValvePower(Level level, EssentiaTubeBlockEntity tube) {
        if (tube.getMode() != TubeMode.VALVE || tube.getCount() % 5 != 0) {
            return;
        }
        boolean powered = level.hasNeighborSignal(tube.getBlockPos());
        if (tube.wasPoweredLastTick() && !powered && !tube.isEnabled()) {
            tube.setAllowFlow(true);
            level.playSound(null, tube.getBlockPos(), TCSoundEvents.SQUEEK.get(), SoundSource.BLOCKS, 0.7F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        if (!tube.wasPoweredLastTick() && powered && tube.isEnabled()) {
            tube.setAllowFlow(false);
            level.playSound(null, tube.getBlockPos(), TCSoundEvents.SQUEEK.get(), SoundSource.BLOCKS, 0.7F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        tube.setWasPoweredLastTick(powered);
    }

    private static boolean canSmelt(AlchemicalFurnaceBlockEntity furnace) {
        ItemStack input = furnace.getItem(AlchemicalFurnaceBlockEntity.INPUT_SLOT);
        if (input.isEmpty()) {
            return false;
        }
        AspectList inputAspects = ObjectAspectRegistry.getObjectTagsWithBonus(input);
        if (inputAspects.isEmpty()) {
            return false;
        }
        int aspectAmount = inputAspects.visSize();
        if (aspectAmount > furnace.getRemainingVisCapacity()) {
            return false;
        }
        furnace.setSmeltTime(aspectAmount * 10);
        return true;
    }

    private static void smeltItem(AlchemicalFurnaceBlockEntity furnace) {
        if (!canSmelt(furnace)) {
            return;
        }
        AspectList inputAspects = ObjectAspectRegistry.getObjectTagsWithBonus(
                furnace.getItem(AlchemicalFurnaceBlockEntity.INPUT_SLOT));
        furnace.addAspects(inputAspects);
        furnace.shrinkInput();
    }

    private static boolean transferEssentiaUp(Level level, BlockPos pos, AlchemicalFurnaceBlockEntity furnace) {
        boolean changed = false;
        AspectList excluded = new AspectList();
        for (int depth = 1; depth <= 5; depth++) {
            if (!(level.getBlockEntity(pos.above(depth)) instanceof ArcaneAlembicBlockEntity alembic)) {
                break;
            }
            if (!alembic.getEssentia().isEmpty()
                    && alembic.getEssentia().amount() < alembic.getEssentiaCapacity()
                    && furnace.getAspectAmount(alembic.getEssentia().aspect()) > 0) {
                Aspect aspect = alembic.getEssentia().aspect();
                furnace.takeFromContainer(aspect, 1);
                alembic.fillEssentia(aspect, 1, false);
                excluded.merge(aspect, 1);
                changed = true;
            }
        }
        for (int depth = 1; depth <= 5; depth++) {
            if (!(level.getBlockEntity(pos.above(depth)) instanceof ArcaneAlembicBlockEntity alembic)) {
                break;
            }
            if (alembic.getEssentia().isEmpty()) {
                Aspect aspect = null;
                if (alembic.getFilterAspect() == null) {
                    aspect = furnace.takeRandomAspect(level, excluded);
                } else if (furnace.takeFromContainer(alembic.getFilterAspect(), 1)) {
                    aspect = alembic.getFilterAspect();
                }
                if (aspect != null) {
                    alembic.fillEssentia(aspect, 1, false);
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            furnace.markStorageChanged(level, pos);
        }
        return changed;
    }

    private static void fillJar(Level level, BlockPos pos, WardedJarBlockEntity jar) {
        IEssentiaTransport transport = ThaumcraftApiHelper.getConnectableTransport(level, pos, Direction.UP);
        if (transport == null || !transport.canOutputTo(Direction.DOWN)) {
            return;
        }
        Aspect targetAspect = jar.getFilterAspect() != null ? jar.getFilterAspect() : jar.getEssentiaType(Direction.UP);
        if (targetAspect == null && transport.getEssentiaAmount(Direction.DOWN) > 0
                && transport.getSuctionAmount(Direction.DOWN) < jar.getSuctionAmount(Direction.UP)
                && jar.getSuctionAmount(Direction.UP) >= transport.getMinimumSuction()) {
            targetAspect = transport.getEssentiaType(Direction.DOWN);
        }
        if (targetAspect != null && jar.canAccept(targetAspect)
                && transport.getSuctionAmount(Direction.DOWN) < jar.getSuctionAmount(Direction.UP)) {
            jar.fillEssentia(targetAspect, transport.takeEssentia(targetAspect, 1, Direction.DOWN), false);
        }
    }
}
