package io.redspace.pvp_flagging.network;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class WarnPlayerPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WarnPlayerPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PvpFlagging.MODID, "warn_player"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WarnPlayerPacket> STREAM_CODEC = CustomPacketPayload.codec(WarnPlayerPacket::write, WarnPlayerPacket::new);

    public WarnPlayerPacket() {
    }

    public WarnPlayerPacket(FriendlyByteBuf buf) {
        buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(true);
    }

    public static void handle(WarnPlayerPacket packet, IPayloadContext context) {
        context.enqueueWork(ClientHelper::handlePvpZoneWarning);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
