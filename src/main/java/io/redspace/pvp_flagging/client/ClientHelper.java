package io.redspace.pvp_flagging.client;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.util.Logging;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class ClientHelper {
    private static HashMap<UUID, Component> flaggedPlayerLookup = new HashMap<>();
    public static final Component NAME_TAG_INDICATOR = Component.translatable("ui.pvp_flagging.name_tag_indicator");
    private static int unflagTimestamp = -1;

    @Deprecated(forRemoval = true)
    public static @Nullable Component getNameTag(Player player) {
        Component newTag = null;
        if (flaggedPlayerLookup.containsKey(player.getUUID())) {
            newTag = flaggedPlayerLookup.get(player.getUUID());
            if (newTag == null) {
                newTag = player.getDisplayName().copy().append(NAME_TAG_INDICATOR);
                flaggedPlayerLookup.put(player.getUUID(), newTag);
            }
        }

        return newTag;
    }

    public static Component modifyNameTag(Component nameTag) {
        return nameTag.copy().append(NAME_TAG_INDICATOR);
    }

    public static boolean isFlagged(UUID uuid) {
        return flaggedPlayerLookup.containsKey(uuid);
    }

    public static boolean isFlagged(Player player) {
        return isFlagged(player.getUUID());
    }

    public static void handlePvpUnflagScheduled(int ticks) {
        if (Logging.CLIENT_PVP_FLAG_CACHE) {
            PvpFlagging.LOGGER.debug("handlePvpUnflagScheduled: ticks:{}", ticks);
        }

        if (ticks > 0) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_off_scheduled", ticks / 20).withStyle(ChatFormatting.RED), false);
            if (Minecraft.getInstance().player != null) {
                unflagTimestamp = Minecraft.getInstance().player.tickCount + ticks;
            }
        }
    }

    public static void handlePvpUnflagScheduleCancelled(int ticks) {
        if (Logging.CLIENT_PVP_FLAG_CACHE) {
            PvpFlagging.LOGGER.debug("handlePvpUnflagScheduleCancelled: ticks:{}", ticks);
        }

        Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_off_scheduled_cancelled").withStyle(ChatFormatting.RED), false);
        unflagTimestamp = -1;
    }

    public static int getUnflagTimestamp() {
        return unflagTimestamp;
    }

    public static void resetUnflagTimestamp() {
        unflagTimestamp = -1;
    }

    public static void handlePvpZoneWarning() {
        var player = Minecraft.getInstance().player;

        if (Logging.CLIENT_PVP_FLAG_CACHE) {
            PvpFlagging.LOGGER.debug("handlePvpZoneWarning player:{}", player);
        }

        if (player != null && isFlagged(player)) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_zone.entry_warning.flagged").withStyle(ChatFormatting.RED), false);
        } else {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_zone.entry_warning.unflagged").withStyle(ChatFormatting.RED), false);
        }
    }

    public static void handlePvpFlagUpdate(UUID playerUUID, boolean isFlagged) {
        if (Logging.CLIENT_PVP_FLAG_CACHE) {
            PvpFlagging.LOGGER.debug("handlePvpFlagUpdate: playerUUID:{}, isFlagged:{}", playerUUID, isFlagged);
        }

        var player = Minecraft.getInstance().player;
        if (player != null && playerUUID.equals(player.getUUID())) {
            if (isFlagged) {
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_on").withStyle(ChatFormatting.RED), false);
            } else {
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("ui.pvp_flagging.pvp_off").withStyle(ChatFormatting.RED), false);
                resetUnflagTimestamp();
            }
        }

        if (isFlagged) {
            flaggedPlayerLookup.put(playerUUID, null);
        } else {
            flaggedPlayerLookup.remove(playerUUID);
        }
    }

    public static void handleFullPvpDataSync(ObjectSet<UUID> flaggedPlayers) {
        if (Logging.CLIENT_PVP_FLAG_CACHE) {
            PvpFlagging.LOGGER.debug("handleFullPvpDataSync: size:{}", flaggedPlayers.size());
        }

        var tmp = new HashMap<UUID, Component>();
        flaggedPlayers.forEach((k) -> {
            tmp.put(k, null);
        });
        flaggedPlayerLookup = tmp;
        unflagTimestamp = -1;
    }
}
