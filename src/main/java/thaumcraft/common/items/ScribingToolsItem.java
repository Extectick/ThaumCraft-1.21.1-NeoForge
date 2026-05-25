package thaumcraft.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.blocks.ResearchTableBlock;
import thaumcraft.common.blocks.SimpleTableBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public class ScribingToolsItem extends Item {
    public ScribingToolsItem(Properties properties) {
        super(properties.stacksTo(1).durability(100));
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.INK_SAC) || super.isValidRepairItem(stack, repairCandidate);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(TCBlocks.TABLE.get())) {
            return InteractionResult.PASS;
        }

        Direction direction = findTablePairDirection(context, level, pos);
        if (direction == null) {
            return InteractionResult.PASS;
        }

        BlockPos neighborPos = pos.relative(direction);
        if (!level.getBlockState(neighborPos).is(TCBlocks.TABLE.get())) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            level.setBlock(pos, TCBlocks.RESEARCH_TABLE.get().defaultBlockState()
                    .setValue(SimpleTableBlock.FACING, direction)
                    .setValue(ResearchTableBlock.PRIMARY, true), 3);
            level.setBlock(neighborPos, TCBlocks.RESEARCH_TABLE.get().defaultBlockState()
                    .setValue(SimpleTableBlock.FACING, direction.getOpposite())
                    .setValue(ResearchTableBlock.PRIMARY, false), 3);

            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                if (level.getBlockEntity(pos) instanceof ResearchTableBlockEntity researchTable) {
                    ItemStack tools = context.getItemInHand().copyWithCount(1);
                    researchTable.setItem(ResearchTableBlockEntity.SCRIBING_TOOLS_SLOT, tools);
                }
                context.getItemInHand().shrink(1);
            }
            level.playSound(null, pos, TCSoundEvents.WRITE.get(), SoundSource.BLOCKS, 0.45F, 1.0F);
            level.playSound(null, pos, TCSoundEvents.HHON.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static Direction findTablePairDirection(UseOnContext context, Level level, BlockPos pos) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal() && level.getBlockState(pos.relative(clickedFace)).is(TCBlocks.TABLE.get())) {
            return clickedFace;
        }

        Direction found = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).is(TCBlocks.TABLE.get())) {
                if (found != null) {
                    return null;
                }
                found = direction;
            }
        }
        return found;
    }
}
