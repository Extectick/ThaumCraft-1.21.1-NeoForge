package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menus.ArcaneWorktableMenu;
import thaumcraft.common.menus.ResearchTableMenu;

public final class TCMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneWorktableMenu>> ARCANE_WORKTABLE = REGISTRY.register("arcane_worktable",
            () -> new MenuType<>(ArcaneWorktableMenu::new, FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<ResearchTableMenu>> RESEARCH_TABLE = REGISTRY.register("research_table",
            () -> new MenuType<>(ResearchTableMenu::new, FeatureFlags.VANILLA_SET));

    private TCMenuTypes() {
    }
}
