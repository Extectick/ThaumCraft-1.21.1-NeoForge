package thaumictinkerer.common.registry;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumictinkerer.ThaumicTinkerer;
import thaumictinkerer.common.menus.IchorPouchMenu;

public class TTMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ThaumicTinkerer.MODID);

    public static final java.util.function.Supplier<MenuType<IchorPouchMenu>> ICHOR_POUCH = REGISTRY.register("ichor_pouch", () -> IMenuTypeExtension.create(IchorPouchMenu::new));
}

