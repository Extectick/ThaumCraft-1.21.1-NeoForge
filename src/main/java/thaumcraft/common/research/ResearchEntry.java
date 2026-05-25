package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.research.ResearchTrigger.AspectTrigger;
import thaumcraft.common.research.ResearchTrigger.EntityTrigger;
import thaumcraft.common.research.ResearchTrigger.ItemTrigger;

public record ResearchEntry(String key, String category, List<Aspect> tags, int displayColumn, int displayRow,
        int complexity, Optional<ResearchIcon> icon, List<ResearchPage> pages, Set<String> parents,
        Set<String> hiddenParents, Set<String> siblings, List<ResearchTrigger> triggers, Set<ResearchFlag> flags,
        int color, int warp) {
    public ResearchEntry(String key, String category, List<Aspect> tags, int complexity) {
        this(builder(key, category).tags(tags).complexity(complexity));
    }

    public ResearchEntry(String key, String category, List<Aspect> tags, int complexity, Set<String> parents,
            Set<ResearchFlag> flags) {
        this(builder(key, category).tags(tags).complexity(complexity).parents(parents).flags(flags));
    }

    public ResearchEntry(Builder builder) {
        this(builder.key, builder.category, builder.tags, builder.displayColumn, builder.displayRow,
                builder.complexity, Optional.ofNullable(builder.icon), builder.pages, builder.parents,
                builder.hiddenParents, builder.siblings, builder.triggers, builder.flags, builder.color == null
                        ? defaultColor(builder.tags)
                        : builder.color,
                builder.warp);
    }

    public ResearchEntry {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Research key cannot be blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Research category cannot be blank");
        }
        tags = List.copyOf(tags);
        if (tags.isEmpty()) {
            throw new IllegalArgumentException("Research must define at least one tag");
        }
        displayColumn = Math.max(-99, Math.min(99, displayColumn));
        displayRow = Math.max(-99, Math.min(99, displayRow));
        complexity = Math.max(1, Math.min(3, complexity));
        pages = List.copyOf(pages);
        parents = Set.copyOf(parents);
        hiddenParents = Set.copyOf(hiddenParents);
        siblings = Set.copyOf(siblings);
        triggers = List.copyOf(triggers);
        flags = Set.copyOf(flags);
        warp = Math.max(0, warp);
    }

    public Aspect primaryAspect() {
        return this.tags.getFirst();
    }

    public String nameTranslationKey() {
        return "tc.research." + this.key + ".name";
    }

    public String legacyNameTranslationKey() {
        return "tc.research_name." + this.key;
    }

    public String textTranslationKey() {
        return "tc.research." + this.key + ".text";
    }

    public String legacyTextTranslationKey() {
        return "tc.research_text." + this.key;
    }

    public boolean hasFlag(ResearchFlag flag) {
        return this.flags.contains(flag);
    }

    public boolean isVisibleInBook(ResearchKnowledgeData knowledge) {
        if ((this.hasFlag(ResearchFlag.HIDDEN) || this.hasFlag(ResearchFlag.LOST)) && !knowledge.isComplete(this.key)) {
            return false;
        }
        if (this.hasFlag(ResearchFlag.CONCEALED)) {
            return this.parents.stream().anyMatch(knowledge::isComplete)
                    || this.hiddenParents.stream().anyMatch(knowledge::isComplete)
                    || knowledge.isComplete(this.key);
        }
        return true;
    }

    public List<String> requisites() {
        List<String> requisites = new ArrayList<>(this.parents);
        requisites.addAll(this.hiddenParents);
        return List.copyOf(requisites);
    }

    public boolean hasTriggers() {
        return !this.triggers.isEmpty();
    }

    public static Builder builder(String key, String category) {
        return new Builder(key, category);
    }

    private static int defaultColor(List<Aspect> tags) {
        if (tags == null || tags.isEmpty()) {
            return 0x999999;
        }
        return tags.getFirst().getColor();
    }

    public enum ResearchFlag {
        AUTO_UNLOCK,
        HIDDEN,
        CONCEALED,
        LOST,
        VIRTUAL,
        STUB,
        SECONDARY,
        SPECIAL,
        ROUND
    }

    public sealed interface ResearchIcon permits ResearchIcon.CyclingItemIcon, ResearchIcon.ItemIcon,
            ResearchIcon.TextureIcon {
        record CyclingItemIcon(List<ItemStack> stacks, int ticksPerFrame) implements ResearchIcon {
            public CyclingItemIcon {
                if (stacks.isEmpty()) {
                    throw new IllegalArgumentException("Cycling research icon must contain at least one item stack");
                }
                stacks = stacks.stream().map(ItemStack::copy).toList();
                ticksPerFrame = Math.max(1, ticksPerFrame);
            }

            public ItemStack stackAt(int tick) {
                return this.stacks.get(Math.floorDiv(tick, this.ticksPerFrame) % this.stacks.size());
            }
        }

        record ItemIcon(ItemStack stack) implements ResearchIcon {
            public ItemIcon {
                stack = stack.copy();
            }
        }

        record TextureIcon(ResourceLocation texture) implements ResearchIcon {
        }
    }

    public static final class Builder {
        private final String key;
        private final String category;
        private List<Aspect> tags = List.of(Aspect.AIR);
        private int displayColumn;
        private int displayRow;
        private int complexity = 1;
        private ResearchIcon icon;
        private List<ResearchPage> pages = List.of();
        private Set<String> parents = Set.of();
        private Set<String> hiddenParents = Set.of();
        private Set<String> siblings = Set.of();
        private List<ResearchTrigger> triggers = List.of();
        private Set<ResearchFlag> flags = Set.of();
        private Integer color;
        private int warp;

        private Builder(String key, String category) {
            this.key = key;
            this.category = category;
        }

        public Builder tags(List<Aspect> tags) {
            this.tags = List.copyOf(tags);
            return this;
        }

        public Builder position(int column, int row) {
            this.displayColumn = column;
            this.displayRow = row;
            return this;
        }

        public Builder complexity(int complexity) {
            this.complexity = complexity;
            return this;
        }

        public Builder icon(ItemStack stack) {
            this.icon = new ResearchIcon.ItemIcon(stack);
            return this;
        }

        public Builder cyclingIcon(int ticksPerFrame, ItemStack... stacks) {
            this.icon = new ResearchIcon.CyclingItemIcon(List.of(stacks), ticksPerFrame);
            return this;
        }

        public Builder icon(ResourceLocation texture) {
            this.icon = new ResearchIcon.TextureIcon(texture);
            return this;
        }

        public Builder pages(ResearchPage... pages) {
            this.pages = List.of(pages);
            return this;
        }

        public Builder pages(List<ResearchPage> pages) {
            this.pages = List.copyOf(pages);
            return this;
        }

        public Builder parents(String... parents) {
            this.parents = Set.of(parents);
            return this;
        }

        public Builder setParents(String... parents) {
            return this.parents(parents);
        }

        public Builder parents(Set<String> parents) {
            this.parents = Set.copyOf(parents);
            return this;
        }

        public Builder hiddenParents(String... hiddenParents) {
            this.hiddenParents = Set.of(hiddenParents);
            return this;
        }

        public Builder setParentsHidden(String... hiddenParents) {
            return this.hiddenParents(hiddenParents);
        }

        public Builder hiddenParents(Set<String> hiddenParents) {
            this.hiddenParents = Set.copyOf(hiddenParents);
            return this;
        }

        public Builder siblings(String... siblings) {
            this.siblings = Set.of(siblings);
            return this;
        }

        public Builder setSiblings(String... siblings) {
            return this.siblings(siblings);
        }

        public Builder siblings(Set<String> siblings) {
            this.siblings = Set.copyOf(siblings);
            return this;
        }

        public Builder itemTriggers(ItemStack... stacks) {
            List<ResearchTrigger> copy = new ArrayList<>(this.triggers);
            for (ItemStack stack : stacks) {
                copy.add(new ItemTrigger(stack));
            }
            this.triggers = List.copyOf(copy);
            return this;
        }

        public Builder setItemTriggers(ItemStack... stacks) {
            return this.itemTriggers(stacks);
        }

        public Builder entityTriggers(ResourceLocation... entityTypes) {
            List<ResearchTrigger> copy = new ArrayList<>(this.triggers);
            for (ResourceLocation entityType : entityTypes) {
                copy.add(new EntityTrigger(entityType));
            }
            this.triggers = List.copyOf(copy);
            return this;
        }

        public Builder setEntityTriggers(ResourceLocation... entityTypes) {
            return this.entityTriggers(entityTypes);
        }

        public Builder aspectTriggers(Aspect... aspects) {
            List<ResearchTrigger> copy = new ArrayList<>(this.triggers);
            for (Aspect aspect : aspects) {
                copy.add(new AspectTrigger(aspect));
            }
            this.triggers = List.copyOf(copy);
            return this;
        }

        public Builder setAspectTriggers(Aspect... aspects) {
            return this.aspectTriggers(aspects);
        }

        public Builder triggers(List<ResearchTrigger> triggers) {
            this.triggers = List.copyOf(triggers);
            return this;
        }

        public Builder flags(Set<ResearchFlag> flags) {
            this.flags = Set.copyOf(flags);
            return this;
        }

        public Builder flags(ResearchFlag... flags) {
            if (flags.length == 0) {
                this.flags = Set.of();
            } else {
                EnumSet<ResearchFlag> copy = EnumSet.noneOf(ResearchFlag.class);
                for (ResearchFlag flag : flags) {
                    copy.add(flag);
                }
                this.flags = copy;
            }
            return this;
        }

        public Builder flag(ResearchFlag flag) {
            EnumSet<ResearchFlag> copy = this.flags.isEmpty()
                    ? EnumSet.noneOf(ResearchFlag.class)
                    : EnumSet.copyOf(this.flags);
            copy.add(flag);
            this.flags = copy;
            return this;
        }

        public Builder setAutoUnlock() {
            return this.flag(ResearchFlag.AUTO_UNLOCK);
        }

        public Builder setHidden() {
            return this.flag(ResearchFlag.HIDDEN);
        }

        public Builder setConcealed() {
            return this.flag(ResearchFlag.CONCEALED);
        }

        public Builder setLost() {
            return this.flag(ResearchFlag.LOST);
        }

        public Builder setVirtual() {
            return this.flag(ResearchFlag.VIRTUAL);
        }

        public Builder setStub() {
            return this.flag(ResearchFlag.STUB);
        }

        public Builder setSecondary() {
            return this.flag(ResearchFlag.SECONDARY);
        }

        public Builder setSpecial() {
            return this.flag(ResearchFlag.SPECIAL);
        }

        public Builder setRound() {
            return this.flag(ResearchFlag.ROUND);
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder warp(int warp) {
            this.warp = warp;
            return this;
        }

        public ResearchEntry build() {
            return new ResearchEntry(this);
        }

        public List<String> requisites() {
            List<String> requisites = new ArrayList<>(this.parents);
            requisites.addAll(this.hiddenParents);
            return List.copyOf(requisites);
        }
    }
}
