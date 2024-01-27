package io.redspace.pvp_flagging.core;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.data.PvpDataStorage;
import io.redspace.pvp_flagging.network.ClientboundPvpFlagUpdate;
import io.redspace.pvp_flagging.network.ClientboundSyncPvpData;
import io.redspace.pvp_flagging.network.ClientbountPvpUnflagScheduled;
import io.redspace.pvp_flagging.registries.Network;
import io.redspace.pvp_flagging.util.Logging;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = PvpFlagging.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerFlagManager implements INBTSerializable<CompoundTag> {
    public static PlayerFlagManager INSTANCE;

    private final ObjectSet<UUID> flaggedPlayers = new ObjectOpenHashSet<>();
    private final Queue<ScheduleUnflagItem> playersToUnflag = new ConcurrentLinkedQueue<>();

    public static void init() {
        INSTANCE = new PlayerFlagManager();
    }

    public boolean isPlayerFlagged(@Nullable Player player) {
        return player != null && flaggedPlayers.contains(player.getUUID());
    }

    public boolean areBothPlayersFlagged(Player player1, Player player2) {
        return flaggedPlayers.contains(player1.getUUID()) && flaggedPlayers.contains(player2.getUUID());
    }

    public void flagPlayer(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger flagPlayer:{}", serverPlayer);
        }

        if (serverPlayer != null) {
            flaggedPlayers.add(serverPlayer.getUUID());
            PvpDataStorage.INSTANCE.setDirty();
            Network.sendToAllPlayers(new ClientboundPvpFlagUpdate(serverPlayer.getUUID(), true));
        }
    }

    public void unflagPlayerImmediate(@Nullable ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger unflagPlayerImmediate:{}", serverPlayer);
        }

        if (serverPlayer != null && flaggedPlayers.contains(serverPlayer.getUUID())) {
            flaggedPlayers.remove(serverPlayer.getUUID());
            PvpDataStorage.INSTANCE.setDirty();
            Network.sendToAllPlayers(new ClientboundPvpFlagUpdate(serverPlayer.getUUID(), false));
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

            var server = serverPlayer.getServer();
            long scheduledTick = 0;
            int waitTicks = 0;
            if (server != null) {
                waitTicks = PvpConfig.SERVER.UNFLAG_WAIT_TIME_TICKS.get();
                scheduledTick = server.overworld().getGameTime() + waitTicks;
            }

            playersToUnflag.add(new ScheduleUnflagItem(serverPlayer, scheduledTick));
            Network.sendToPlayer(new ClientbountPvpUnflagScheduled(waitTicks), serverPlayer);
        }
    }

    public void syncToPlayer(ServerPlayer serverPlayer) {
        if (Logging.PLAYER_FLAG_MANAGER) {
            PvpFlagging.LOGGER.debug("PlayerFlagManger syncToPlayer:{}, count:{}", serverPlayer, flaggedPlayers.size());
        }

        if (!flaggedPlayers.isEmpty()) {
            Network.sendToPlayer(new ClientboundSyncPvpData(flaggedPlayers), serverPlayer);
        }
    }

    public void processScheduledUnflags(long gameTime) {
        boolean done = false;

        while (!done) {
            var unflagItem = playersToUnflag.peek();

            if (unflagItem == null) {
                done = true;
            } else {
                if (unflagItem.scheduledTick < gameTime) {
                    playersToUnflag.remove();
                    flaggedPlayers.remove(unflagItem.serverPlayer.getUUID());
                    PvpDataStorage.INSTANCE.setDirty();
                    Network.sendToAllPlayers(new ClientboundPvpFlagUpdate(unflagItem.serverPlayer.getUUID(), false));

                    if (Logging.PLAYER_FLAG_MANAGER) {
                        PvpFlagging.LOGGER.debug("PlayerFlagManger processScheduledUnflags unflagged:{} ", unflagItem.serverPlayer);
                    }
                } else {
                    done = true;
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long gameTime = event.getServer().overworld().getGameTime();
            if (gameTime % 20 == 0) {
                PlayerFlagManager.INSTANCE.processScheduledUnflags(gameTime);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        ListTag uuids = new ListTag();
        for (UUID flaggedPlayer : flaggedPlayers) {
            uuids.add(NbtUtils.createUUID(flaggedPlayer));
        }
        tag.put("flaggedPlayers", uuids);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag list = nbt.getList("flaggedPlayers", CompoundTag.TAG_INT_ARRAY);
        for (Tag uuidTag : list) {
            try {
                var uuid = NbtUtils.loadUUID(uuidTag);
                flaggedPlayers.add(uuid);
            } catch (Exception ignored) {
                continue;
            }
        }
    }

    public static record ScheduleUnflagItem(ServerPlayer serverPlayer, long scheduledTick) {
    }
}
