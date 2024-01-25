package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.client.ClientPvpFlagCache;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameTagEvent(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player player) {
            var newTag = ClientPvpFlagCache.getNameTag(player);
            if (newTag != null) {
                event.setContent(newTag);
            }
        }
    }
}

