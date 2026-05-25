package thaumcraft.common.menus;

import java.util.Map;
import java.util.Optional;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.sounds.SoundSource;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blockentities.ResearchTableBlockEntity;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCMenuTypes;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.AspectPoolData;
import thaumcraft.common.research.ResearchNoteData;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchRegistry;

public class ResearchTableMenu extends AbstractContainerMenu {
    public static final int SCRIBING_TOOLS_MENU_SLOT = 0;
    public static final int NOTES_MENU_SLOT = 1;
    public static final int PLAYER_INV_SLOT_START = 2;
    public static final int PLAYER_INV_SLOT_END = 29;
    public static final int HOTBAR_SLOT_START = 29;
    public static final int HOTBAR_SLOT_END = 38;

    private final Container researchTable;
    private final Player player;
    private final ContainerData bonusAspects;

    public ResearchTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(ResearchTableBlockEntity.CONTAINER_SIZE),
                new SimpleContainerData(Aspect.values().length));
    }

    public ResearchTableMenu(int containerId, Inventory playerInventory, Container researchTable) {
        this(containerId, playerInventory, researchTable, new SimpleContainerData(Aspect.values().length));
    }

    public ResearchTableMenu(int containerId, Inventory playerInventory, Container researchTable,
            ContainerData bonusAspects) {
        super(TCMenuTypes.RESEARCH_TABLE.get(), containerId);
        checkContainerSize(researchTable, ResearchTableBlockEntity.CONTAINER_SIZE);
        this.researchTable = researchTable;
        this.player = playerInventory.player;
        this.bonusAspects = bonusAspects;
        researchTable.startOpen(playerInventory.player);
        this.addDataSlots(bonusAspects);

        this.addSlot(new Slot(researchTable, ResearchTableBlockEntity.SCRIBING_TOOLS_SLOT, 14, 10) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(TCItems.SCRIBING_TOOLS.get());
            }
        });
        this.addSlot(new Slot(researchTable, ResearchTableBlockEntity.NOTES_SLOT, 70, 10) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(TCItems.RESEARCH_NOTES.get());
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 48 + column * 18, 175 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 48 + column * 18, 233));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.researchTable.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            moved = stack.copy();

            if (index < PLAYER_INV_SLOT_START) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_SLOT_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(TCItems.SCRIBING_TOOLS.get())) {
                if (!this.moveItemStackTo(stack, SCRIBING_TOOLS_MENU_SLOT, SCRIBING_TOOLS_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(TCItems.RESEARCH_NOTES.get())) {
                if (!this.moveItemStackTo(stack, NOTES_MENU_SLOT, NOTES_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    public boolean placeAspect(Player player, HexUtils.Hex hex, Optional<Aspect> aspect) {
        Player actor = player != null ? player : this.player;
        ItemStack notes = this.researchTable.getItem(ResearchTableBlockEntity.NOTES_SLOT);
        if (!notes.is(TCItems.RESEARCH_NOTES.get())) {
            return false;
        }

        ResearchNoteData data = notes.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
        if (data.isEmpty()) {
            return false;
        }
        if (data.complete()) {
            return false;
        }
        if (actor != null && !actor.getAbilities().instabuild && !ResearchManager.canStart(actor, data.key())) {
            return false;
        }

        Map<String, ResearchNoteData.HexEntry> entries = data.entryMap();
        ResearchNoteData.HexEntry current = entries.get(hex.toString());
        if (current == null || current.type() == 1) {
            return false;
        }

        Aspect placingAspect = aspect.orElse(null);
        if (placingAspect != null && !this.hasAvailableAspect(actor, placingAspect, 1)) {
            return false;
        }

        if (!this.consumeInk()) {
            return false;
        }

        if (placingAspect != null) {
            this.consumeAvailableAspect(actor, placingAspect);
        }

        boolean wasComplete = data.complete();
        ResearchNoteData updated = aspect.isPresent()
                ? data.withEntry(hex, Optional.of(aspect.get()), 2)
                : data.withEntry(hex, Optional.empty(), 0);
        if (aspect.isPresent()) {
            updated = updated.tryComplete(placed -> actor == null || actor.getAbilities().instabuild
                    || actor.getData(TCDataAttachments.ASPECT_POOL).isDiscovered(placed));
        }
        if (!wasComplete && updated.complete()) {
            notes.set(DataComponents.RARITY, Rarity.EPIC);
            if (actor != null) {
                actor.level().playSound(null, actor.blockPosition(), TCSoundEvents.WRITE.get(), SoundSource.PLAYERS,
                        0.45F, 1.0F);
            }
        }
        notes.set(TCDataComponents.RESEARCH_NOTE, updated);
        this.researchTable.setChanged();
        this.slots.get(NOTES_MENU_SLOT).setChanged();
        this.broadcastChanges();
        return true;
    }

    public boolean combineAspects(Player player, Aspect first, Aspect second) {
        Player actor = player != null ? player : this.player;
        if (actor == null || first == null || second == null) {
            return false;
        }

        if (!actor.getAbilities().instabuild) {
            AspectPoolData pool = actor.getData(TCDataAttachments.ASPECT_POOL);
            if (!this.hasCombinationComponents(pool, first, second)) {
                return false;
            }
            pool = this.consumeAvailableAspect(pool, first);
            pool = this.consumeAvailableAspect(pool, second);
            Optional<Aspect> result = this.getCombinationResult(first, second);
            if (result.isPresent()) {
                pool = pool.learn(result.get(), 1);
            }
            actor.setData(TCDataAttachments.ASPECT_POOL, pool);
            actor.syncData(TCDataAttachments.ASPECT_POOL);
        }
        return true;
    }

    private boolean hasCombinationComponents(AspectPoolData pool, Aspect first, Aspect second) {
        if (first == second) {
            return this.getAvailableAspectAmount(pool, first) >= 2;
        }
        return this.getAvailableAspectAmount(pool, first) > 0 && this.getAvailableAspectAmount(pool, second) > 0;
    }

    private Optional<Aspect> getCombinationResult(Aspect first, Aspect second) {
        for (Aspect aspect : Aspect.getCompoundAspects()) {
            Aspect[] components = aspect.getComponents();
            if (components != null && components.length == 2
                    && (components[0] == first && components[1] == second
                            || components[0] == second && components[1] == first)) {
                return Optional.of(aspect);
            }
        }
        return Optional.empty();
    }

    public int getBonusAspectAmount(Aspect aspect) {
        return aspect == null ? 0 : this.bonusAspects.get(aspect.ordinal());
    }

    public int getAvailableAspectAmount(AspectPoolData pool, Aspect aspect) {
        return pool.get(aspect) + this.getBonusAspectAmount(aspect);
    }

    private boolean hasAvailableAspect(Player player, Aspect aspect, int amount) {
        if (player == null || player.getAbilities().instabuild) {
            return true;
        }
        return this.getAvailableAspectAmount(player.getData(TCDataAttachments.ASPECT_POOL), aspect) >= amount;
    }

    private void consumeAvailableAspect(Player player, Aspect aspect) {
        if (player == null || player.getAbilities().instabuild) {
            return;
        }
        AspectPoolData pool = this.consumeAvailableAspect(player.getData(TCDataAttachments.ASPECT_POOL), aspect);
        player.setData(TCDataAttachments.ASPECT_POOL, pool);
        player.syncData(TCDataAttachments.ASPECT_POOL);
    }

    private AspectPoolData consumeAvailableAspect(AspectPoolData pool, Aspect aspect) {
        if (this.researchTable instanceof ResearchTableBlockEntity table && table.consumeBonusAspect(aspect)) {
            return pool;
        }
        return pool.remove(aspect, 1);
    }

    private boolean consumeInk() {
        ItemStack tools = this.researchTable.getItem(ResearchTableBlockEntity.SCRIBING_TOOLS_SLOT);
        if (!tools.is(TCItems.SCRIBING_TOOLS.get())) {
            return false;
        }
        if (tools.isDamageableItem() && tools.getDamageValue() >= tools.getMaxDamage()) {
            return false;
        }

        if (tools.isDamageableItem()) {
            int damage = tools.getDamageValue() + 1;
            tools.setDamageValue(Math.min(damage, tools.getMaxDamage()));
            this.researchTable.setChanged();
            this.slots.get(SCRIBING_TOOLS_MENU_SLOT).setChanged();
        }
        return true;
    }
}
