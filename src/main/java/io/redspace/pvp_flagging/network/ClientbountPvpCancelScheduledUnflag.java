package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientbountPvpCancelScheduledUnflag {
    private final int ticks;

    public ClientbountPvpCancelScheduledUnflag(int ticks) {
        this.ticks = ticks;
    }

    public ClientbountPvpCancelScheduledUnflag(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(ticks);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientHelper.handlePvpUnflagScheduleCancelled(ticks);
        });
    }
}
