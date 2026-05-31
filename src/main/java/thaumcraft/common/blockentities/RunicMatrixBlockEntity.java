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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.services.ClientServices;
import thaumcraft.common.util.ServerInfusionHooks;

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
    private AspectList recipeEssentia = AspectList.EMPTY;
    private AspectList recipeEssentiaBase = AspectList.EMPTY;
    private ResourceLocation recipeId;
    private String recipePlayer = "";
    private int recipeInstability;

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
            spawnCraftRunes(level, pos);
            spawnInstabilityBolt(level, pos, matrix.instability);
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

    private static void spawnCraftRunes(Level level, BlockPos pos) {
        ClientServices.get().blockRunes(level, pos);
    }

    private static void spawnInstabilityBolt(Level level, BlockPos pos, int instability) {
        if (instability <= 0) {
            return;
        }
        ClientServices.get().instabilityBolt(level, pos, instability);
    }

    private void serverTick(Level level, BlockPos pos) {
        ServerInfusionHooks.tickRunicMatrix(this, level, pos);
    }

    private void refreshSurroundings(Level level, BlockPos pos) {
        ServerInfusionHooks.refreshRunicMatrixSurroundings(this, level, pos);
    }

    public void applySurroundingsScan(boolean valid, int symmetry, List<BlockPos> pedestals) {
        boolean changed = this.structureValid != valid
                || this.symmetry != symmetry
                || !this.pedestals.equals(pedestals);
        this.structureValid = valid;
        this.symmetry = symmetry;
        this.pedestals = List.copyOf(pedestals);
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
        this.recipeEssentia = AspectList.load(tag, "recipeEssentia");
        this.recipeEssentiaBase = AspectList.load(tag, "recipeEssentiaBase");
        if (this.recipeEssentiaBase.isEmpty() && !this.recipeEssentia.isEmpty()) {
            this.recipeEssentiaBase = this.recipeEssentia.copy();
        }
        this.recipeInput = ItemStack.parseOptional(registries, tag.getCompound("recipeInput"));
        this.recipeOutput = ItemStack.parseOptional(registries, tag.getCompound("recipeOutput"));
        this.recipeIngredients = loadStacks(tag, registries);
        this.recipePlayer = tag.getString("recipePlayer");
        this.recipeInstability = tag.getInt("recipeInstability");
        if (tag.contains("recipeId")) {
            this.recipeId = ResourceLocation.tryParse(tag.getString("recipeId"));
        } else {
            this.recipeId = null;
        }
        this.countDelay = tag.getInt("countDelay");
        if (this.countDelay <= 0) {
            this.countDelay = 10;
        }
        this.itemCount = tag.getInt("itemCount");
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
        this.recipeEssentia.writeToNBT(tag, "recipeEssentia");
        this.recipeEssentiaBase.writeToNBT(tag, "recipeEssentiaBase");
        tag.put("recipeInput", this.recipeInput.saveOptional(registries));
        tag.put("recipeOutput", this.recipeOutput.saveOptional(registries));
        saveStacks(tag, this.recipeIngredients, registries);
        tag.putString("recipePlayer", this.recipePlayer);
        tag.putInt("recipeInstability", this.recipeInstability);
        if (this.recipeId != null) {
            tag.putString("recipeId", this.recipeId.toString());
        }
        tag.putInt("countDelay", this.countDelay);
        tag.putInt("itemCount", this.itemCount);
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

    public int getTickCount() {
        return this.tickCount;
    }

    public int getCountDelay() {
        return this.countDelay;
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

    public AspectList getRecipeEssentia() {
        return this.recipeEssentia.copy();
    }

    public AspectList getRecipeEssentiaBase() {
        return this.recipeEssentiaBase.copy();
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public String getRecipePlayer() {
        return this.recipePlayer;
    }

    public int getRecipeInstability() {
        return this.recipeInstability;
    }

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = Math.max(0, itemCount);
        this.markChangedAndSync();
    }

    public boolean consumeSurroundingsCheckRequest(int scanInterval) {
        if (this.checkSurroundings || this.tickCount % scanInterval == 0) {
            this.checkSurroundings = false;
            return true;
        }
        return false;
    }

    public void setRecipeEssentia(AspectList recipeEssentia) {
        this.recipeEssentia = recipeEssentia.copy();
        this.markChangedAndSync();
    }

    public void setRecipeEssentiaBase(AspectList recipeEssentiaBase) {
        this.recipeEssentiaBase = recipeEssentiaBase.copy();
        this.markChangedAndSync();
    }

    public void setRecipeIngredients(List<ItemStack> recipeIngredients) {
        this.recipeIngredients = recipeIngredients.stream().map(ItemStack::copy).toList();
        this.markChangedAndSync();
    }

    public void clearCraftingState() {
        this.crafting = false;
        this.recipeInput = ItemStack.EMPTY;
        this.recipeIngredients = List.of();
        this.recipeOutput = ItemStack.EMPTY;
        this.recipeEssentia = AspectList.EMPTY;
        this.recipeEssentiaBase = AspectList.EMPTY;
        this.recipeId = null;
        this.recipePlayer = "";
        this.recipeInstability = 0;
        this.itemCount = 0;
        this.markChangedAndSync();
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
        this.recipeEssentiaBase = this.recipeEssentia.copy();
        this.recipeId = recipeHolder.id();
        this.recipePlayer = player != null ? player.getGameProfile().getName() : "";
        this.recipeInstability = recipe.getInstability();
        this.instability = this.symmetry + this.recipeInstability;
        this.craftCount = 0;
        this.countDelay = 10;
        this.itemCount = 0;
        this.crafting = true;
        this.markChangedAndSync();
    }

    public void finishCrafting(Level level, ArcanePedestalBlockEntity centerPedestal) {
        centerPedestal.setStoredItem(this.recipeOutput.copy());
        this.instability = 0;
        this.clearCraftingState();
        level.playSound(null, this.worldPosition, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.5F, 1.25F);
    }

    public void failCrafting(Level level) {
        this.instability = 0;
        this.clearCraftingState();
        level.playSound(null, this.worldPosition, TCSoundEvents.CRAFTFAIL.get(), SoundSource.BLOCKS, 1.0F, 0.6F);
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

    public void markChangedAndSync() {
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

    private static List<ItemStack> loadStacks(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag stackTags = tag.getList("recipeIngredients", Tag.TAG_COMPOUND);
        List<ItemStack> loadedStacks = new ArrayList<>();
        for (int i = 0; i < stackTags.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, stackTags.getCompound(i));
            if (!stack.isEmpty()) {
                loadedStacks.add(stack);
            }
        }
        return List.copyOf(loadedStacks);
    }

    private static void saveStacks(CompoundTag tag, List<ItemStack> stacks, HolderLookup.Provider registries) {
        ListTag stackTags = new ListTag();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && stack.saveOptional(registries) instanceof CompoundTag stackTag) {
                stackTags.add(stackTag);
            }
        }
        tag.put("recipeIngredients", stackTags);
    }

}
