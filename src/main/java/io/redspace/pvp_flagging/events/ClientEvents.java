package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

@EventBusSubscriber(modid = PvpFlagging.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameTagEvent(RenderNameTagEvent.CanRender event) {
        if (event.getEntity() instanceof Player player && ClientHelper.isFlagged(player.getUUID()) && event.getContent() != null) {
            event.setContent(ClientHelper.modifyNameTag(event.getContent()));
        }
    }
}
