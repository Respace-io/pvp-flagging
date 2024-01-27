package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvpFlagging.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventsForge {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
                switch (PvpConfig.SERVER.PLAYER_LOGIN_STATE.get()) {
                    case FLAG -> PlayerFlagManager.INSTANCE.flagPlayer(serverPlayer);
                    case UNFLAG -> PlayerFlagManager.INSTANCE.unflagPlayerImmediate(serverPlayer);
                }
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                switch (PvpConfig.SERVER.PLAYER_RESPAWN_STATE.get()) {
                    case FLAG -> PlayerFlagManager.INSTANCE.flagPlayer(serverPlayer);
                    case UNFLAG -> PlayerFlagManager.INSTANCE.unflagPlayerImmediate(serverPlayer);
                }
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        if (PlayerFlagManager.INSTANCE.anyPlayersScheduledToUnflag()) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                PlayerFlagManager.INSTANCE.cancelScheduledUnflag(serverPlayer);
            }
            if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
                PlayerFlagManager.INSTANCE.cancelScheduledUnflag(serverPlayer);
            }
            if (event.getSource().getDirectEntity() instanceof ServerPlayer serverPlayer) {
                PlayerFlagManager.INSTANCE.cancelScheduledUnflag(serverPlayer);
            }
        }
    }
}