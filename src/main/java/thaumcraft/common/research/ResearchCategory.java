package thaumcraft.common.research;

import net.minecraft.resources.ResourceLocation;

public record ResearchCategory(String key, ResourceLocation icon, ResourceLocation background,
        int minDisplayColumn, int maxDisplayColumn, int minDisplayRow, int maxDisplayRow, int sortOrder) {
    public ResearchCategory(String key, ResourceLocation icon, ResourceLocation background) {
        this(key, icon, background, -8, 8, -8, 8, 0);
    }

    public String nameTranslationKey() {
        return "tc.research_category." + this.key + ".name";
    }
}
