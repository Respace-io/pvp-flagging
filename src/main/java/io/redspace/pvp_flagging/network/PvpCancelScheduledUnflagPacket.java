package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PvpCancelScheduledUnflagPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PvpCancelScheduledUnflagPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(PvpFlagging.MODID, "pvp_cancel_scheduled_unflag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PvpCancelScheduledUnflagPacket> STREAM_CODEC = CustomPacketPayload.codec(PvpCancelScheduledUnflagPacket::write, PvpCancelScheduledUnflagPacket::new);
    private final int ticks;

    public PvpCancelScheduledUnflagPacket(int ticks) {
        this.ticks = ticks;
    }

    public PvpCancelScheduledUnflagPacket(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(ticks);
    }

    public static void handle(PvpCancelScheduledUnflagPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHelper.handlePvpUnflagScheduleCancelled(packet.ticks));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
