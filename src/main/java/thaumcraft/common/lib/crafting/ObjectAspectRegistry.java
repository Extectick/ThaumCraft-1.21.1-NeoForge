package thaumcraft.common.lib.crafting;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.services.ServerServices;

public final class ObjectAspectRegistry {
    private ObjectAspectRegistry() {
    }

    public static void registerReloadListener(AddReloadListenerEvent event) {
        ServerServices.get().registerObjectAspectReloadListener(event);
    }

    public static AspectList getObjectTags(ItemStack stack) {
        return ServerServices.get().getObjectTags(stack);
    }

    public static AspectList getObjectTagsWithBonus(ItemStack stack) {
        return ServerServices.get().getObjectTagsWithBonus(stack);
    }

    public static AspectList getBonusTags(ItemStack stack, AspectList sourceTags) {
        return ServerServices.get().getBonusTags(stack, sourceTags);
    }

    public static int itemEntryCount() {
        return ServerServices.get().itemAspectEntryCount();
    }

    public static int tagEntryCount() {
        return ServerServices.get().tagAspectEntryCount();
    }

    public static int generatedEntryCount() {
        return ServerServices.get().generatedAspectEntryCount();
    }

    public static String source(ItemStack stack) {
        return ServerServices.get().objectAspectSource(stack);
    }
}
