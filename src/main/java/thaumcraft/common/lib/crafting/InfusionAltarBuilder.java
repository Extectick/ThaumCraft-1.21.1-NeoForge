package thaumcraft.common.lib.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.blocks.InfusionPillarBlock;
import thaumcraft.common.blocks.InfusionPillarBlock.Corner;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public final class InfusionAltarBuilder {
    private static final PrimalVisStorage ACTIVATION_COST = new PrimalVisStorage(2500, 2500, 2500, 2500, 2500, 2500);

    private InfusionAltarBuilder() {
    }

    public static InteractionResult tryCreate(Level level, BlockPos clickedPos, ItemStack wand, Player player) {
        if (!(wand.getItem() instanceof WandCastingItem wandItem)) {
            return InteractionResult.PASS;
        }

        BlockPos origin = findOrigin(level, clickedPos);
        if (origin == null) {
            reportClosestMismatch(level, clickedPos, player);
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player != null && !player.getAbilities().instabuild
                && !wandItem.consumeAllVis(wand, player, ACTIVATION_COST, true)) {
            level.playSound(null, clickedPos, TCSoundEvents.WANDFAIL.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
            return InteractionResult.FAIL;
        }

        replace(level, origin);
        return InteractionResult.CONSUME;
    }

    private static BlockPos findOrigin(Level level, BlockPos clickedPos) {
        for (int x = clickedPos.getX() - 2; x <= clickedPos.getX(); x++) {
            for (int y = clickedPos.getY() - 2; y <= clickedPos.getY(); y++) {
                for (int z = clickedPos.getZ() - 2; z <= clickedPos.getZ(); z++) {
                    BlockPos origin = new BlockPos(x, y, z);
                    if (fits(level, origin)) {
                        return origin;
                    }
                }
            }
        }
        return null;
    }

    private static boolean fits(Level level, BlockPos origin) {
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockPos pos = fromBlueprint(origin, xx, yy, zz);
                    if (!matches(level, pos, xx, yy, zz)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void reportClosestMismatch(Level level, BlockPos clickedPos, Player player) {
        if (level.isClientSide || player == null) {
            return;
        }

        Mismatch mismatch = findClosestMismatch(level, clickedPos);
        if (mismatch != null) {
            player.displayClientMessage(Component.literal("Infusion altar mismatch: " + mismatch.pos().getX() + ", "
                    + mismatch.pos().getY() + ", " + mismatch.pos().getZ() + " expected " + mismatch.expected()
                    + ", found " + mismatch.found() + mismatchHint(level, mismatch)), true);
        }
    }

    private static String mismatchHint(Level level, Mismatch mismatch) {
        if (!"thaumcraft:arcane_pedestal".equals(mismatch.expected())
                || !"minecraft:air".equals(mismatch.found())) {
            return "";
        }

        if (level.getBlockState(mismatch.pos().below()).is(TCBlocks.ARCANE_PEDESTAL.get())) {
            return " (pedestal is 1 block too low)";
        }
        if (level.getBlockState(mismatch.pos().above()).is(TCBlocks.ARCANE_PEDESTAL.get())) {
            return " (pedestal is 1 block too high)";
        }
        return " (place central pedestal here, 2 blocks below the matrix)";
    }

    private static Mismatch findClosestMismatch(Level level, BlockPos clickedPos) {
        Mismatch best = null;
        int bestScore = 0;
        for (int x = clickedPos.getX() - 2; x <= clickedPos.getX(); x++) {
            for (int y = clickedPos.getY() - 2; y <= clickedPos.getY(); y++) {
                for (int z = clickedPos.getZ() - 2; z <= clickedPos.getZ(); z++) {
                    BlockPos origin = new BlockPos(x, y, z);
                    CandidateCheck check = checkCandidate(level, origin);
                    if (check.score() > bestScore) {
                        bestScore = check.score();
                        best = check.mismatch();
                    }
                }
            }
        }
        return bestScore >= 3 ? best : null;
    }

    private static CandidateCheck checkCandidate(Level level, BlockPos origin) {
        int score = 0;
        Mismatch firstMismatch = null;
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockPos pos = fromBlueprint(origin, xx, yy, zz);
                    ExpectedBlock expected = expectedBlock(xx, yy, zz);
                    BlockState state = level.getBlockState(pos);
                    boolean matches = expected.matches(state);
                    if (matches) {
                        score++;
                    } else if (firstMismatch == null && expected.important()) {
                        firstMismatch = new Mismatch(pos, expected.name(), blockName(state));
                    }
                }
            }
        }
        return new CandidateCheck(score, firstMismatch);
    }

    private static boolean matches(Level level, BlockPos pos, int xx, int yy, int zz) {
        boolean center = xx == 1 && zz == 1;
        boolean corner = (xx == 0 || xx == 2) && (zz == 0 || zz == 2);
        BlockState state = level.getBlockState(pos);

        if (yy == 0) {
            return center ? state.is(TCBlocks.RUNIC_MATRIX.get()) : state.isAir();
        }
        if (yy == 1) {
            return corner ? state.is(TCBlocks.ARCANE_STONE.get()) : state.isAir();
        }
        if (center) {
            return state.is(TCBlocks.ARCANE_PEDESTAL.get());
        }
        return corner ? state.is(TCBlocks.ARCANE_STONE_BRICKS.get()) : state.isAir();
    }

    private static ExpectedBlock expectedBlock(int xx, int yy, int zz) {
        boolean center = xx == 1 && zz == 1;
        boolean corner = (xx == 0 || xx == 2) && (zz == 0 || zz == 2);
        if (yy == 0) {
            return center ? ExpectedBlock.block("thaumcraft:runic_matrix", TCBlocks.RUNIC_MATRIX.get())
                    : ExpectedBlock.air();
        }
        if (yy == 1) {
            return corner ? ExpectedBlock.block("thaumcraft:arcane_stone", TCBlocks.ARCANE_STONE.get())
                    : ExpectedBlock.air();
        }
        if (center) {
            return ExpectedBlock.block("thaumcraft:arcane_pedestal", TCBlocks.ARCANE_PEDESTAL.get());
        }
        return corner ? ExpectedBlock.block("thaumcraft:arcane_stone_bricks", TCBlocks.ARCANE_STONE_BRICKS.get())
                : ExpectedBlock.air();
    }

    private static String blockName(BlockState state) {
        return state.isAir() ? "minecraft:air" : state.getBlock().builtInRegistryHolder().key().location().toString();
    }

    private static void replace(Level level, BlockPos origin) {
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockPos pos = fromBlueprint(origin, xx, yy, zz);
                    boolean corner = (xx == 0 || xx == 2) && (zz == 0 || zz == 2);
                    if (yy == 1 && corner || yy == 2 && corner) {
                        level.setBlock(pos, pillarState(xx, yy, zz), 3);
                        sparkle(level, pos);
                    }
                }
            }
        }

        BlockPos matrixPos = fromBlueprint(origin, 1, 0, 1);
        if (level.getBlockEntity(matrixPos) instanceof RunicMatrixBlockEntity matrix) {
            matrix.setActive(true);
        } else {
            level.sendBlockUpdated(matrixPos, level.getBlockState(matrixPos), level.getBlockState(matrixPos), 3);
        }

        level.playSound(null, origin.getX() + 0.5D, origin.getY() + 0.5D, origin.getZ() + 0.5D,
                TCSoundEvents.WAND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void sparkle(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 0.5D,
                    pos.getZ() + 0.5D, 8, 0.35D, 0.35D, 0.35D, 0.1D);
        }
    }

    private static BlockPos fromBlueprint(BlockPos origin, int xx, int yy, int zz) {
        return origin.offset(xx, 2 - yy, zz);
    }

    private static BlockState pillarState(int xx, int yy, int zz) {
        return TCBlocks.INFUSION_PILLAR.get().defaultBlockState()
                .setValue(InfusionPillarBlock.CORNER, corner(xx, zz))
                .setValue(InfusionPillarBlock.TOP, yy == 1);
    }

    private static Corner corner(int xx, int zz) {
        if (xx == 0 && zz == 0) {
            return Corner.NORTH_WEST;
        }
        if (xx == 0 && zz == 2) {
            return Corner.SOUTH_WEST;
        }
        if (xx == 2 && zz == 0) {
            return Corner.NORTH_EAST;
        }
        return Corner.SOUTH_EAST;
    }

    private record CandidateCheck(int score, Mismatch mismatch) {
    }

    private record Mismatch(BlockPos pos, String expected, String found) {
    }

    private record ExpectedBlock(String name, Block block, boolean expectsAir) {
        private static ExpectedBlock block(String name, Block block) {
            return new ExpectedBlock(name, block, false);
        }

        private static ExpectedBlock air() {
            return new ExpectedBlock("minecraft:air", null, true);
        }

        private boolean matches(BlockState state) {
            return this.expectsAir ? state.isAir() : state.is(this.block);
        }

        private boolean important() {
            return !this.expectsAir;
        }
    }
}
