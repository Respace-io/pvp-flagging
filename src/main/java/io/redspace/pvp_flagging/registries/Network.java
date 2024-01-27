package io.redspace.pvp_flagging.registries;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.network.ClientboundPvpFlagUpdate;
import io.redspace.pvp_flagging.network.ClientboundSyncPvpData;
import io.redspace.pvp_flagging.network.ClientbountPvpCancelScheduledUnflag;
import io.redspace.pvp_flagging.network.ClientbountPvpUnflagScheduled;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(PvpFlagging.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        net.messageBuilder(ClientboundPvpFlagUpdate.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundPvpFlagUpdate::new)
                .encoder(ClientboundPvpFlagUpdate::toBytes)
                .consumerMainThread(ClientboundPvpFlagUpdate::handle)
                .add();

        net.messageBuilder(ClientboundSyncPvpData.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncPvpData::new)
                .encoder(ClientboundSyncPvpData::toBytes)
                .consumerMainThread(ClientboundSyncPvpData::handle)
                .add();

        net.messageBuilder(ClientbountPvpUnflagScheduled.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientbountPvpUnflagScheduled::new)
                .encoder(ClientbountPvpUnflagScheduled::toBytes)
                .consumerMainThread(ClientbountPvpUnflagScheduled::handle)
                .add();

        net.messageBuilder(ClientbountPvpCancelScheduledUnflag.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientbountPvpCancelScheduledUnflag::new)
                .encoder(ClientbountPvpCancelScheduledUnflag::toBytes)
                .consumerMainThread(ClientbountPvpCancelScheduledUnflag::handle)
                .add();
        
        INSTANCE = net;
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);

    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity, boolean sendToSource) {
        if (sendToSource) {
            INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
        } else {
            INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        }
    }
}