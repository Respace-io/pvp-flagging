package io.redspace.pvp_flagging.client;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class ClientPvpFlagCache {
    private static HashMap<UUID, Component> flaggedPlayerLookup = new HashMap<>();
    private static final Component nameTagIndicator = Component.translatable("ui.pvp_flagging.name_tag_indicator");

    public static @Nullable Component getNameTag(Player player) {
        Component newTag = null;
        if (flaggedPlayerLookup.containsKey(player.getUUID())) {
            newTag = flaggedPlayerLookup.get(player.getUUID());
            if (newTag == null) {
                newTag = player.getDisplayName().copy().append(nameTagIndicator);
                flaggedPlayerLookup.put(player.getUUID(), newTag);
            }
        }

        return newTag;
    }

    public static void handlePvpUnflagScheduled(int ticks) {
        if (ticks > 0) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_off_scheduled", ticks / 20).withStyle(ChatFormatting.RED), false);
        }
    }

    public static void handlePvpFlagUpdate(UUID playerUUID, boolean isFlagged) {
        var player = Minecraft.getInstance().player;
        if (player != null && playerUUID.equals(player.getUUID())) {
            if (isFlagged) {
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_on").withStyle(ChatFormatting.RED), false);
            } else {
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_off").withStyle(ChatFormatting.RED), false);
            }
        }

        if (isFlagged) {
            flaggedPlayerLookup.put(playerUUID, null);
        } else {
            flaggedPlayerLookup.remove(playerUUID);
        }
    }

    public static void handleFullPvpDataSync(ObjectSet<UUID> flaggedPlayers) {
        var tmp = new HashMap<UUID, Component>();
        flaggedPlayers.forEach((k) -> {
            tmp.put(k, null);
        });
        flaggedPlayerLookup = tmp;
    }
}
