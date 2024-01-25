package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.client.ClientPvpFlagCache;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundSyncPvpData {
    private final ObjectSet<UUID> flaggedPlayers;

    public ClientboundSyncPvpData(ObjectSet<UUID> flaggedPlayers) {
        this.flaggedPlayers = flaggedPlayers;//flaggedPlayers.toArray(new UUID[0]);
    }

    public ClientboundSyncPvpData(FriendlyByteBuf buf) {
        flaggedPlayers = buf.readCollection(ObjectOpenHashSet::new, FriendlyByteBuf::readUUID);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(flaggedPlayers, FriendlyByteBuf::writeUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientPvpFlagCache.handleFullPvpDataSync(flaggedPlayers);
        });
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
