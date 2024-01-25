package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.server.level.ServerPlayer;
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
            }
        }
    }
}