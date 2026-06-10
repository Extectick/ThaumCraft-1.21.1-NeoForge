package thaumcraft.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.research.ResearchManager;

import java.util.ArrayList;

public class PrimordialPearlItem extends Item {

    public PrimordialPearlItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof AuraNodeBlockEntity node) {
            if (player != null) {
                player.swing(context.getHand());
            }

            if (!level.isClientSide()) {
                context.getItemInHand().shrink(1);

                boolean research = player != null && ResearchManager.isComplete(player, "PRIMNODE");

                for (Aspect a : new ArrayList<>(node.getAspects().getAspects())) {
                    int m = node.getNodeVisBase(a);
                    if (!a.isPrimal()) {
                        if (level.random.nextBoolean()) {
                            node.setNodeVisBase(a, (short) Math.max(0, m - 1));
                        }
                    } else {
                        m = m - 2 + level.random.nextInt(research ? 9 : 6);
                        node.setNodeVisBase(a, (short) Math.max(0, m));
                    }
                }

                for (Aspect a : Aspect.getPrimalAspects()) {
                    int m = node.getNodeVisBase(a);
                    int r = level.random.nextInt(research ? 4 : 3);
                    if (r > 0 && r > m) {
                        node.setNodeVisBase(a, (short) r);
                        node.addToContainer(a, 1);
                    }
                }

                if (node.getNodeModifier() == NodeModifier.FADING && level.random.nextBoolean()) {
                    node.setNodeModifier(NodeModifier.PALE);
                } else if (node.getNodeModifier() == NodeModifier.PALE && level.random.nextBoolean()) {
                    node.setNodeModifier(null);
                } else if (node.getNodeModifier() == null && level.random.nextInt(5) == 0) {
                    node.setNodeModifier(NodeModifier.BRIGHT);
                }

                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                node.setChanged();

                level.explode(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 3.0f + level.random.nextFloat() * (research ? 3.0f : 5.0f), Level.ExplosionInteraction.BLOCK);

                for (int a = 0; a < 33; ++a) {
                    int xx = pos.getX() + level.random.nextInt(6) - level.random.nextInt(6);
                    int yy = pos.getY() + level.random.nextInt(6) - level.random.nextInt(6);
                    int zz = pos.getZ() + level.random.nextInt(6) - level.random.nextInt(6);
                    BlockPos targetPos = new BlockPos(xx, yy, zz);
                    if (level.isEmptyBlock(targetPos)) {
                        if (yy < pos.getY()) {
                            level.setBlock(targetPos, TCBlocks.FLUX_GOO.get().defaultBlockState(), 3);
                        } else {
                            level.setBlock(targetPos, TCBlocks.FLUX_GAS.get().defaultBlockState(), 3);
                        }
                    }
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }
}
