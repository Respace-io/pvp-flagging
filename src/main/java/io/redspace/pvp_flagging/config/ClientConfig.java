package io.redspace.pvp_flagging.config;


import io.redspace.pvp_flagging.client.FlagIndicatorOverlay;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public final ForgeConfigSpec SPEC;
    public final ForgeConfigSpec.ConfigValue<Boolean> INDICATOR_ENABLED;
    public final ForgeConfigSpec.ConfigValue<FlagIndicatorOverlay.HudAnchor> INDICATOR_HUD_ANCHOR;
    public final ForgeConfigSpec.ConfigValue<Integer> INDICATOR_X_OFFSET;
    public final ForgeConfigSpec.ConfigValue<Integer> INDICATOR_Y_OFFSET;

    ClientConfig() {
        var builder = new ForgeConfigSpec.Builder();
        builder.push("PVP_FLAGGING");
        INDICATOR_ENABLED = builder.define("hud_indicator_enabled", true);
        INDICATOR_HUD_ANCHOR = builder.defineEnum("hud_indicator_anchor", FlagIndicatorOverlay.HudAnchor.BottomLeft);
        INDICATOR_X_OFFSET = builder.define("hud_indicator_x_offset", 0);
        INDICATOR_Y_OFFSET = builder.define("hud_indicator_y_offset", 0);
        builder.pop();
        SPEC = builder.build();
    }
}