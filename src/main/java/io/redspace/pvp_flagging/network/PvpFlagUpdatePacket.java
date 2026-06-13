package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class PvpFlagUpdatePacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PvpFlagUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(PvpFlagging.MODID, "pvp_flag_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PvpFlagUpdatePacket> STREAM_CODEC = CustomPacketPayload.codec(PvpFlagUpdatePacket::write, PvpFlagUpdatePacket::new);
    private final boolean flagged;
    private final UUID playerUUID;

    public PvpFlagUpdatePacket(UUID playerUUID, boolean flagged) {
        this.flagged = flagged;
        this.playerUUID = playerUUID;
    }

    public PvpFlagUpdatePacket(FriendlyByteBuf buf) {
        flagged = buf.readBoolean();
        playerUUID = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(flagged);
        buf.writeUUID(playerUUID);
    }

    public static void handle(PvpFlagUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHelper.handlePvpFlagUpdate(packet.playerUUID, packet.flagged));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
