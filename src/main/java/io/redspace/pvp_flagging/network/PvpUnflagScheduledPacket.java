package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PvpUnflagScheduledPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PvpUnflagScheduledPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(PvpFlagging.MODID, "pvp_unflag_scheduled"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PvpUnflagScheduledPacket> STREAM_CODEC = CustomPacketPayload.codec(PvpUnflagScheduledPacket::write, PvpUnflagScheduledPacket::new);
    private final int ticks;

    public PvpUnflagScheduledPacket(int ticks) {
        this.ticks = ticks;
    }

    public PvpUnflagScheduledPacket(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(ticks);
    }

    public static void handle(PvpUnflagScheduledPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHelper.handlePvpUnflagScheduled(packet.ticks));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
