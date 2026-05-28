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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.InfusionRecipe;
import thaumcraft.common.lib.crafting.InfusionAltarScan;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.network.BlockZapFxPayload;
import thaumcraft.common.network.InfusionSourceFxPayload;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCRecipeTypes;
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
        double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.8D;
        double y = pos.getY() - 1.5D + level.random.nextDouble() * 0.6D;
        double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.8D;
        level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0.0D, -0.03D, 0.0D);
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

    private void craftCycle(Level level) {
        boolean valid = isCenterInputValid(level);

        if (!valid || shouldTriggerInstabilityEvent(level)) {
            this.triggerInstabilityEvent(level);
            if (valid) {
                return;
            }
        }

        if (!valid) {
            this.failCrafting(level);
            return;
        }

        if (!(level.getBlockEntity(this.worldPosition.below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)) {
            this.failCrafting(level);
            return;
        }

        this.ensureRecipeEssentiaBase(level);

        if (this.hasRemainingEssentia()) {
            this.drainNextEssentia(level);
            return;
        }

        if (this.recipeIngredients.isEmpty()) {
            this.finishCrafting(level, centerPedestal);
            return;
        }

        if (this.consumeNextIngredient(level)) {
            return;
        }

        this.addMissingIngredientInstability(level);
    }

    private boolean isCenterInputValid(Level level) {
        if (!(level.getBlockEntity(this.worldPosition.below(2)) instanceof ArcanePedestalBlockEntity centerPedestal)) {
            return false;
        }
        ItemStack stack = centerPedestal.getStoredItem();
        return !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, this.recipeInput);
    }

    private boolean shouldTriggerInstabilityEvent(Level level) {
        return this.instability > 0 && level.random.nextInt(500) <= this.instability;
    }

    private boolean hasRemainingEssentia() {
        return this.recipeEssentia.visSize() > 0;
    }

    private void ensureRecipeEssentiaBase(Level level) {
        if (!this.recipeEssentiaBase.isEmpty() || this.recipeId == null) {
            return;
        }
        level.getRecipeManager()
                .getAllRecipesFor(TCRecipeTypes.INFUSION.get())
                .stream()
                .filter(holder -> holder.id().equals(this.recipeId))
                .findFirst()
                .ifPresent(holder -> {
                    this.recipeEssentiaBase = holder.value().getEssentia();
                    this.markChangedAndSync();
                });
    }

    private void drainNextEssentia(Level level) {
        boolean attemptedDrain = false;
        for (Aspect aspect : this.recipeEssentia.getAspects()) {
            if (this.recipeEssentia.getAmount(aspect) <= 0) {
                continue;
            }
            if (EssentiaHandler.drainEssentia(level, this.worldPosition, aspect, 12)) {
                this.recipeEssentia = this.recipeEssentia.copy().remove(aspect, 1);
                this.markChangedAndSync();
                this.checkSurroundings = true;
                return;
            } else {
                attemptedDrain = true;
                int instabilityBound = Math.max(1, 100 - this.recipeInstability * 3);
                if (level.random.nextInt(instabilityBound) == 0) {
                    this.instability = Math.min(25, this.instability + 1);
                }
            }
        }
        this.checkSurroundings = true;
        if (attemptedDrain) {
            this.markChangedAndSync();
        }
    }

    private boolean consumeNextIngredient(Level level) {
        for (int ingredientIndex = 0; ingredientIndex < this.recipeIngredients.size(); ingredientIndex++) {
            ItemStack required = this.recipeIngredients.get(ingredientIndex);
            for (BlockPos pedestalPos : this.pedestals) {
                if (level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal) {
                    ItemStack stored = pedestal.getStoredItem();
                    if (!stored.isEmpty() && ItemStack.isSameItemSameComponents(stored, required)) {
                        if (this.itemCount == 0) {
                            this.itemCount = 5;
                            this.sendInfusionSourceFx(level, pedestalPos, 60, 0);
                            this.markChangedAndSync();
                        } else if (this.itemCount-- <= 1) {
                            pedestal.setStoredItem(getCraftingRemainingItem(stored));
                            List<ItemStack> remaining = new ArrayList<>(this.recipeIngredients);
                            remaining.remove(ingredientIndex);
                            this.recipeIngredients = remaining.stream().map(ItemStack::copy).toList();
                            this.markChangedAndSync();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addMissingIngredientInstability(Level level) {
        List<Aspect> aspects = this.recipeEssentiaBase.getAspects();
        if (aspects.isEmpty()) {
            return;
        }

        for (int ingredientIndex = 0; ingredientIndex < this.recipeIngredients.size(); ingredientIndex++) {
            if (level.random.nextInt(1 + ingredientIndex) == 0) {
                Aspect aspect = aspects.get(level.random.nextInt(aspects.size()));
                this.recipeEssentia = this.recipeEssentia.copy().add(aspect, 1);
                if (level.random.nextInt(Math.max(1, 50 - this.recipeInstability * 2)) == 0) {
                    this.instability = Math.min(25, this.instability + 1);
                }
                this.markChangedAndSync();
            }
        }
    }

    private void finishCrafting(Level level, ArcanePedestalBlockEntity centerPedestal) {
        centerPedestal.setStoredItem(this.recipeOutput.copy());
        this.instability = 0;
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
        level.playSound(null, this.worldPosition, TCSoundEvents.CRAFTSTART.get(), SoundSource.BLOCKS, 0.5F, 1.25F);
        this.markChangedAndSync();
    }

    private void failCrafting(Level level) {
        this.instability = 0;
        this.crafting = false;
        this.recipeEssentia = AspectList.EMPTY;
        this.recipeEssentiaBase = AspectList.EMPTY;
        this.recipeIngredients = List.of();
        this.recipeOutput = ItemStack.EMPTY;
        this.recipeId = null;
        this.recipeInput = ItemStack.EMPTY;
        this.recipePlayer = "";
        this.recipeInstability = 0;
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

    private static ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem()) {
            return new ItemStack(stack.getItem().getCraftingRemainingItem());
        }
        return ItemStack.EMPTY;
    }

    private void triggerInstabilityEvent(Level level) {
        switch (level.random.nextInt(21)) {
            case 0, 2, 10, 13 -> this.instabilityEjectItem(level, 0);
            case 1, 11 -> this.instabilityEjectItem(level, 2);
            case 3, 8, 14 -> this.instabilityZap(level, false);
            case 4, 15 -> this.instabilityEjectItem(level, 5);
            case 5, 16 -> this.instabilityHarm(level, false);
            case 6, 17 -> this.instabilityEjectItem(level, 1);
            case 7 -> this.instabilityEjectItem(level, 4);
            case 9 -> level.explode(null, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D,
                    this.worldPosition.getZ() + 0.5D, 1.5F + level.random.nextFloat(), Level.ExplosionInteraction.NONE);
            case 12 -> this.instabilityZap(level, true);
            case 18 -> this.instabilityHarm(level, true);
            case 19 -> this.instabilityEjectItem(level, 3);
            case 20 -> this.instabilityWarp(level);
            default -> {
            }
        }
    }

    private void instabilityZap(Level level, boolean all) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, effectBounds());
        for (LivingEntity target : targets) {
            sendBlockZapFx(level, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D,
                    this.worldPosition.getZ() + 0.5D, target.getX(), target.getY() + target.getBbHeight() / 2.0D,
                    target.getZ());
            DamageSource source = level.damageSources().magic();
            target.hurt(source, 4 + level.random.nextInt(4));
            if (!all) {
                break;
            }
        }
    }

    private void instabilityHarm(Level level, boolean all) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, effectBounds());
        for (LivingEntity target : targets) {
            if (level.random.nextBoolean()) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0, false, true));
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, 0, true, true));
            }
            if (!all) {
                break;
            }
        }
    }

    private void instabilityWarp(Level level) {
        List<Player> targets = level.getEntitiesOfClass(Player.class, effectBounds());
        if (!targets.isEmpty()) {
            Player target = targets.get(level.random.nextInt(targets.size()));
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200 + level.random.nextInt(200), 0, true,
                    true));
        }
    }

    private void instabilityEjectItem(Level level, int type) {
        for (int attempt = 0; attempt < 50 && !this.pedestals.isEmpty(); attempt++) {
            BlockPos pedestalPos = this.pedestals.get(level.random.nextInt(this.pedestals.size()));
            if (!(level.getBlockEntity(pedestalPos) instanceof ArcanePedestalBlockEntity pedestal)) {
                continue;
            }

            ItemStack stack = pedestal.getStoredItem();
            if (stack.isEmpty()) {
                continue;
            }

            if (type >= 3 && type != 5) {
                pedestal.setStoredItem(ItemStack.EMPTY);
            } else {
                pedestal.setStoredItem(ItemStack.EMPTY);
                Containers.dropItemStack(level, pedestalPos.getX(), pedestalPos.getY() + 1.0D, pedestalPos.getZ(),
                        stack.copy());
            }

            if (type == 1 || type == 3) {
                level.playSound(null, pedestalPos, TCSoundEvents.SPILL.get(), SoundSource.BLOCKS, 0.3F, 1.0F);
            } else if (type == 2 || type == 4) {
                level.playSound(null, pedestalPos, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS, 0.3F, 1.0F);
            } else if (type == 5) {
                level.explode(null, pedestalPos.getX() + 0.5D, pedestalPos.getY() + 0.5D, pedestalPos.getZ() + 0.5D,
                        1.0F, Level.ExplosionInteraction.NONE);
            }

            sendBlockZapFx(level, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D,
                    this.worldPosition.getZ() + 0.5D, pedestalPos.getX() + 0.5D, pedestalPos.getY() + 1.5D,
                    pedestalPos.getZ() + 0.5D);
            return;
        }
    }

    private AABB effectBounds() {
        return new AABB(this.worldPosition).inflate(10.0D);
    }

    private void sendInfusionSourceFx(Level level, BlockPos source, int ticks, int color) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, this.worldPosition.getX(), this.worldPosition.getY(),
                    this.worldPosition.getZ(), 32.0D,
                    new InfusionSourceFxPayload(this.worldPosition, source, color, ticks));
        }
    }

    private void sendBlockZapFx(Level level, double fromX, double fromY, double fromZ, double toX, double toY,
            double toZ) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, this.worldPosition.getX(), this.worldPosition.getY(),
                    this.worldPosition.getZ(), 32.0D,
                    new BlockZapFxPayload(fromX, fromY, fromZ, toX, toY, toZ));
        }
    }
}
