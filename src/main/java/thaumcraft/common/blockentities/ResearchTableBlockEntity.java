package thaumcraft.common.blockentities;

import java.util.EnumMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.menus.ResearchTableMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.util.ServerResearchHooks;

public class ResearchTableBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SCRIBING_TOOLS_SLOT = 0;
    public static final int NOTES_SLOT = 1;
    public static final int CONTAINER_SIZE = 2;
    public static final int BONUS_RECALC_INTERVAL = 600;
    public static final int BONUS_SCAN_RANGE = 8;

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final EnumMap<Aspect, Integer> bonusAspects = new EnumMap<>(Aspect.class);
    private int nextRecalc;
    private final ContainerData bonusDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            Aspect[] aspects = Aspect.values();
            return index >= 0 && index < aspects.length ? ResearchTableBlockEntity.this.getBonusAspect(aspects[index]) : 0;
        }

        @Override
        public void set(int index, int value) {
            Aspect[] aspects = Aspect.values();
            if (index >= 0 && index < aspects.length) {
                ResearchTableBlockEntity.this.setBonusAspect(aspects[index], value);
            }
        }

        @Override
        public int getCount() {
            return Aspect.values().length;
        }
    };

    public ResearchTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.RESEARCH_TABLE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.nextRecalc = tag.getInt("nextRecalc");
        this.bonusAspects.clear();
        ListTag bonusList = tag.getList("bonusAspects", Tag.TAG_COMPOUND);
        for (int i = 0; i < bonusList.size(); i++) {
            CompoundTag bonusTag = bonusList.getCompound(i);
            Aspect.byTag(bonusTag.getString("tag")).ifPresent(aspect -> this.setBonusAspect(aspect,
                    bonusTag.getInt("amount")));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("nextRecalc", this.nextRecalc);
        ListTag bonusList = new ListTag();
        for (Aspect aspect : Aspect.values()) {
            int amount = this.getBonusAspect(aspect);
            if (amount > 0) {
                CompoundTag bonusTag = new CompoundTag();
                bonusTag.putString("tag", aspect.getTag());
                bonusTag.putInt("amount", amount);
                bonusList.add(bonusTag);
            }
        }
        tag.put("bonusAspects", bonusList);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.research_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ResearchTableMenu(containerId, playerInventory, this, this.bonusDataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity table) {
        ServerResearchHooks.tickResearchTable(level, pos, state, table);
    }

    public int getBonusAspect(Aspect aspect) {
        return this.bonusAspects.getOrDefault(aspect, 0);
    }

    public boolean hasBonusAspect(Aspect aspect) {
        return this.getBonusAspect(aspect) > 0;
    }

    public boolean consumeBonusAspect(Aspect aspect) {
        int amount = this.getBonusAspect(aspect);
        if (amount <= 0) {
            return false;
        }
        this.setBonusAspect(aspect, amount - 1);
        this.setChanged();
        return true;
    }

    public void addBonusAspect(Aspect aspect) {
        this.setBonusAspect(aspect, Math.min(1, this.getBonusAspect(aspect) + 1));
    }

    public void setBonusAspect(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            this.bonusAspects.remove(aspect);
        } else {
            this.bonusAspects.put(aspect, Math.min(1, amount));
        }
    }

    public int incrementNextRecalc() {
        return ++this.nextRecalc;
    }

    public void resetNextRecalc() {
        this.nextRecalc = 0;
    }

    public void markChangedAndSync(Level level, BlockPos pos, BlockState state) {
        this.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize(stack)) {
            stack.setCount(this.getMaxStackSize(stack));
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case SCRIBING_TOOLS_SLOT -> stack.is(TCItems.SCRIBING_TOOLS.get());
            case NOTES_SLOT -> stack.is(TCItems.RESEARCH_NOTES.get());
            default -> false;
        };
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
}
