package io.redspace.pvp_flagging.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public final ForgeConfigSpec SPEC;
    public final ForgeConfigSpec.ConfigValue<PvpConfigState> PLAYER_LOGIN_STATE;
    public final ForgeConfigSpec.ConfigValue<PvpConfigState> PLAYER_RESPAWN_STATE;
    public final ForgeConfigSpec.ConfigValue<Integer> UNFLAG_WAIT_TIME_TICKS;
    public final ForgeConfigSpec.ConfigValue<Integer> PVP_ZONE_BOUNDS_CHECK_TICKS;
    public final ForgeConfigSpec.ConfigValue<Number> PVP_DAMAGE_MULIPLIER;
    public final ForgeConfigSpec.ConfigValue<Boolean> WELCOME_MESSAGE;

    ServerConfig() {
        var builder = new ForgeConfigSpec.Builder();
        builder.push("PVP_FLAGGING");
        PLAYER_LOGIN_STATE = builder.defineEnum("player_login_state", PvpConfigState.PRESERVE);
        PLAYER_RESPAWN_STATE = builder.defineEnum("player_respawn_state", PvpConfigState.UNFLAG);
        UNFLAG_WAIT_TIME_TICKS = builder.define("unflag_wait_time_ticks", 15 * 20);
        PVP_ZONE_BOUNDS_CHECK_TICKS = builder.define("pvp_zone_bounds_check_ticks", 20);
        PVP_DAMAGE_MULIPLIER = builder.define("pvp_damage_multiplier", 1.0);
        WELCOME_MESSAGE = builder.define("display_welcome_message", true);
        builder.pop();
        SPEC = builder.build();
    }
}