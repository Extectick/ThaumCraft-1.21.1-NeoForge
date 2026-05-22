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

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ThaumcraftConfig() {
    }
}
