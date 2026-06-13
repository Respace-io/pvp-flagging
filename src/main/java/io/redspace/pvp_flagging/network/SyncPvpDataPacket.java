package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class SyncPvpDataPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPvpDataPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(PvpFlagging.MODID, "sync_pvp_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPvpDataPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncPvpDataPacket::write, SyncPvpDataPacket::new);
    private final ObjectSet<UUID> flaggedPlayers;

    public SyncPvpDataPacket(ObjectSet<UUID> flaggedPlayers) {
        this.flaggedPlayers = flaggedPlayers;//flaggedPlayers.toArray(new UUID[0]);
    }

    public SyncPvpDataPacket(FriendlyByteBuf buf) {
        flaggedPlayers = buf.readCollection(ObjectOpenHashSet::new, b -> b.readUUID());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(flaggedPlayers, (b, v) -> b.writeUUID(v));
    }

    public static void handle(SyncPvpDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHelper.handleFullPvpDataSync(packet.flaggedPlayers));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


//public class ClientboundSyncPvpData {
//    private final Object2BooleanMap<UUID> flaggedPlayers;
//
//    public ClientboundSyncPvpData(Object2BooleanMap<UUID> flaggedPlayers) {
//        this.flaggedPlayers = Object2BooleanMaps.unmodifiable(flaggedPlayers);
//    }
//
//    public ClientboundSyncPvpData(FriendlyByteBuf buf) {
//        var tmp = buf.readMap(Object2BooleanOpenHashMap::new, FriendlyByteBuf::readUUID, FriendlyByteBuf::readBoolean);
//        this.flaggedPlayers = Object2BooleanMaps.unmodifiable(tmp);
//    }
//
//    public void toBytes(FriendlyByteBuf buf) {
//        buf.writeMap(this.flaggedPlayers, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeBoolean);
//    }
//
//    public void handle(Supplier<NetworkEvent.Context> supplier) {
//        NetworkEvent.Context ctx = supplier.get();
//        ctx.enqueueWork(() -> {
//            ClientHelper.
//        });
//    }
//}
