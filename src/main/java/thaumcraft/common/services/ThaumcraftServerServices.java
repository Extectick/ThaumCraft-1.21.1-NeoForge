package thaumcraft.common.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.IEventBus;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blockentities.CrucibleBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.network.CycleWandFocusPayload;
import thaumcraft.common.network.ResearchTableCombineAspectPayload;
import thaumcraft.common.network.ResearchTablePlaceAspectPayload;
import thaumcraft.common.network.ThaumonomiconCreateNotePayload;
import net.minecraft.world.phys.BlockHitResult;

public interface ThaumcraftServerServices {
    final class Empty implements ThaumcraftServerServices {
    }

    default void registerServerEventHandlers(IEventBus gameEventBus) {
    }

    default InteractionResultHolder<ItemStack> useWand(WandCastingItem wandItem, Level level, Player player,
            InteractionHand hand, ItemStack wand) {
        return InteractionResultHolder.pass(wand);
    }

    default InteractionResult useWandOnAfterWandable(WandCastingItem wandItem, UseOnContext context) {
        return InteractionResult.PASS;
    }

    default void startAuraNodeTap(ServerPlayer player, InteractionHand hand, BlockPos nodePos) {
    }

    default void tickAuraNodeTap(ServerPlayer player, ItemStack wand, int remainingUseDuration) {
    }

    default void stopAuraNodeTap(ServerPlayer player) {
    }

    default InteractionResult startThaumometerScan(Player player, InteractionHand hand) {
        return InteractionResult.CONSUME;
    }

    default InteractionResult startThaumometerBlockScan(Player player, InteractionHand hand,
            BlockHitResult hitResult) {
        return InteractionResult.CONSUME;
    }

    default void addWarpToPlayer(Player player, int amount, boolean temporary) {
    }

    default void addStickyWarpToPlayer(Player player, int amount) {
    }

    default boolean grantResearchTree(Player player, String key) {
        return false;
    }

    default int grantAllResearch(Player player) {
        return 0;
    }

    default int resetResearch(Player player) {
        return 0;
    }

    default boolean tryCreateResearchNote(ServerPlayer player, String key) {
        return false;
    }

    default boolean completeResearch(Player player, String key) {
        return false;
    }

    default void tickResearchTable(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity table) {
    }

    default void handleCycleWandFocus(CycleWandFocusPayload payload, ServerPlayer player) {
    }

    default void handleResearchTablePlaceAspect(ResearchTablePlaceAspectPayload payload, ServerPlayer player) {
    }

    default void handleResearchTableCombineAspect(ResearchTableCombineAspectPayload payload, ServerPlayer player) {
    }

    default void handleThaumonomiconCreateNote(ThaumonomiconCreateNotePayload payload, ServerPlayer player) {
    }

    default boolean refreshRunicMatrixSurroundings(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        return false;
    }

    default void tickRunicMatrix(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        matrix.setActive(false);
    }

    default boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return false;
    }

    default boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return false;
    }

    default void refreshEssentiaSources(Level level, BlockPos targetPos) {
    }

    default void tickTube(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
    }

    default void tickAlchemicalFurnace(Level level, BlockPos pos, BlockState state,
            AlchemicalFurnaceBlockEntity furnace) {
    }

    default void tickCrucible(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
    }

    default void crucibleEntityInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible,
            Entity entity) {
    }

    default void spillCrucibleRemnants(Level level, BlockPos pos, CrucibleBlockEntity crucible) {
    }

    default void tickWardedJar(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
    }

    default int takeBufferEssentia(EssentiaTubeBlockEntity tube, Aspect aspect, int amount, Direction face) {
        return 0;
    }

    default void tickFlux(FluxBlock block, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    }

    default void fluxEntityInside(FluxBlock block, BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    default void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
    }

    default void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
    }

    default boolean tryCraftArcaneWorktable(Level level, ArcaneWorktableBlockEntity worktable, ItemStack wand,
            Player player) {
        return false;
    }

    default boolean tryCraftArcaneWorktable(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return false;
    }

    default boolean tryConsumeArcaneCraft(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return false;
    }

    default boolean tryConsumeVanillaCraft(Level level, CraftingInput input, Container worktable, Player player,
            CraftingRecipe recipe) {
        return false;
    }

    default void registerObjectAspectReloadListener(AddReloadListenerEvent event) {
    }

    default AspectList getObjectTags(ItemStack stack) {
        return AspectList.EMPTY;
    }

    default AspectList getObjectTagsWithBonus(ItemStack stack) {
        return AspectList.EMPTY;
    }

    default AspectList getBonusTags(ItemStack stack, AspectList sourceTags) {
        return AspectList.EMPTY;
    }

    default int itemAspectEntryCount() {
        return 0;
    }

    default int tagAspectEntryCount() {
        return 0;
    }

    default int generatedAspectEntryCount() {
        return 0;
    }

    default String objectAspectSource(ItemStack stack) {
        return "none";
    }
}
