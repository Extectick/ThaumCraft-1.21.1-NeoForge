package thaumcraft.server;

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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blockentities.AlchemicalFurnaceBlockEntity;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.blockentities.EssentiaTubeBlockEntity;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.blockentities.RunicMatrixBlockEntity;
import thaumcraft.common.blockentities.WardedJarBlockEntity;
import thaumcraft.common.blocks.FluxBlock;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.network.CycleWandFocusPayload;
import thaumcraft.common.network.ResearchTableCombineAspectPayload;
import thaumcraft.common.network.ResearchTablePlaceAspectPayload;
import thaumcraft.common.network.ThaumonomiconCreateNotePayload;
import thaumcraft.common.services.ThaumcraftServerServices;
import thaumcraft.server.crafting.ServerArcaneWorktableService;
import thaumcraft.server.crafting.ServerObjectAspectRegistry;
import thaumcraft.server.essentia.ServerEssentiaService;
import thaumcraft.server.essentia.ServerEssentiaTransportService;
import thaumcraft.server.flux.ServerFluxService;
import thaumcraft.server.infusion.ServerInfusionRuntime;
import thaumcraft.server.events.RunicShieldEvents;
import thaumcraft.server.events.TCCommands;
import thaumcraft.server.network.TCServerPayloadHandler;
import thaumcraft.server.research.ServerResearchService;
import thaumcraft.server.wands.ServerWandService;
import thaumcraft.server.warp.ServerWarpService;

public final class ThaumcraftServerServicesProvider implements ThaumcraftServerServices {
    @Override
    public void registerServerEventHandlers(IEventBus gameEventBus) {
        gameEventBus.addListener(ServerObjectAspectRegistry::registerReloadListener);
        gameEventBus.addListener(TCCommands::register);
        gameEventBus.register(new RunicShieldEvents());
    }

    @Override
    public InteractionResultHolder<ItemStack> useWand(WandCastingItem wandItem, Level level, Player player,
            InteractionHand hand, ItemStack wand) {
        return ServerWandService.use(wandItem, level, player, hand, wand);
    }

    @Override
    public InteractionResult useWandOnAfterWandable(WandCastingItem wandItem, UseOnContext context) {
        return ServerWandService.useOnAfterWandable(wandItem, context);
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, boolean temporary) {
        ServerWarpService.addWarpToPlayer(player, amount, temporary);
    }

    @Override
    public void addStickyWarpToPlayer(Player player, int amount) {
        ServerWarpService.addStickyWarpToPlayer(player, amount);
    }

    @Override
    public boolean grantResearchTree(Player player, String key) {
        return ServerResearchService.grantResearchTree(player, key);
    }

    @Override
    public int grantAllResearch(Player player) {
        return ServerResearchService.grantAllResearch(player);
    }

    @Override
    public int resetResearch(Player player) {
        return ServerResearchService.resetResearch(player);
    }

    @Override
    public boolean tryCreateResearchNote(ServerPlayer player, String key) {
        return ServerResearchService.tryCreateResearchNote(player, key);
    }

    @Override
    public boolean completeResearch(Player player, String key) {
        return ServerResearchService.completeResearch(player, key);
    }

    @Override
    public void tickResearchTable(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity table) {
        ServerResearchService.tickResearchTable(level, pos, state, table);
    }

    @Override
    public void handleCycleWandFocus(CycleWandFocusPayload payload, ServerPlayer player) {
        TCServerPayloadHandler.handleCycleWandFocus(payload, player);
    }

    @Override
    public void handleResearchTablePlaceAspect(ResearchTablePlaceAspectPayload payload, ServerPlayer player) {
        TCServerPayloadHandler.handleResearchTablePlaceAspect(payload, player);
    }

    @Override
    public void handleResearchTableCombineAspect(ResearchTableCombineAspectPayload payload, ServerPlayer player) {
        TCServerPayloadHandler.handleResearchTableCombineAspect(payload, player);
    }

    @Override
    public void handleThaumonomiconCreateNote(ThaumonomiconCreateNotePayload payload, ServerPlayer player) {
        TCServerPayloadHandler.handleThaumonomiconCreateNote(payload, player);
    }

    @Override
    public boolean refreshRunicMatrixSurroundings(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        return ServerInfusionRuntime.refreshRunicMatrixSurroundings(matrix, level, pos);
    }

    @Override
    public void tickRunicMatrix(RunicMatrixBlockEntity matrix, Level level, BlockPos pos) {
        ServerInfusionRuntime.tickRunicMatrix(matrix, level, pos);
    }

    @Override
    public boolean drainEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return ServerEssentiaService.drainEssentia(level, targetPos, aspect, direction, range);
    }

    @Override
    public boolean findEssentia(Level level, BlockPos targetPos, Aspect aspect, Direction direction, int range) {
        return ServerEssentiaService.findEssentia(level, targetPos, aspect, direction, range);
    }

    @Override
    public void refreshEssentiaSources(Level level, BlockPos targetPos) {
        ServerEssentiaService.refreshSources(level, targetPos);
    }

    @Override
    public void tickTube(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        ServerEssentiaTransportService.tickTube(level, pos, state, tube);
    }

    @Override
    public void tickAlchemicalFurnace(Level level, BlockPos pos, BlockState state,
            AlchemicalFurnaceBlockEntity furnace) {
        ServerEssentiaTransportService.tickAlchemicalFurnace(level, pos, state, furnace);
    }

    @Override
    public void tickWardedJar(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
        ServerEssentiaTransportService.tickWardedJar(level, pos, state, jar);
    }

    @Override
    public int takeBufferEssentia(EssentiaTubeBlockEntity tube, Aspect aspect, int amount, Direction face) {
        return ServerEssentiaTransportService.takeBufferEssentia(tube, aspect, amount, face);
    }

    @Override
    public void tickFlux(FluxBlock block, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ServerFluxService.tickFlux(block, state, level, pos, random);
    }

    @Override
    public void fluxEntityInside(FluxBlock block, BlockState state, Level level, BlockPos pos, Entity entity) {
        ServerFluxService.entityInside(block, state, level, pos, entity);
    }

    @Override
    public void placeFlux(Level level, BlockPos pos, boolean gas, int metadata) {
        ServerFluxService.placeFlux(level, pos, gas, metadata);
    }

    @Override
    public void addFlux(Level level, BlockPos pos, boolean gas, int amount) {
        ServerFluxService.addFlux(level, pos, gas, amount);
    }

    @Override
    public boolean tryCraftArcaneWorktable(Level level, ArcaneWorktableBlockEntity worktable, ItemStack wand,
            Player player) {
        return ServerArcaneWorktableService.tryCraft(level, worktable, wand, player);
    }

    @Override
    public boolean tryCraftArcaneWorktable(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return ServerArcaneWorktableService.tryCraft(level, input, worktable, wand, player, recipe);
    }

    @Override
    public boolean tryConsumeArcaneCraft(Level level, CraftingInput input, Container worktable, ItemStack wand,
            Player player, ArcaneWorktableRecipe recipe) {
        return ServerArcaneWorktableService.tryConsumeArcaneCraft(level, input, worktable, wand, player, recipe);
    }

    @Override
    public boolean tryConsumeVanillaCraft(Level level, CraftingInput input, Container worktable, Player player,
            CraftingRecipe recipe) {
        return ServerArcaneWorktableService.tryConsumeVanillaCraft(level, input, worktable, player, recipe);
    }

    @Override
    public void registerObjectAspectReloadListener(AddReloadListenerEvent event) {
        ServerObjectAspectRegistry.registerReloadListener(event);
    }

    @Override
    public AspectList getObjectTags(ItemStack stack) {
        return ServerObjectAspectRegistry.getObjectTags(stack);
    }

    @Override
    public AspectList getObjectTagsWithBonus(ItemStack stack) {
        return ServerObjectAspectRegistry.getObjectTagsWithBonus(stack);
    }

    @Override
    public AspectList getBonusTags(ItemStack stack, AspectList sourceTags) {
        return ServerObjectAspectRegistry.getBonusTags(stack, sourceTags);
    }

    @Override
    public int itemAspectEntryCount() {
        return ServerObjectAspectRegistry.itemEntryCount();
    }

    @Override
    public int tagAspectEntryCount() {
        return ServerObjectAspectRegistry.tagEntryCount();
    }

    @Override
    public int generatedAspectEntryCount() {
        return ServerObjectAspectRegistry.generatedEntryCount();
    }
}
