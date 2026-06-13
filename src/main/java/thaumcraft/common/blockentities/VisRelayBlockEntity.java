package thaumcraft.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.blocks.VisRelayBlock;
import thaumcraft.common.visnet.IVisNetNode;
import thaumcraft.common.visnet.VisNetHandler;

public class VisRelayBlockEntity extends BlockEntity implements IVisNetNode, IWandable {
    public static final int[] COLORS = new int[] { 0xFFFF7E, 0xFF1A81, 0x0090FF, 0x00A000, 0xEECFFF, 0x555577 };
    private static final int RANGE = 8;

    @Nullable
    private BlockPos parentPos;
    private byte color = -1;
    private int pulse;
    private float pulseRed = 0.5F;
    private float pulseGreen = 0.5F;
    private float pulseBlue = 0.5F;
    private int nodeCounter;

    public VisRelayBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.VIS_RELAY.get(), pos, state);
    }

    protected VisRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VisRelayBlockEntity relay) {
        if (level.isClientSide) {
            relay.clientTick();
            return;
        }
        relay.serverTick();
    }

    protected void serverTick() {
        this.nodeCounter++;
        if (this.nodeCounter % 40 != 0) {
            return;
        }
        BlockPos nextParent = this.parentPos;
        if (nextParent == null
                || !VisNetHandler.isNodeValid(this.level, nextParent)
                || !VisNetHandler.canNodeBeSeen(this.level, this.worldPosition, nextParent)) {
            nextParent = VisNetHandler.findParent(this.level, this);
        }
        if (!samePos(this.parentPos, nextParent)) {
            this.parentPos = nextParent;
            this.markChangedAndSync();
        }
        this.updateConnectedState(VisNetHandler.isNodeValid(this.level, this.parentPos));
    }

    private void clientTick() {
        if (this.pulseRed < 1.0F) {
            this.pulseRed = Math.min(1.0F, this.pulseRed + 0.025F);
        }
        if (this.pulseGreen < 1.0F) {
            this.pulseGreen = Math.min(1.0F, this.pulseGreen + 0.025F);
        }
        if (this.pulseBlue < 1.0F) {
            this.pulseBlue = Math.min(1.0F, this.pulseBlue + 0.025F);
        }
        if (this.pulse > 0) {
            this.pulse--;
        }
    }

    @Override
    public BlockPos getVisNetPos() {
        return this.worldPosition;
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public byte getAttunement() {
        return this.color;
    }

    public void setColor(byte color) {
        if (color < -1 || color > 5) {
            return;
        }
        this.color = color;
        this.parentPos = null;
        this.nodeCounter = 39;
        this.updateConnectedState(false);
        this.markChangedAndSync();
    }

    @Nullable
    @Override
    public BlockPos getParentPos() {
        return this.parentPos;
    }

    @Override
    public void setParentPos(@Nullable BlockPos parentPos) {
        this.parentPos = parentPos;
        this.updateConnectedState(parentPos != null && VisNetHandler.isNodeValid(this.level, parentPos));
        this.markChangedAndSync();
    }

    @Override
    public int consumeVis(Aspect aspect, int amount) {
        if (this.level == null || this.parentPos == null || amount <= 0) {
            return 0;
        }
        IVisNetNode parent = VisNetHandler.asNode(this.level.getBlockEntity(this.parentPos));
        if (parent == null || !parent.isValidVisNode(this.level)) {
            this.parentPos = null;
            this.updateConnectedState(false);
            this.markChangedAndSync();
            return 0;
        }
        int drained = parent.consumeVis(aspect, amount);
        if (drained > 0) {
            this.triggerConsumeEffect(aspect);
        }
        return drained;
    }

    @Override
    public void triggerConsumeEffect(Aspect aspect) {
        if (aspect == null) {
            return;
        }
        int index = Aspect.getPrimalAspects().indexOf(aspect);
        if (index < 0 || index >= COLORS.length || this.pulse > 0) {
            return;
        }
        this.pulse = 5;
        int color = COLORS[index];
        this.pulseRed = ((color >> 16) & 255) / 255.0F;
        this.pulseGreen = ((color >> 8) & 255) / 255.0F;
        this.pulseBlue = (color & 255) / 255.0F;
        this.markChangedAndSync();
        if (this.level != null && this.parentPos != null) {
            IVisNetNode parent = VisNetHandler.asNode(this.level.getBlockEntity(this.parentPos));
            if (parent != null) {
                parent.triggerConsumeEffect(aspect);
            }
        }
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        byte nextColor = (byte) (this.color + 1);
        if (nextColor > 5) {
            nextColor = -1;
        }
        this.setColor(nextColor);
        level.playSound(null, pos, TCSoundEvents.CRYSTAL.get(), SoundSource.BLOCKS, 0.2F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    public Direction getOrientation() {
        BlockState state = this.getBlockState();
        return state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : Direction.UP;
    }

    public float getPulseRed() {
        return this.pulseRed;
    }

    public float getPulseGreen() {
        return this.pulseGreen;
    }

    public float getPulseBlue() {
        return this.pulseBlue;
    }

    public boolean isPulsing() {
        return this.pulse > 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.color = tag.getByte("Color");
        this.pulse = tag.getInt("Pulse");
        this.pulseRed = tag.contains("PulseRed") ? tag.getFloat("PulseRed") : 0.5F;
        this.pulseGreen = tag.contains("PulseGreen") ? tag.getFloat("PulseGreen") : 0.5F;
        this.pulseBlue = tag.contains("PulseBlue") ? tag.getFloat("PulseBlue") : 0.5F;
        this.parentPos = tag.contains("ParentX")
                ? new BlockPos(tag.getInt("ParentX"), tag.getInt("ParentY"), tag.getInt("ParentZ"))
                : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("Color", this.color);
        tag.putInt("Pulse", this.pulse);
        tag.putFloat("PulseRed", this.pulseRed);
        tag.putFloat("PulseGreen", this.pulseGreen);
        tag.putFloat("PulseBlue", this.pulseBlue);
        if (this.parentPos != null) {
            tag.putInt("ParentX", this.parentPos.getX());
            tag.putInt("ParentY", this.parentPos.getY());
            tag.putInt("ParentZ", this.parentPos.getZ());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    private void updateConnectedState(boolean connected) {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockState state = this.getBlockState();
        if (state.hasProperty(VisRelayBlock.CONNECTED) && state.getValue(VisRelayBlock.CONNECTED) != connected) {
            this.level.setBlock(this.worldPosition, state.setValue(VisRelayBlock.CONNECTED, connected), 3);
        }
    }

    private static boolean samePos(@Nullable BlockPos left, @Nullable BlockPos right) {
        return left == null ? right == null : left.equals(right);
    }
}
