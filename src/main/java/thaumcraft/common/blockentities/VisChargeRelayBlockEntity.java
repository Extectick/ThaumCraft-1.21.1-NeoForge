package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.registry.TCBlockEntities;

public class VisChargeRelayBlockEntity extends VisRelayBlockEntity {
    public VisChargeRelayBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.VIS_CHARGE_RELAY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VisChargeRelayBlockEntity relay) {
        VisRelayBlockEntity.tick(level, pos, state, relay);
        if (!level.isClientSide) {
            relay.chargeWorkbench();
        }
    }

    private void chargeWorkbench() {
        if (this.level == null || !(this.level.getBlockEntity(this.worldPosition.below())
                instanceof ArcaneWorktableBlockEntity worktable)) {
            return;
        }
        var wandStack = worktable.getWand();
        if (!(wandStack.getItem() instanceof WandCastingItem wand)) {
            return;
        }

        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int room = wand.getMaxVis(wandStack) - wand.getVis(wandStack, aspect);
            int request = Math.min(5, room);
            if (request <= 0) {
                continue;
            }
            int drained = this.consumeVis(aspect, request);
            if (drained > 0) {
                wand.addVis(wandStack, aspect, drained);
                worktable.setChanged();
            }
        }
    }
}
