package thaumcraft.client.input;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.common.network.CycleWandFocusPayload;

public final class TCKeyMappings {
    public static final String CATEGORY = "key.categories.thaumcraft";
    public static final KeyMapping CHANGE_FOCUS = new KeyMapping("key.thaumcraft.change_focus", GLFW.GLFW_KEY_R,
            CATEGORY);

    private TCKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CHANGE_FOCUS);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        while (CHANGE_FOCUS.consumeClick()) {
            PacketDistributor.sendToServer(CycleWandFocusPayload.INSTANCE);
        }
    }
}
