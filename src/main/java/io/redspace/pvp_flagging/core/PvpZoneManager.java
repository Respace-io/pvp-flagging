package io.redspace.pvp_flagging.core;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.data.PvpDataStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = PvpFlagging.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PvpZoneManager implements INBTSerializable<CompoundTag> {
    public static PvpZoneManager INSTANCE;

    //TODO: This should probably get called on the server started event.
    public static void init() {
        INSTANCE = new PvpZoneManager();
    }

    private final ArrayList<PvpZone> pvpZones = new ArrayList<>();
    private int boundsCheckTicks = 0;

    public boolean addZone(PvpZone pvpZone) {
        if (!pvpZones.contains(pvpZone)) {
            pvpZones.add(pvpZone);
            PvpDataStorage.INSTANCE.setDirty();
            return true;
        }
        return false;
    }

    public boolean boundsCheckShouldWarn(Player player) {
        for (int i = 0; i < pvpZones.size(); i++) {
            var zone = pvpZones.get(i);

            if (zone.getBufferedZoneBounds().contains(player.position())) {
                return true;
            }
        }
        return false;
    }

    public boolean boundsCheckShouldFlag(Player player) {
        for (int i = 0; i < pvpZones.size(); i++) {
            var zone = pvpZones.get(i);

            if (zone.contains(player.position())) {
                return true;
            }
        }
        return false;
    }

    public int boundsCheckTicks() {
        if (boundsCheckTicks == 0) {
            boundsCheckTicks = PvpConfig.SERVER.PVP_ZONE_BOUNDS_CHECK_TICKS.get();
        }

        return boundsCheckTicks;
    }

    public void removeZone(String name) {
        pvpZones.stream()
                .filter(zone -> zone.getName().equals(name))
                .findFirst()
                .ifPresent(zone -> pvpZones.remove(zone));

        PvpDataStorage.INSTANCE.setDirty();
    }

    public ArrayList<PvpZone> getZones() {
        return pvpZones;
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        ListTag pvpZonesTag = new ListTag();
        for (PvpZone pvpZone : pvpZones) {
            pvpZonesTag.add(pvpZone.serializeNBT());
        }
        tag.put("pvpZones", pvpZonesTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("pvpZones")) {
            var pvpZonesTag = nbt.getList("pvpZones", CompoundTag.TAG_COMPOUND);
            pvpZonesTag.forEach(pvpZoneTag -> {
                pvpZones.add(PvpZone.getPvpZone((CompoundTag) pvpZoneTag));
            });
        }
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && !INSTANCE.getZones().isEmpty()) {
            var player = event.player;
            var server = player.getServer();
            if (server != null && server.overworld().getGameTime() % INSTANCE.boundsCheckTicks() == 0 && !PlayerFlagManager.INSTANCE.isPlayerFlagged(player)) {
                if (INSTANCE.boundsCheckShouldFlag(player)) {
                    PlayerFlagManager.INSTANCE.flagPlayer((ServerPlayer) player);
                } else if (INSTANCE.boundsCheckShouldWarn(player)) {
                    PlayerFlagManager.INSTANCE.warnPlayer((ServerPlayer) player);
                }
            }
        }
    }
}
