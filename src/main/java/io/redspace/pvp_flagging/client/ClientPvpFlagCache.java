package io.redspace.pvp_flagging.client;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class ClientPvpFlagCache {
    private static HashMap<UUID, Component> flaggedPlayerLookup = new HashMap<>();

    public static @Nullable Component getNameTag(Player player) {
        Component newTag = null;
        if (flaggedPlayerLookup.containsKey(player.getUUID())) {
            newTag = flaggedPlayerLookup.get(player.getUUID());
            if (newTag == null) {
                //TODO: this should be a translation
                newTag = player.getDisplayName().copy().append(" §c(PVP)");
                flaggedPlayerLookup.put(player.getUUID(), newTag);
            }
        }

        return newTag;
    }

    public static void handlePvpFLagUpdate(UUID playerUUID, boolean isFlagged) {
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
