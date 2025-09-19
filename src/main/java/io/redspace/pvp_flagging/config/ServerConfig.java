package io.redspace.pvp_flagging.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public final ModConfigSpec SPEC;
    public final ModConfigSpec.ConfigValue<PvpConfigState> PLAYER_LOGIN_STATE;
    public final ModConfigSpec.ConfigValue<PvpConfigState> PLAYER_RESPAWN_STATE;
    public final ModConfigSpec.ConfigValue<Integer> UNFLAG_WAIT_TIME_TICKS;
    public final ModConfigSpec.ConfigValue<Integer> PVP_ZONE_BOUNDS_CHECK_TICKS;
    public final ModConfigSpec.ConfigValue<Number> PVP_DAMAGE_MULIPLIER;

    ServerConfig() {
        var builder = new ModConfigSpec.Builder();
        builder.push("PVP_FLAGGING");
        PLAYER_LOGIN_STATE = builder.defineEnum("player_login_state", PvpConfigState.PRESERVE);
        PLAYER_RESPAWN_STATE = builder.defineEnum("player_respawn_state", PvpConfigState.UNFLAG);
        UNFLAG_WAIT_TIME_TICKS = builder.define("unflag_wait_time_ticks", 15 * 20);
        PVP_ZONE_BOUNDS_CHECK_TICKS = builder.define("pvp_zone_bounds_check_ticks", 20);
        PVP_DAMAGE_MULIPLIER = builder.define("pvp_damage_multiplier", 1.0);
        builder.pop();
        SPEC = builder.build();
    }
}