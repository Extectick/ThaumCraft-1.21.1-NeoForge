package thaumcraft.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.PrimalVisStorage;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.lib.crafting.InfusionAltarScan;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;

public class RunicMatrixBlockEntity extends BlockEntity {
    private boolean active;
    private boolean crafting;
    private boolean checkSurroundings = true;
    private boolean structureValid;
    private int instability;
    private int symmetry;
    private int craftCount;
    private int tickCount;
    private int countDelay = 10;
    private int itemCount;
    private float startUp;
    private List<BlockPos> pedestals = List.of();
    private ItemStack recipeInput = ItemStack.EMPTY;
    private List<ItemStack> recipeIngredients = List.of();
    private ItemStack recipeOutput = ItemStack.EMPTY;
    private PrimalVisStorage recipeEssentia = PrimalVisStorage.EMPTY;
    private ResourceLocation recipeId;
    private String recipePlayer = "";

    public RunicMatrixBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.RUNIC_MATRIX.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RunicMatrixBlockEntity matrix) {
        matrix.tickCount++;
        if (!level.isClientSide) {
            matrix.serverTick(level, pos);
            return;
        }

        if (matrix.crafting) {
            if (matrix.craftCount == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), TCSoundEvents.INFUSERSTART.get(),
                        SoundSource.BLOCKS, 0.5F, 1.0F, false);
            } else if (matrix.craftCount % 65 == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), TCSoundEvents.INFUSER.get(),
                        SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
            matrix.craftCount++;
        } else if (matrix.craftCount > 0) {
            matrix.craftCount = Math.max(0, matrix.craftCount - 2);
            if (matrix.craftCount > 50) {
                matrix.craftCount = 50;
            }
        }

        if (matrix.active && matrix.startUp != 1.0F) {
            matrix.startUp += Math.max(matrix.startUp / 10.0F, 0.001F);
            if (matrix.startUp > 0.999F) {
                matrix.startUp = 1.0F;
            }
        }

        if (!matrix.active && matrix.startUp > 0.0F) {
            matrix.startUp -= matrix.startUp / 10.0F;
            if (matrix.startUp < 0.001F) {
                matrix.startUp = 0.0F;
            }
        }
    }

    private void serverTick(Level level, BlockPos pos) {
        int scanInterval = this.crafting ? 20 : 100;
        boolean scanned = false;
        if (this.checkSurroundings || this.tickCount % scanInterval == 0) {
            this.checkSurroundings = false;
            this.refreshSurroundings(level, pos);
            scanned = true;
        }

        if (this.active && scanned && !this.structureValid) {
            this.setActive(false);
            return;
        }

        if (this.active && this.crafting && this.tickCount % this.countDelay == 0) {
            this.craftCycle(level);
        }
    }

    private void refreshSurroundings(Level level, BlockPos pos) {
        InfusionAltarScan scan = InfusionAltarScan.scan(level, pos);
        boolean changed = this.structureValid != scan.valid()
                || this.symmetry != scan.symmetry()
                || !this.pedestals.equals(scan.pedestals());
        this.structureValid = scan.valid();
        this.symmetry = scan.symmetry();
        this.pedestals = scan.pedestals();
        if (changed) {
            this.markChangedAndSync();
        }
    }

    public boolean refreshSurroundingsNow() {
        if (this.level == null) {
            return false;
        }
        this.checkSurroundings = false;
        this.refreshSurroundings(this.level, this.worldPosition);
        return this.structureValid;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.active = tag.getBoolean("active");
        this.crafting = tag.getBoolean("crafting");
        this.structureValid = tag.getBoolean("structureValid");
        this.instability = tag.getInt("instability");
        this.symmetry = tag.getInt("symmetry");
        this.tickCount = tag.getInt("tickCount");
        this.pedestals = loadPedestals(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", this.active);
        tag.putBoolean("crafting", this.crafting);
        tag.putBoolean("structureValid", this.structureValid);
        tag.putInt("instability", this.instability);
        tag.putInt("symmetry", this.symmetry);
        tag.putInt("tickCount", this.tickCount);
        savePedestals(tag, this.pedestals);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isCrafting() {
        return this.crafting;
    }

    public int getInstability() {
        return this.instability;
    }

    public boolean isStructureValid() {
        return this.structureValid;
    }

    public int getSymmetry() {
        return this.symmetry;
    }

    public int getCraftCount() {
        return this.craftCount;
    }

    public float getStartUp() {
        return this.startUp;
    }

    public ItemStack getRecipeInput() {
        return this.recipeInput;
    }

    public List<ItemStack> getRecipeIngredients() {
        return this.recipeIngredients;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public PrimalVisStorage getRecipeEssentia() {
        return this.recipeEssentia;
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public String getRecipePlayer() {
        return this.recipePlayer;
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            if (!active) {
                this.crafting = false;
            }
            this.markChangedAndSync();
        }
    }

    public void setCrafting(boolean crafting) {
        if (this.crafting != crafting) {
            this.crafting = crafting;
            this.markChangedAndSync();
        }
    }

    public void startCrafting(Player player, RecipeHolder<InfusionRecipe> recipeHolder, InfusionRecipe.Input input) {
        InfusionRecipe recipe = recipeHolder.value();
        this.recipeInput = input.catalyst().copy();
        this.recipeIngredients = input.components().stream().map(ItemStack::copy).toList();
        this.recipeOutput = recipe.assemble(input, this.level.registryAccess()).copy();
        this.recipeEssentia = recipe.getEssentia();
        this.recipeId = recipeHolder.id();
        this.recipePlayer = player != null ? player.getGameProfile().getName() : "";
        this.instability = this.symmetry + recipe.getInstability();
        this.craftCount = 0;
        this.countDelay = 10;
        this.itemCount = 0;
        this.crafting = true;
        this.markChangedAndSync();
    }

    private void craftCycle(Level level) {
        if (!(level.getBlockEntity(this.worldPosition.below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)
                || !ItemStack.isSameItemSameComponents(centerPedestal.getStoredItem(), this.recipeInput)) {
            this.failCrafting(level);
            return;
        }

        if (this.hasRemainingEssentia()) {
            // Essentia drain is the next porting layer. Keep the slot in the cycle so recipe data is already shaped
            // like old Thaumcraft, but don't block recipes that do not yet define essentia costs.
            this.recipeEssentia = PrimalVisStorage.EMPTY;
            this.markChangedAndSync();
            return;
        }

        if (this.recipeIngredients.isEmpty()) {
            this.finishCrafting(level, centerPedestal);
            return;
        }

        if (this.consumeNextIngredient(level)) {
            return;
        }

        this.failCrafting(level);
    }

    private boolean hasRemainingEssentia() {
        return !this.recipeEssentia.isEmpty();
    }

    private boolean consumeNextIngredient(Level level) {
        ItemStack required = this.recipeIngredients.get(0);
        if (this.itemCount > 0) {
            this.itemCount--;
            return true;
        }

        for (BlockPos pedestalPos : this.pedestals) {
            if (level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal) {
                ItemStack stored = pedestal.getStoredItem();
                if (!stored.isEmpty() && ItemStack.isSameItemSameComponents(stored, required)) {
                    pedestal.setStoredItem(ItemStack.EMPTY);
                    this.recipeIngredients = this.recipeIngredients.stream().skip(1).map(ItemStack::copy).toList();
                    this.itemCount = 5;
                    this.markChangedAndSync();
                    return true;
                }
            }
        }
        return false;
    }

    private void finishCrafting(Level level, ArcanePedestalBlockEntity centerPedestal) {
        centerPedestal.setStoredItem(this.recipeOutput.copy());
        this.instability = 0;
        this.crafting = false;
        this.recipeInput = ItemStack.EMPTY;
        this.recipeIngredients = List.of();
        this.recipeOutput = ItemStack.EMPTY;
        this.recipeEssentia = PrimalVisStorage.EMPTY;
        this.recipeId = null;
        this.recipePlayer = "";
        this.itemCount = 0;
        level.playSound(null, this.worldPosition, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.5F, 1.25F);
        this.markChangedAndSync();
    }

    private void failCrafting(Level level) {
        this.instability = 0;
        this.crafting = false;
        this.recipeEssentia = PrimalVisStorage.EMPTY;
        this.recipeIngredients = List.of();
        this.recipeOutput = ItemStack.EMPTY;
        this.recipeId = null;
        this.itemCount = 0;
        level.playSound(null, this.worldPosition, TCSoundEvents.CRAFTFAIL.get(), SoundSource.BLOCKS, 1.0F, 0.6F);
        this.markChangedAndSync();
    }

    public void setInstability(int instability) {
        this.instability = Math.max(0, instability);
        this.markChangedAndSync();
    }

    public List<BlockPos> getPedestals() {
        return this.pedestals;
    }

    public void requestSurroundingsCheck() {
        this.checkSurroundings = true;
    }

    private void markChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    private static List<BlockPos> loadPedestals(CompoundTag tag) {
        ListTag pedestalTags = tag.getList("pedestals", Tag.TAG_COMPOUND);
        List<BlockPos> loadedPedestals = new ArrayList<>();
        for (int i = 0; i < pedestalTags.size(); i++) {
            CompoundTag pedestalTag = pedestalTags.getCompound(i);
            loadedPedestals.add(new BlockPos(pedestalTag.getInt("x"), pedestalTag.getInt("y"),
                    pedestalTag.getInt("z")));
        }
        return List.copyOf(loadedPedestals);
    }

    private static void savePedestals(CompoundTag tag, List<BlockPos> pedestals) {
        ListTag pedestalTags = new ListTag();
        for (BlockPos pedestalPos : pedestals) {
            CompoundTag pedestalTag = new CompoundTag();
            pedestalTag.putInt("x", pedestalPos.getX());
            pedestalTag.putInt("y", pedestalPos.getY());
            pedestalTag.putInt("z", pedestalPos.getZ());
            pedestalTags.add(pedestalTag);
        }
        tag.put("pedestals", pedestalTags);
    }
}
