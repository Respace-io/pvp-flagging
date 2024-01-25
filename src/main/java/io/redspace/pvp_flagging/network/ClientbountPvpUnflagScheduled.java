package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.client.ClientPvpFlagCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientbountPvpUnflagScheduled {
    private final int ticks;

    public ClientbountPvpUnflagScheduled(int ticks) {
        this.ticks = ticks;
    }

    public ClientbountPvpUnflagScheduled(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(ticks);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientPvpFlagCache.handlePvpUnflagScheduled(ticks);
        });
    }
}
