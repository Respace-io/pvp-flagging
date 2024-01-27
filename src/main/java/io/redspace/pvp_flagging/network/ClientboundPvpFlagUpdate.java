package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundPvpFlagUpdate {
    private final boolean flagged;
    private final UUID playerUUID;

    public ClientboundPvpFlagUpdate(UUID playerUUID, boolean flagged) {
        this.flagged = flagged;
        this.playerUUID = playerUUID;
    }

    public ClientboundPvpFlagUpdate(FriendlyByteBuf buf) {
        flagged = buf.readBoolean();
        playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(flagged);
        buf.writeUUID(playerUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientHelper.handlePvpFlagUpdate(playerUUID, flagged);
        });
    }
}
