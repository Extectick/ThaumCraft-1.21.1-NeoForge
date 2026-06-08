package thaumcraft.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ThaumcraftConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DEBUG_BOOTSTRAP = BUILDER
            .comment("Logs extra information while the 1.21.1 port is being built.")
            .define("debugBootstrap", false);

    public static final ModConfigSpec.IntValue RUNIC_RECHARGE_MILLISECONDS = BUILDER
            .comment("How many milliseconds pass between runic shielding recharge ticks. Matches the old Thaumcraft default.")
            .defineInRange("runicShielding.rechargeMilliseconds", 2000, 500, 60000);

    public static final ModConfigSpec.IntValue RUNIC_RECHARGE_DELAY_TICKS = BUILDER
            .comment("How many game ticks pass after runic shielding is reduced to zero before it starts recharging.")
            .defineInRange("runicShielding.rechargeDelayTicks", 80, 0, 20 * 60 * 10);

    public static final ModConfigSpec.BooleanValue WAND_DIAL_BOTTOM = BUILDER
            .comment("Matches Thaumcraft 4's wand_dial_bottom option. False renders the wand vis dial in the top-left corner.")
            .define("client.wandDialBottom", false);

    public static final ModConfigSpec.IntValue NOTIFICATION_DELAY_MILLISECONDS = BUILDER
            .comment("How long Thaumcraft HUD notifications remain visible. Matches Thaumcraft 4's notificationDelay default.")
            .defineInRange("client.notifications.delayMilliseconds", 5000, 500, 60000);

    public static final ModConfigSpec.IntValue NOTIFICATION_MAX = BUILDER
            .comment("Maximum number of Thaumcraft HUD notifications visible at once. Matches Thaumcraft 4's notificationMax default.")
            .defineInRange("client.notifications.max", 15, 1, 20);

    public static final ModConfigSpec.BooleanValue GENERATE_CINNABAR = BUILDER
            .comment("Generate cinnabar ore. Matches Thaumcraft 4's genCinnibar option.")
            .define("worldgen.cinnabar", true);

    public static final ModConfigSpec.BooleanValue GENERATE_AMBER = BUILDER
            .comment("Generate amber ore. Matches Thaumcraft 4's genAmber option.")
            .define("worldgen.amber", true);

    public static final ModConfigSpec.BooleanValue GENERATE_INFUSED_STONE = BUILDER
            .comment("Generate elemental infused stone. Matches Thaumcraft 4's genInfusedStone option.")
            .define("worldgen.infusedStone", true);

    public static final ModConfigSpec.BooleanValue GENERATE_AURA_NODES = BUILDER
            .comment("Generate natural aura nodes. Matches Thaumcraft 4's genAura option.")
            .define("worldgen.auraNodes", true);

    public static final ModConfigSpec.IntValue AURA_NODE_RARITY = BUILDER
            .comment("Average number of chunks per natural aura node. Matches Thaumcraft 4's node_rarity default.")
            .defineInRange("worldgen.nodeRarity", 36, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SPECIAL_AURA_NODE_RARITY = BUILDER
            .comment("Rarity used for special node types, modifiers, and compound aspects.")
            .defineInRange("worldgen.specialNodeRarity", 18, 3, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ThaumcraftConfig() {
    }
}
