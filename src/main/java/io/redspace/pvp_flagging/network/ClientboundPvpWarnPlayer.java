package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundPvpWarnPlayer {
    public ClientboundPvpWarnPlayer() {

    }

    public ClientboundPvpWarnPlayer(FriendlyByteBuf buf) {
        buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(true);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(ClientHelper::handlePvpZoneWarning);
    }
}
