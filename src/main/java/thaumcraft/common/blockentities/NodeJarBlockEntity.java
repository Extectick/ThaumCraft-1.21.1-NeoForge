package thaumcraft.common.blockentities;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeJarData;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

public class NodeJarBlockEntity extends BlockEntity implements INode, IWandable {
    private NodeJarData data = NodeJarData.EMPTY;
    private int captureAnimationTicks;

    public NodeJarBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.NODE_IN_A_JAR.get(), pos, state);
    }

    public NodeJarData getNodeData() {
        return this.data;
    }

    public void setNodeData(NodeJarData data) {
        this.data = data == null ? NodeJarData.EMPTY : data;
        this.markChangedAndSync();
    }

    public int getCaptureAnimationTicks() {
        return this.captureAnimationTicks;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, NodeJarBlockEntity jar) {
        if (jar.captureAnimationTicks > 0) {
            jar.captureAnimationTicks--;
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 9) {
            this.captureAnimationTicks = 20;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        UUID id = tag.hasUUID("nodeId") ? tag.getUUID("nodeId") : new UUID(0L, 0L);
        NodeType type = safeNodeType(tag.getInt("type"));
        NodeModifier modifier = NodeModifier.byOrdinal(tag.getInt("modifier"));
        AspectList aspects = AspectList.load(tag, "Aspects");
        AspectList base = AspectList.load(tag, "AspectsBase");
        this.data = new NodeJarData(id, type, modifier, aspects, base);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID("nodeId", this.data.nodeId());
        tag.putInt("type", this.data.nodeType().ordinal());
        tag.putInt("modifier", this.data.nodeModifier() == null ? -1 : this.data.nodeModifier().ordinal());
        this.data.aspects().writeToNBT(tag, "Aspects");
        this.data.baseAspects().writeToNBT(tag, "AspectsBase");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public InteractionResult onWandRightClick(Level level, BlockPos pos, Player player, ItemStack wand,
            BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            NodeJarData released = this.data;
            level.setBlock(pos, TCBlocks.AURA_NODE.get().defaultBlockState(), 3);
            if (level.getBlockEntity(pos) instanceof AuraNodeBlockEntity node) {
                node.configure(released.nodeType(), released.nodeModifier(), released.aspects(),
                        released.baseAspects(), released.nodeId());
            }
            level.levelEvent(2001, pos, Block.getId(TCBlocks.NODE_IN_A_JAR.get().defaultBlockState()));
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F,
                    0.9F + level.random.nextFloat() * 0.2F);
        }
        InteractionHand hand = player.getOffhandItem() == wand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        player.swing(hand, true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public String getNodeId() {
        return this.data.nodeId().toString();
    }

    @Override
    public AspectList getAspects() {
        return this.data.aspects();
    }

    @Override
    public AspectList getAspectsBase() {
        return this.data.baseAspects();
    }

    @Override
    public NodeType getNodeType() {
        return this.data.nodeType();
    }

    @Override
    public void setNodeType(NodeType type) {
        this.setNodeData(new NodeJarData(this.data.nodeId(), type, this.data.nodeModifier(), this.data.aspects(),
                this.data.baseAspects()));
    }

    @Nullable
    @Override
    public NodeModifier getNodeModifier() {
        return this.data.nodeModifier();
    }

    @Override
    public void setNodeModifier(@Nullable NodeModifier modifier) {
        this.setNodeData(new NodeJarData(this.data.nodeId(), this.data.nodeType(), modifier, this.data.aspects(),
                this.data.baseAspects()));
    }

    @Override
    public int getNodeVisBase(Aspect aspect) {
        return this.data.baseAspects().getAmount(aspect);
    }

    @Override
    public void setNodeVisBase(Aspect aspect, int amount) {
        AspectList base = this.data.baseAspects();
        base.setAmount(aspect, amount);
        this.setNodeData(new NodeJarData(this.data.nodeId(), this.data.nodeType(), this.data.nodeModifier(),
                this.data.aspects(), base));
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return false;
    }

    private void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    private static NodeType safeNodeType(int ordinal) {
        return ordinal >= 0 && ordinal < NodeType.values().length ? NodeType.values()[ordinal] : NodeType.NORMAL;
    }
}
