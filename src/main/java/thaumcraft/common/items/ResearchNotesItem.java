package thaumcraft.common.items;

import java.util.List;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchNoteData;
import thaumcraft.common.research.ResearchEntry;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchRegistry;

public class ResearchNotesItem extends Item {
    public ResearchNotesItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return create(ResearchRegistry.FIRST_STEPS);
    }

    public static ItemStack create(String key) {
        ItemStack stack = new ItemStack(thaumcraft.common.registry.TCItems.RESEARCH_NOTES.get());
        stack.set(TCDataComponents.RESEARCH_NOTE, ResearchRegistry.createNoteData(key,
                ResearchRegistry.deterministicRandom(key)));
        stack.set(DataComponents.RARITY, Rarity.RARE);
        return stack;
    }

    public static ItemStack createUnknown() {
        ItemStack stack = new ItemStack(thaumcraft.common.registry.TCItems.RESEARCH_NOTES.get());
        stack.set(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
        stack.set(DataComponents.RARITY, Rarity.RARE);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        ResearchNoteData data = stack.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
        if (data.isEmpty()) {
            return Component.translatable("item.thaumcraft.research_notes.unknown");
        }
        return Component.translatable(data.complete() ? "item.thaumcraft.discovery" : "item.thaumcraft.research_notes");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ResearchNoteData data = stack.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
        if (level.isClientSide()) {
            return data.isEmpty() || data.complete()
                    ? InteractionResultHolder.success(stack)
                    : InteractionResultHolder.pass(stack);
        }

        if (data.complete()) {
            if (ResearchManager.isComplete(player, data.key())) {
                player.displayClientMessage(Component.translatable("tc.researchknown"), true);
                return InteractionResultHolder.success(stack);
            }
            if (!ResearchManager.canStart(player, data.key())) {
                player.displayClientMessage(Component.translatable("tc.researcherror"), true);
                return InteractionResultHolder.fail(stack);
            }

            if (ResearchManager.completeResearch(player, data.key())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, player.blockPosition(), TCSoundEvents.LEARN.get(), SoundSource.PLAYERS,
                        0.75F, 1.0F);
                return InteractionResultHolder.success(stack);
            }
        } else if (data.isEmpty()) {
            return this.revealUnknownResearch(level, player, stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        ResearchNoteData data = stack.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
        if (data.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.thaumcraft.research_notes.unknown.1")
                    .withStyle(ChatFormatting.GOLD));
            tooltipComponents.add(Component.translatable("item.thaumcraft.research_notes.unknown.2")
                    .withStyle(ChatFormatting.BLUE));
        } else {
            ResearchRegistry.get(data.key()).ifPresent(entry -> {
                tooltipComponents.add(Component.translatable(entry.nameTranslationKey()).withStyle(ChatFormatting.GOLD));
                tooltipComponents.add(Component.translatable(entry.textTranslationKey()).withStyle(ChatFormatting.ITALIC,
                        ChatFormatting.GRAY));
            });
            tooltipComponents.add(Component.translatable(data.complete()
                    ? "item.thaumcraft.research_notes.complete"
                    : "item.thaumcraft.research_notes.incomplete").withStyle(data.complete()
                            ? ChatFormatting.GREEN
                            : ChatFormatting.YELLOW));
            ResearchRegistry.get(data.key()).ifPresent(entry -> {
                if (!entry.parents().isEmpty()) {
                    tooltipComponents.add(Component.translatable("item.thaumcraft.research_notes.parents",
                            formatParents(entry)).withStyle(ChatFormatting.DARK_GRAY));
                }
            });
        }
    }

    private InteractionResultHolder<ItemStack> revealUnknownResearch(Level level, Player player, ItemStack stack) {
        return ResearchManager.findHiddenResearch(player)
                .map(key -> {
                    ResearchNoteData data = ResearchRegistry.createNoteData(key, new Random(level.random.nextLong()));
                    stack.set(TCDataComponents.RESEARCH_NOTE, data);
                    stack.set(DataComponents.RARITY, Rarity.RARE);
                    level.playSound(null, player.blockPosition(), TCSoundEvents.WRITE.get(), SoundSource.PLAYERS,
                            0.75F, 1.0F);
                    return InteractionResultHolder.success(stack);
                })
                .orElseGet(() -> {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    ItemStack fragments = new ItemStack(TCItems.KNOWLEDGE_FRAGMENT.get(),
                            7 + level.random.nextInt(3));
                    ItemEntity entity = new ItemEntity(level, player.getX(), player.getY() + player.getEyeHeight() / 2.0F,
                            player.getZ(), fragments);
                    level.addFreshEntity(entity);
                    level.playSound(null, player.blockPosition(), TCSoundEvents.ERASE.get(), SoundSource.PLAYERS,
                            0.75F, 1.0F);
                    return InteractionResultHolder.success(stack);
                });
    }

    private static Component formatParents(ResearchEntry entry) {
        MutableComponent parents = Component.empty();
        boolean first = true;
        for (String parent : entry.parents()) {
            if (!first) {
                parents.append(", ");
            }
            parents.append(ResearchRegistry.get(parent)
                    .map(parentEntry -> Component.translatable(parentEntry.nameTranslationKey()))
                    .orElseGet(() -> Component.literal(parent)));
            first = false;
        }
        return parents;
    }
}
