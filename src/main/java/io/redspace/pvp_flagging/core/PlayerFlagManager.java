package io.redspace.pvp_flagging.core;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.data.PvpDataStorage;
import io.redspace.pvp_flagging.network.*;
import io.redspace.pvp_flagging.util.Logging;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@EventBusSubscriber(modid = PvpFlagging.MODID)
public class PlayerFlagManager {
    public static PlayerFlagManager INSTANCE;

    private final int EXPECTED_SIZE = 20; //This will prevent rehashing when the collection grows and shrinks for most use cases
    private final ObjectSet<UUID> flaggedPlayers = new ObjectOpenHashSet<>(EXPECTED_SIZE);
    private final Int2ObjectMap<ScheduleUnflagItem> playersScheduledToUnflag = new Int2ObjectOpenHashMap<>(EXPECTED_SIZE);

    public static void init() {
        INSTANCE = new PlayerFlagManager();
    }

    public boolean isPlayerFlagged(@Nullable Player player) {
        return player != null && flaggedPlayers.contains(player.getUUID());
    }

    public boolean anyPlayersScheduledToUnflag() {
        return !playersScheduledToUnflag.isEmpty();
    }

    public boolean isScheduledToUnflag(ServerPlayer player) {
        return playersScheduledToUnflag.containsKey(player.getId());
    }

    public boolean areBothPlayersFlagged(Player player1, Player player2) {
        return flaggedPlayers.contains(player1.getUUID()) && flaggedPlayers.contains(player2.getUUID());
    }

    public void flagPlayer(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger flagPlayer:{}", serverPlayer);
        }

        if (serverPlayer != null) {
            cancelScheduledUnflag(serverPlayer);
            flaggedPlayers.add(serverPlayer.getUUID());
            PvpDataStorage.markDirty();
            PacketDistributor.sendToAllPlayers(new PvpFlagUpdatePacket(serverPlayer.getUUID(), true));
        }
    }

    public void warnPlayer(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger warnPlayer:{}", serverPlayer);
        }

        if (serverPlayer != null) {
            PacketDistributor.sendToPlayer(serverPlayer, new WarnPlayerPacket());
        }
    }

    public void cancelScheduledUnflag(@Nullable ServerPlayer serverPlayer) {
        if (serverPlayer != null && !playersScheduledToUnflag.isEmpty()) {
            if (playersScheduledToUnflag.remove(serverPlayer.getId()) != null) {
                if (Logging.PLAYER_FLAG_MANAGER) {
                    PvpFlagging.LOGGER.debug("PlayerFlagManger cancelScheduledUnflag:{}", serverPlayer);
                }

                PacketDistributor.sendToPlayer(serverPlayer, new PvpCancelScheduledUnflagPacket(0));
            }
        }
    }

    public void unflagPlayerImmediate(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger unflagPlayerImmediate:{}", serverPlayer);
        }

        if (serverPlayer != null && flaggedPlayers.contains(serverPlayer.getUUID())) {
            flaggedPlayers.remove(serverPlayer.getUUID());
            PvpDataStorage.markDirty();
            PacketDistributor.sendToAllPlayers(new PvpFlagUpdatePacket(serverPlayer.getUUID(), false));

            if (!playersScheduledToUnflag.isEmpty()) {
                playersScheduledToUnflag.remove(serverPlayer.getId());
            }
        }
    }

    public void unflagPlayer(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger unflagPlayer:{}", serverPlayer);
        }

        if (serverPlayer != null && flaggedPlayers.contains(serverPlayer.getUUID())) {
            if (Logging.PLAYER_FLAG_MANAGER) {
                PvpFlagging.LOGGER.debug("Player {} is scheduled to be unflagged", serverPlayer.getUUID());
            }

            var server = serverPlayer.level().getServer();
            long scheduledTick = 0;
            int waitTicks = 0;
            if (server != null) {
                waitTicks = PvpConfig.SERVER.UNFLAG_WAIT_TIME_TICKS.get();
                scheduledTick = server.overworld().getGameTime() + waitTicks;
            }

            playersScheduledToUnflag.put(serverPlayer.getId(), new ScheduleUnflagItem(serverPlayer, scheduledTick));
            PacketDistributor.sendToPlayer(serverPlayer, new PvpUnflagScheduledPacket(waitTicks));
        }
    }

    public void syncToPlayer(ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger syncToPlayer:{}, count:{}", serverPlayer, flaggedPlayers.size());
        }

        if (!flaggedPlayers.isEmpty()) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncPvpDataPacket(flaggedPlayers));
        }
    }

    public void processScheduledUnflags(long gameTime) {
        if (!playersScheduledToUnflag.isEmpty()) {
            playersScheduledToUnflag.values().stream()
                    .filter(unflagItem -> unflagItem.scheduledTick < gameTime)
                    .toList()
                    .forEach(unflagItem -> {
                        playersScheduledToUnflag.remove(unflagItem.serverPlayer.getId());

                        if (flaggedPlayers.contains(unflagItem.serverPlayer.getUUID())) {
                            flaggedPlayers.remove(unflagItem.serverPlayer.getUUID());
                            PvpDataStorage.markDirty();
                            PacketDistributor.sendToAllPlayers(new PvpFlagUpdatePacket(unflagItem.serverPlayer.getUUID(), false));
                        }

                        if (Logging.PLAYER_FLAG_MANAGER) {
                            PvpFlagging.LOGGER.debug("PlayerFlagManger processScheduledUnflags unflagged:{} ", unflagItem.serverPlayer);
                        }
                    });
        }
    }

    @SubscribeEvent
    public static void handleServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().overworld().getGameTime();
        if (gameTime % 20 == 0) {
            PlayerFlagManager.INSTANCE.processScheduledUnflags(gameTime);
        }
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        ListTag uuids = new ListTag();
        for (UUID flaggedPlayer : flaggedPlayers) {
            uuids.add(StringTag.valueOf(flaggedPlayer.toString()));
        }
        tag.put("flaggedPlayers", uuids);
        return tag;
    }

    public void load(CompoundTag nbt) {
        flaggedPlayers.clear();
        if (!nbt.contains("flaggedPlayers")) {
            return;
        }
        ListTag list = nbt.getList("flaggedPlayers").orElse(new ListTag());
        for (Tag uuidTag : list) {
            try {
                if (uuidTag instanceof StringTag stringTag) {
                    flaggedPlayers.add(UUID.fromString(stringTag.value()));
                } else if (uuidTag instanceof IntArrayTag intArrayTag) {
                    flaggedPlayers.add(uuidFromIntArray(intArrayTag));
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * lazy reimplementation of int-array uuid serialization to easily port old nbt based serialization logic.
     * todo: swap all serialization to codecs
     */
    private static UUID uuidFromIntArray(IntArrayTag tag) {
        int[] data = tag.getAsIntArray();
        if (data.length != 4) {
            throw new IllegalArgumentException("Invalid UUID int array length");
        }
        return new UUID((long) data[0] << 32 | (data[1] & 0xFFFFFFFFL), (long) data[2] << 32 | (data[3] & 0xFFFFFFFFL));
    }

    public static class ScheduleUnflagItem {
        private final ServerPlayer serverPlayer;
        private final long scheduledTick;

        public ScheduleUnflagItem(ServerPlayer serverPlayer, long scheduledTick) {
            this.serverPlayer = serverPlayer;
            this.scheduledTick = scheduledTick;
        }

        @Override
        public int hashCode() {
            return serverPlayer.hashCode();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return serverPlayer.equals(obj);
        }
    }
}
