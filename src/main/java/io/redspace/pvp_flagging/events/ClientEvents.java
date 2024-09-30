package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;


@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameTagEvent(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player player) {
            var newTag = ClientHelper.getNameTag(player);
            if (newTag != null) {
                event.setContent(newTag);
            }
        }
    }
}

