package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = PvpFlagging.MODID, bus = EventBusSubscriber.Bus.GAME)
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
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                PlayerFlagManager.INSTANCE.cancelScheduledUnflag(serverPlayer);
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
    public static void onLivingHurtEvent(LivingDamageEvent.Post event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        boolean pvp = victim instanceof ServerPlayer && attacker instanceof ServerPlayer;
        if (pvp && PlayerFlagManager.INSTANCE.anyPlayersScheduledToUnflag()) {
            PlayerFlagManager.INSTANCE.cancelScheduledUnflag((ServerPlayer) victim);
            PlayerFlagManager.INSTANCE.cancelScheduledUnflag((ServerPlayer) attacker);
        }
    }
}