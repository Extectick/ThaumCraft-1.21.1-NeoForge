package thaumcraft.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.EssentiaStorage;
import thaumcraft.api.aspects.IEssentiaContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.blocks.EssentiaTubeBlock;
import thaumcraft.common.blocks.EssentiaTubeBlock.TubeMode;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;

public class EssentiaTubeBlockEntity extends BlockEntity implements IEssentiaContainer, IEssentiaTransport, IWandable {
    private static final int BUFFER_CAPACITY = 8;
    private static final double TRACE_MIN = 0.42D;
    private static final double TRACE_MAX = 0.58D;
    private static final double CORE_MIN = 0.34375D;
    private static final double CORE_MAX = 0.65625D;

    private final boolean[] openSides = new boolean[] { true, true, true, true, true, true };
    private Direction facing = Direction.NORTH;
    @Nullable
    private Aspect essentiaType;
    private int essentiaAmount;
    @Nullable
    private Aspect suctionType;
    private int suction;
    private final AspectList bufferAspects = new AspectList();
    private byte[] chokedSides = new byte[] { 0, 0, 0, 0, 0, 0 };
    private int venting;
    private int count;
    @Nullable
    private Aspect filterAspect;
    private boolean allowFlow = true;
    private boolean wasPoweredLastTick;

    public EssentiaTubeBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.ESSENTIA_TUBE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        if (tube.venting > 0) {
            tube.venting--;
        }
        if (tube.count == 0) {
            tube.count = level.random.nextInt(10);
        }
        tube.tickValvePower(level);

        if (tube.venting > 0) {
            return;
        }

        if (tube.getMode() == TubeMode.BUFFER) {
            if (++tube.count % 5 == 0 && tube.bufferAspects.visSize() < BUFFER_CAPACITY) {
                tube.fillBuffer();
            }
            return;
        }

        if (++tube.count % 2 == 0) {
            tube.calculateSuction(tube.getMode() == TubeMode.FILTERED ? tube.filterAspect : null,
                    tube.getMode() == TubeMode.RESTRICTED, tube.getMode() == TubeMode.DIRECTIONAL);
            tube.checkVenting();
            if (tube.essentiaAmount == 0) {
                tube.essentiaType = null;
            }
        }

        if (tube.count % 5 == 0 && tube.suction > 0) {
            tube.equalizeWithNeighbours(tube.getMode() == TubeMode.DIRECTIONAL);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.essentiaType = Aspect.byTag(tag.getString("type")).orElseGet(
                () -> Aspect.byTag(tag.getString("aspect")).orElse(null));
        this.essentiaAmount = tag.getInt("amount");
        if (this.essentiaAmount <= 0) {
            this.essentiaType = null;
            this.essentiaAmount = 0;
        }
        this.suctionType = Aspect.byTag(tag.getString("stype")).orElse(null);
        this.suction = tag.getInt("samount");
        this.bufferAspects.readFromNBT(tag);
        if (this.getMode() == TubeMode.BUFFER && this.bufferAspects.isEmpty() && this.essentiaType != null
                && this.essentiaAmount > 0) {
            this.bufferAspects.add(this.essentiaType, this.essentiaAmount);
            this.essentiaType = null;
            this.essentiaAmount = 0;
        }
        this.filterAspect = Aspect.byTag(tag.getString("AspectFilter")).orElseGet(
                () -> Aspect.byTag(tag.getString("filter")).orElse(null));
        this.allowFlow = !tag.contains("flow") || tag.getBoolean("flow");
        if (tag.contains("enabled")) {
            this.allowFlow = tag.getBoolean("enabled");
        }
        this.wasPoweredLastTick = tag.getBoolean("hadpower");
        this.facing = Direction.from3DDataValue(tag.getByte("side"));
        if (tag.contains("open")) {
            byte[] sides = tag.getByteArray("open");
            if (sides.length == 6) {
                for (int i = 0; i < 6; i++) {
                    this.openSides[i] = sides[i] == 1;
                }
            }
        }
        if (tag.contains("choke")) {
            byte[] choke = tag.getByteArray("choke");
            if (choke.length == 6) {
                this.chokedSides = choke;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.essentiaType != null && this.essentiaAmount > 0) {
            tag.putString("type", this.essentiaType.getTag());
        }
        tag.putInt("amount", this.essentiaAmount);
        if (this.suctionType != null) {
            tag.putString("stype", this.suctionType.getTag());
        }
        tag.putInt("samount", this.suction);
        if (this.getMode() == TubeMode.BUFFER) {
            this.bufferAspects.writeToNBT(tag);
            tag.putByteArray("choke", this.chokedSides);
        }
        if (this.filterAspect != null) {
            tag.putString("AspectFilter", this.filterAspect.getTag());
        }
        tag.putBoolean("flow", this.allowFlow);
        tag.putBoolean("enabled", this.allowFlow);
        tag.putBoolean("hadpower", this.wasPoweredLastTick);
        tag.putByte("side", (byte) this.facing.get3DDataValue());
        byte[] sides = new byte[6];
        for (int i = 0; i < 6; i++) {
            sides[i] = (byte) (this.openSides[i] ? 1 : 0);
        }
        tag.putByteArray("open", sides);
    }

    @Override
    public EssentiaStorage getEssentia() {
        if (this.getMode() == TubeMode.BUFFER) {
            Aspect aspect = this.getRandomBufferAspect();
            return aspect != null ? new EssentiaStorage(aspect, this.bufferAspects.getAmount(aspect))
                    : EssentiaStorage.EMPTY;
        }
        return this.essentiaType != null && this.essentiaAmount > 0
                ? new EssentiaStorage(this.essentiaType, this.essentiaAmount)
                : EssentiaStorage.EMPTY;
    }

    @Override
    public int getEssentiaCapacity() {
        return this.getMode() == TubeMode.BUFFER ? BUFFER_CAPACITY : 1;
    }

    @Override
    public int fillEssentia(Aspect aspect, int amount, boolean simulate) {
        if (this.getMode() == TubeMode.BUFFER) {
            if (amount != 1 || this.bufferAspects.visSize() >= BUFFER_CAPACITY) {
                return 0;
            }
            if (!simulate) {
                this.bufferAspects.add(aspect, 1);
                this.markStorageChanged();
            }
            return 1;
        }

        if (amount <= 0 || !this.canAccept(aspect)) {
            return 0;
        }
        int accepted = Math.min(amount, this.getEssentiaCapacity() - this.essentiaAmount);
        if (accepted > 0 && !simulate) {
            this.essentiaType = aspect;
            this.essentiaAmount += accepted;
            this.markStorageChanged();
        }
        return accepted;
    }

    @Override
    public int drainEssentia(Aspect aspect, int amount, boolean simulate) {
        if (this.getMode() == TubeMode.BUFFER) {
            if (amount <= 0 || this.bufferAspects.getAmount(aspect) < amount) {
                return 0;
            }
            if (!simulate) {
                this.bufferAspects.remove(aspect, amount);
                this.markStorageChanged();
            }
            return amount;
        }

        if (amount <= 0 || this.essentiaType != aspect || this.essentiaAmount <= 0) {
            return 0;
        }
        int drained = Math.min(amount, this.essentiaAmount);
        if (drained > 0 && !simulate) {
            this.essentiaAmount -= drained;
            if (this.essentiaAmount <= 0) {
                this.essentiaType = null;
                this.essentiaAmount = 0;
            }
            this.markStorageChanged();
        }
        return drained;
    }

    @Override
    public boolean canAccept(Aspect aspect) {
        if (this.getMode() == TubeMode.BUFFER) {
            return true;
        }
        return (this.essentiaType == null || this.essentiaType == aspect)
                && (this.filterAspect == null || this.filterAspect == aspect);
    }

    @Override
    public boolean isConnectable(Direction face) {
        if (this.getMode() == TubeMode.VALVE && face == this.facing) {
            return false;
        }
        return this.openSides[face.ordinal()];
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return this.openSides[face.ordinal()];
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return this.openSides[face.ordinal()];
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
        if (this.getMode() != TubeMode.VALVE || this.allowFlow) {
            this.suctionType = aspect;
            this.suction = amount;
            this.setChanged();
        }
    }

    @Nullable
    @Override
    public Aspect getSuctionType(@Nullable Direction face) {
        return this.suctionType;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        if (this.getMode() == TubeMode.BUFFER && face != null) {
            int choke = this.chokedSides[face.ordinal()];
            return choke == 2 ? 0 : 1;
        }
        return this.suction;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (this.getMode() == TubeMode.BUFFER) {
            return this.takeBufferEssentia(aspect, amount, face);
        }

        if (this.canOutputTo(face) && this.essentiaType == aspect && this.essentiaAmount > 0 && amount > 0) {
            this.essentiaAmount--;
            if (this.essentiaAmount <= 0) {
                this.essentiaType = null;
                this.essentiaAmount = 0;
            }
            this.markStorageChanged();
            return 1;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (this.getMode() == TubeMode.BUFFER) {
            return this.canInputFrom(face) ? amount - this.addToBuffer(aspect, amount) : 0;
        }

        if (this.canInputFrom(face) && this.essentiaAmount == 0 && amount > 0 && this.canAccept(aspect)) {
            this.essentiaType = aspect;
            this.essentiaAmount = 1;
            this.markStorageChanged();
            return 1;
        }
        return 0;
    }

    @Nullable
    @Override
    public Aspect getEssentiaType(@Nullable Direction face) {
        if (this.getMode() == TubeMode.BUFFER) {
            return this.getRandomBufferAspect();
        }
        return this.essentiaType;
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        if (this.getMode() == TubeMode.BUFFER) {
            return this.bufferAspects.visSize();
        }
        return this.essentiaAmount;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        Direction hitPart = getTraceablePart(pos, player, hitResult);
        if (hitPart == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (this.canConnectSide(hitPart)) {
                if (this.getMode() == TubeMode.BUFFER && player.isShiftKeyDown()) {
                    this.chokedSides[hitPart.ordinal()]++;
                    if (this.chokedSides[hitPart.ordinal()] > 2) {
                        this.chokedSides[hitPart.ordinal()] = 0;
                    }
                    this.markStorageChanged();
                    level.playSound(null, pos, TCSoundEvents.SQUEEK.get(), SoundSource.BLOCKS, 0.6F,
                            1.1F + level.random.nextFloat() * 0.2F);
                } else {
                    this.openSides[hitPart.ordinal()] = !this.openSides[hitPart.ordinal()];
                    this.suction = 0;
                    this.suctionType = null;
                    this.essentiaType = this.essentiaAmount <= 0 ? null : this.essentiaType;
                    this.markStorageChanged();
                    syncAdjacentTubeSide(level, pos, hitPart, this.openSides[hitPart.ordinal()]);
                    updateTubeState(level, pos);
                    updateTubeState(level, pos.relative(hitPart));
                    level.playSound(null, pos, TCSoundEvents.TOOL.get(), SoundSource.BLOCKS, 0.5F,
                            0.9F + level.random.nextFloat() * 0.2F);
                }
            } else if (hitPart == Direction.UP || hitPart == Direction.DOWN || hitPart == Direction.NORTH
                    || hitPart == Direction.SOUTH || hitPart == Direction.WEST || hitPart == Direction.EAST) {
                rotateFacingToNextConnectable(level, pos);
                level.playSound(null, pos, TCSoundEvents.TOOL.get(), SoundSource.BLOCKS, 0.5F,
                        0.9F + level.random.nextFloat() * 0.2F);
            }
        }
        player.swing(player.getUsedItemHand(), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean isEnabled() {
        return this.allowFlow;
    }

    public void toggleEnabled() {
        this.allowFlow = !this.allowFlow;
        this.markStorageChanged();
    }

    public void setFilterAspect(Aspect aspect) {
        this.filterAspect = aspect;
        this.markStorageChanged();
    }

    public void clearFilterAspect() {
        this.filterAspect = null;
        this.markStorageChanged();
    }

    private void calculateSuction(@Nullable Aspect filter, boolean restrict, boolean directional) {
        this.suction = 0;
        this.suctionType = null;
        if (this.level == null) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if ((directional && this.facing != direction.getOpposite()) || !this.isConnectable(direction)) {
                continue;
            }

            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(this.level, this.worldPosition,
                    direction);
            if (neighbor == null) {
                continue;
            }

            Direction opposite = direction.getOpposite();
            Aspect neighborSuctionType = neighbor.getSuctionType(opposite);
            if ((filter == null || neighborSuctionType == null || neighborSuctionType == filter)
                    && (filter != null || this.getEssentiaAmount(direction) <= 0 || neighborSuctionType == null
                            || this.getEssentiaType(direction) == neighborSuctionType)
                    && (filter == null || this.getEssentiaAmount(direction) <= 0
                            || this.getEssentiaType(direction) == null || neighborSuctionType == null
                            || this.getEssentiaType(direction) == neighborSuctionType)) {
                int neighborSuction = neighbor.getSuctionAmount(opposite);
                if (neighborSuction > 0 && neighborSuction > this.suction + 1) {
                    Aspect newSuctionType = neighborSuctionType == null ? filter : neighborSuctionType;
                    this.setSuction(newSuctionType, restrict ? neighborSuction / 2 : neighborSuction - 1);
                }
            }
        }
    }

    private void checkVenting() {
        if (this.level == null) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (!this.isConnectable(direction)) {
                continue;
            }
            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(this.level, this.worldPosition,
                    direction);
            if (neighbor == null) {
                continue;
            }
            Direction opposite = direction.getOpposite();
            int neighborSuction = neighbor.getSuctionAmount(opposite);
            if (this.suction > 0 && (neighborSuction == this.suction || neighborSuction == this.suction - 1)
                    && this.suctionType != neighbor.getSuctionType(opposite)) {
                this.venting = 40;
                this.markStorageChanged();
                return;
            }
        }
    }

    private void equalizeWithNeighbours(boolean directional) {
        if (this.level == null || this.essentiaAmount > 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if ((directional && this.facing == direction.getOpposite()) || !this.isConnectable(direction)) {
                continue;
            }
            IEssentiaTransport neighbor = ThaumcraftApiHelper.getConnectableTransport(this.level, this.worldPosition,
                    direction);
            if (neighbor == null) {
                continue;
            }

            Direction opposite = direction.getOpposite();
            if (!neighbor.canOutputTo(opposite)
                    || (this.getSuctionType(null) != null && this.getSuctionType(null) != neighbor.getEssentiaType(opposite)
                            && neighbor.getEssentiaType(opposite) != null)
                    || this.getSuctionAmount(null) <= neighbor.getSuctionAmount(opposite)
                    || this.getSuctionAmount(null) < neighbor.getMinimumSuction()) {
                continue;
            }

            Aspect aspect = this.getSuctionType(null);
            if (aspect == null) {
                aspect = neighbor.getEssentiaType(opposite);
            }
            if (aspect == null) {
                aspect = neighbor.getEssentiaType(null);
            }
            if (aspect == null) {
                continue;
            }

            int moved = this.addEssentia(aspect, neighbor.takeEssentia(aspect, 1, opposite), direction);
            if (moved > 0) {
                return;
            }
        }
    }

    private int addToBuffer(Aspect aspect, int amount) {
        if (amount != 1 || this.bufferAspects.visSize() >= BUFFER_CAPACITY) {
            return amount;
        }
        this.bufferAspects.add(aspect, 1);
        this.markStorageChanged();
        return 0;
    }

    private int takeBufferEssentia(Aspect aspect, int amount, Direction face) {
        if (!this.canOutputTo(face)) {
            return 0;
        }

        int suction = 0;
        IEssentiaTransport requestingTransport = ThaumcraftApiHelper.getConnectableTransport(this.level,
                this.worldPosition, face);
        if (requestingTransport != null) {
            suction = requestingTransport.getSuctionAmount(face.getOpposite());
        }

        for (Direction direction : Direction.values()) {
            if (!this.canOutputTo(direction) || direction == face) {
                continue;
            }

            IEssentiaTransport transport = ThaumcraftApiHelper.getConnectableTransport(this.level, this.worldPosition,
                    direction);
            if (transport == null) {
                continue;
            }

            Direction opposite = direction.getOpposite();
            int sideSuction = transport.getSuctionAmount(opposite);
            Aspect sideSuctionType = transport.getSuctionType(opposite);
            if ((sideSuctionType == aspect || sideSuctionType == null) && suction < sideSuction
                    && this.getSuctionAmount(direction) < sideSuction) {
                return 0;
            }
        }

        int drained = Math.min(amount, this.bufferAspects.getAmount(aspect));
        return drained > 0 && this.drainEssentia(aspect, drained, false) == drained ? drained : 0;
    }

    private void fillBuffer() {
        if (this.level == null) {
            return;
        }

        for (Direction direction : Direction.values()) {
            IEssentiaTransport transport = ThaumcraftApiHelper.getConnectableTransport(this.level, this.worldPosition,
                    direction);
            if (transport == null) {
                continue;
            }

            Direction opposite = direction.getOpposite();
            if (transport.getEssentiaAmount(opposite) > 0
                    && transport.getSuctionAmount(opposite) < this.getSuctionAmount(direction)
                    && this.getSuctionAmount(direction) >= transport.getMinimumSuction()) {
                Aspect aspect = transport.getEssentiaType(opposite);
                this.addToBuffer(aspect, transport.takeEssentia(aspect, 1, opposite));
                return;
            }
        }
    }

    @Nullable
    private Aspect getRandomBufferAspect() {
        if (this.bufferAspects.isEmpty() || this.level == null) {
            return null;
        }
        var aspects = this.bufferAspects.getAspects();
        return aspects.get(this.level.random.nextInt(aspects.size()));
    }

    private void tickValvePower(Level level) {
        if (this.getMode() != TubeMode.VALVE || this.count % 5 != 0) {
            return;
        }
        boolean powered = level.hasNeighborSignal(this.worldPosition);
        if (this.wasPoweredLastTick && !powered && !this.allowFlow) {
            this.allowFlow = true;
            this.markStorageChanged();
            level.playSound(null, this.worldPosition, TCSoundEvents.SQUEEK.get(), SoundSource.BLOCKS, 0.7F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        if (!this.wasPoweredLastTick && powered && this.allowFlow) {
            this.allowFlow = false;
            this.markStorageChanged();
            level.playSound(null, this.worldPosition, TCSoundEvents.SQUEEK.get(), SoundSource.BLOCKS, 0.7F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        this.wasPoweredLastTick = powered;
    }

    private TubeMode getMode() {
        return this.getBlockState().getBlock() instanceof EssentiaTubeBlock tube ? tube.getMode() : TubeMode.NORMAL;
    }

    private boolean canConnectSide(Direction direction) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition.relative(direction)) instanceof IEssentiaTransport;
    }

    private void rotateFacingToNextConnectable(Level level, BlockPos pos) {
        if (this.getMode() == TubeMode.VALVE) {
            rotateValveFacing(level, pos);
            return;
        }

        int start = this.facing.ordinal();
        for (int offset = 1; offset < 20; offset++) {
            Direction candidate = Direction.values()[(start + offset) % Direction.values().length];
            Direction opposite = candidate.getOpposite();
            if (this.canConnectSide(opposite) && this.isConnectable(opposite)) {
                this.facing = candidate;
                this.markStorageChanged();
                updateTubeState(level, pos);
                return;
            }
        }
    }

    private void rotateValveFacing(Level level, BlockPos pos) {
        int start = this.facing.ordinal();
        for (int offset = 1; offset < 20; offset++) {
            Direction candidate = Direction.values()[(start + offset) % Direction.values().length];
            if (!this.canConnectSide(candidate)) {
                this.facing = candidate;
                this.markStorageChanged();
                updateTubeState(level, pos);
                return;
            }
        }
    }

    private static void syncAdjacentTubeSide(Level level, BlockPos pos, Direction direction, boolean open) {
        BlockEntity adjacent = level.getBlockEntity(pos.relative(direction));
        if (adjacent instanceof EssentiaTubeBlockEntity tube) {
            tube.openSides[direction.getOpposite().ordinal()] = open;
            tube.markStorageChanged();
        }
    }

    private static void updateTubeState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof EssentiaTubeBlock tube) {
            level.setBlock(pos, tube.stateWithConnections(level, pos, state), 3);
        }
    }

    private Direction getTraceablePart(BlockPos pos, Player player, BlockHitResult hitResult) {
        Direction rayHit = rayTraceTraceablePart(pos, player);
        if (rayHit != null) {
            return rayHit;
        }
        Vec3 local = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        double x = local.x();
        double y = local.y();
        double z = local.z();
        if (x >= TRACE_MIN && x <= TRACE_MAX && z >= TRACE_MIN && z <= TRACE_MAX) {
            if (y < 0.5D && this.canConnectSide(Direction.DOWN)) {
                return Direction.DOWN;
            }
            if (y > 0.5D && this.canConnectSide(Direction.UP)) {
                return Direction.UP;
            }
        }
        if (x >= TRACE_MIN && x <= TRACE_MAX && y >= TRACE_MIN && y <= TRACE_MAX) {
            if (z < 0.5D && this.canConnectSide(Direction.NORTH)) {
                return Direction.NORTH;
            }
            if (z > 0.5D && this.canConnectSide(Direction.SOUTH)) {
                return Direction.SOUTH;
            }
        }
        if (y >= TRACE_MIN && y <= TRACE_MAX && z >= TRACE_MIN && z <= TRACE_MAX) {
            if (x < 0.5D && this.canConnectSide(Direction.WEST)) {
                return Direction.WEST;
            }
            if (x > 0.5D && this.canConnectSide(Direction.EAST)) {
                return Direction.EAST;
            }
        }
        if (x >= CORE_MIN && x <= CORE_MAX && y >= CORE_MIN && y <= CORE_MAX && z >= CORE_MIN && z <= CORE_MAX) {
            return Direction.UP;
        }
        return null;
    }

    @Nullable
    private Direction rayTraceTraceablePart(BlockPos pos, Player player) {
        if (this.level == null) {
            return null;
        }
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(player.blockInteractionRange()));
        Direction bestDirection = null;
        double bestDistance = Double.MAX_VALUE;
        for (Direction direction : Direction.values()) {
            if (!this.canConnectSide(direction)) {
                continue;
            }
            AABB box = traceBox(pos, direction);
            var hit = box.clip(start, end);
            if (hit.isPresent()) {
                double distance = start.distanceToSqr(hit.get());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestDirection = direction;
                }
            }
        }
        AABB core = new AABB(pos.getX() + CORE_MIN, pos.getY() + CORE_MIN, pos.getZ() + CORE_MIN,
                pos.getX() + CORE_MAX, pos.getY() + CORE_MAX, pos.getZ() + CORE_MAX);
        var coreHit = core.clip(start, end);
        if (coreHit.isPresent() && start.distanceToSqr(coreHit.get()) < bestDistance) {
            return Direction.UP;
        }
        return bestDirection;
    }

    private static AABB traceBox(BlockPos pos, Direction direction) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        return switch (direction) {
            case DOWN -> new AABB(x + TRACE_MIN, y, z + TRACE_MIN, x + TRACE_MAX, y + 0.5D, z + TRACE_MAX);
            case UP -> new AABB(x + TRACE_MIN, y + 0.5D, z + TRACE_MIN, x + TRACE_MAX, y + 1.0D, z + TRACE_MAX);
            case NORTH -> new AABB(x + TRACE_MIN, y + TRACE_MIN, z, x + TRACE_MAX, y + TRACE_MAX, z + 0.5D);
            case SOUTH -> new AABB(x + TRACE_MIN, y + TRACE_MIN, z + 0.5D, x + TRACE_MAX, y + TRACE_MAX, z + 1.0D);
            case WEST -> new AABB(x, y + TRACE_MIN, z + TRACE_MIN, x + 0.5D, y + TRACE_MAX, z + TRACE_MAX);
            case EAST -> new AABB(x + 0.5D, y + TRACE_MIN, z + TRACE_MIN, x + 1.0D, y + TRACE_MAX, z + TRACE_MAX);
        };
    }

    private void markStorageChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
