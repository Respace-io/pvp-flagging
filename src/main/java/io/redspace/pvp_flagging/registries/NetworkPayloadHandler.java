package io.redspace.pvp_flagging.registries;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.network.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = PvpFlagging.MODID)
public class NetworkPayloadHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(PvpFlagging.MODID).versioned("1.0.0").optional();
        payloadRegistrar.playToClient(PvpFlagUpdatePacket.TYPE, PvpFlagUpdatePacket.STREAM_CODEC, PvpFlagUpdatePacket::handle);
        payloadRegistrar.playToClient(PvpUnflagScheduledPacket.TYPE, PvpUnflagScheduledPacket.STREAM_CODEC, PvpUnflagScheduledPacket::handle);
        payloadRegistrar.playToClient(PvpCancelScheduledUnflagPacket.TYPE, PvpCancelScheduledUnflagPacket.STREAM_CODEC, PvpCancelScheduledUnflagPacket::handle);
        payloadRegistrar.playToClient(SyncPvpDataPacket.TYPE, SyncPvpDataPacket.STREAM_CODEC, SyncPvpDataPacket::handle);
        payloadRegistrar.playToClient(WarnPlayerPacket.TYPE, WarnPlayerPacket.STREAM_CODEC, WarnPlayerPacket::handle);
    }
}
