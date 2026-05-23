package thaumcraft.common.menus;

import java.util.Optional;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import thaumcraft.common.blockentities.ArcaneWorktableBlockEntity;
import thaumcraft.common.crafting.ArcaneWorktableRecipe;
import thaumcraft.common.items.wands.WandCastingItem;
import thaumcraft.common.lib.crafting.ArcaneWorktableRecipes;
import thaumcraft.common.registry.TCMenuTypes;

public class ArcaneWorktableMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT = 0;
    public static final int WAND_MENU_SLOT = 1;
    public static final int GRID_SLOT_START = 2;
    public static final int GRID_SLOT_END = 11;
    public static final int PLAYER_INV_SLOT_START = 11;
    public static final int PLAYER_INV_SLOT_END = 38;
    public static final int HOTBAR_SLOT_START = 38;
    public static final int HOTBAR_SLOT_END = 47;

    private final Container worktable;
    private final ResultContainer result = new ResultContainer();
    private final Player player;
    private Optional<RecipeHolder<CraftingRecipe>> selectedVanillaRecipe = Optional.empty();
    private Optional<RecipeHolder<ArcaneWorktableRecipe>> selectedArcaneRecipe = Optional.empty();

    public ArcaneWorktableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(ArcaneWorktableBlockEntity.CONTAINER_SIZE));
    }

    public ArcaneWorktableMenu(int containerId, Inventory playerInventory, Container worktable) {
        super(TCMenuTypes.ARCANE_WORKTABLE.get(), containerId);
        checkContainerSize(worktable, ArcaneWorktableBlockEntity.CONTAINER_SIZE);
        this.worktable = worktable;
        this.player = playerInventory.player;
        worktable.startOpen(playerInventory.player);

        this.addSlot(new ArcaneWorktableResultSlot(this, this.result, 0, 160, 64));

        this.addSlot(new Slot(worktable, ArcaneWorktableBlockEntity.WAND_SLOT, 160, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof WandCastingItem;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new Slot(worktable, column + row * 3, 40 + column * 24, 40 + row * 24));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 16 + column * 18, 151 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 16 + column * 18, 209));
        }

        this.updateResult();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.worktable.stillValid(player);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        this.updateResult();
    }

    public boolean canTakeResult() {
        return !this.result.getItem(0).isEmpty() && (this.selectedVanillaRecipe.isPresent()
                || this.selectedArcaneRecipe
                        .filter(recipe -> ArcaneWorktableRecipes.hasPrimalCost(this.getWand(), recipe.value()))
                        .isPresent());
    }

    public void onTakeResult(Player player, ItemStack stack) {
        this.selectedVanillaRecipe.ifPresentOrElse(recipe -> {
            if (ArcaneWorktableRecipes.tryConsumeVanillaCraft(this.player.level(), this.asArcaneCraftingInput(),
                    this.worktable, player, recipe.value())) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
            }
        }, () -> this.selectedArcaneRecipe.ifPresent(recipe -> {
            if (ArcaneWorktableRecipes.tryConsumeArcaneCraft(this.player.level(), this.asArcaneCraftingInput(),
                    this.worktable, this.getWand(), player, recipe.value())) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
            }
        }));
        this.updateResult();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            moved = stack.copy();
            if (index == RESULT_SLOT) {
                if (!this.canTakeResult()) {
                    return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(stack, PLAYER_INV_SLOT_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, moved);
            } else if (index == WAND_MENU_SLOT || index >= GRID_SLOT_START && index < GRID_SLOT_END) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_SLOT_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() instanceof WandCastingItem) {
                if (!this.moveItemStackTo(stack, WAND_MENU_SLOT, WAND_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, GRID_SLOT_START, GRID_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return moved;
    }

    private void updateResult() {
        Level level = this.player.level();
        if (level.isClientSide) {
            return;
        }

        CraftingInput input = this.asArcaneCraftingInput();
        this.selectedVanillaRecipe = ArcaneWorktableRecipes.findVanillaRecipe(level, input);
        this.selectedArcaneRecipe = this.selectedVanillaRecipe.isEmpty() ? ArcaneWorktableRecipes.findRecipe(level, input)
                : Optional.empty();
        ItemStack resultStack = this.selectedVanillaRecipe
                .map(recipe -> recipe.value().assemble(input, level.registryAccess()))
                .or(() -> this.selectedArcaneRecipe.map(recipe -> recipe.value().assemble(input, level.registryAccess())))
                .orElse(ItemStack.EMPTY);
        this.result.setRecipeUsed(this.selectedVanillaRecipe
                .<RecipeHolder<?>>map(recipe -> recipe)
                .or(() -> this.selectedArcaneRecipe.map(recipe -> recipe))
                .orElse(null));
        this.result.setItem(0, resultStack);
        this.setRemoteSlot(RESULT_SLOT, resultStack);
        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(),
                    RESULT_SLOT, resultStack));
        }
    }

    private CraftingInput asArcaneCraftingInput() {
        return ArcaneWorktableRecipes.createInput(this.worktable).input();
    }

    public Optional<RecipeHolder<ArcaneWorktableRecipe>> getSelectedRecipeForDisplay() {
        CraftingInput input = this.asArcaneCraftingInput();
        return ArcaneWorktableRecipes.findVanillaRecipe(this.player.level(), input).isPresent()
                ? Optional.empty()
                : ArcaneWorktableRecipes.findRecipe(this.player.level(), input);
    }

    public boolean isArcaneResultBlockedByVis() {
        return this.getSelectedRecipeForDisplay()
                .filter(recipe -> !ArcaneWorktableRecipes.hasPrimalCost(this.getWand(), recipe.value()))
                .isPresent();
    }

    public ItemStack getWandStack() {
        return this.getWand();
    }

    private ItemStack getWand() {
        return this.worktable.getItem(ArcaneWorktableBlockEntity.WAND_SLOT);
    }
}
