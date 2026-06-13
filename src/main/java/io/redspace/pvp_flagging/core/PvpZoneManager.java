package io.redspace.pvp_flagging.core;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.data.PvpDataStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = PvpFlagging.MODID)
public class PvpZoneManager {
    public static Map<ResourceKey<Level>, PvpZoneManager> INSTANCES;
//    public static PvpZoneManager INSTANCE;

    //TODO: This should probably get called on the server started event.
    public static void init() {
//        INSTANCE = new PvpZoneManager();
        INSTANCES = new HashMap<>(3);
    }

    public static PvpZoneManager getInstance(Level level) {
        return getInstance(level.dimension());
    }

    public static PvpZoneManager getInstance(ResourceKey<Level> level) {
        //todo: INSTANCES is technically nullable. But not really. Should there be additional safety checks?
        return INSTANCES.computeIfAbsent(level, dim -> new PvpZoneManager());
    }

    /**
     * Map of PVP Zone name to pvpZones of name.
     */
    private final HashMap<String, PvpZone> pvpZones = new HashMap<>();
    private int boundsCheckTicks = 0;

    /**
     * Adds zone if name is not already present in manager
     *
     * @return Whether zone is added
     */
    public boolean addZone(PvpZone pvpZone) {
        if (!pvpZones.containsKey(pvpZone.getName())) {
            pvpZones.put(pvpZone.getName(), pvpZone);
            PvpDataStorage.markDirty();
            return true;
        }
        return false;
    }

    public boolean boundsCheckShouldWarn(Player player) {
        for (PvpZone zone : pvpZones.values()) {
            if (zone.getBufferedZoneBounds().contains(player.position())) {
                return true;
            }
        }
        return false;
    }

    public boolean boundsCheckShouldFlag(Player player) {
        for (PvpZone zone : pvpZones.values()) {
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
        if (pvpZones.remove(name) != null) {
            PvpDataStorage.markDirty();
        }
    }

    public Collection<PvpZone> getZones() {
        return pvpZones.values();
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        ListTag pvpZonesTag = new ListTag();
        for (PvpZone pvpZone : pvpZones.values()) {
            pvpZonesTag.add(pvpZone.save());
        }
        tag.put("pvpZones", pvpZonesTag);
        return tag;
    }

    public void load(CompoundTag nbt) {
        pvpZones.clear();
        if (!nbt.contains("pvpZones")) {
            return;
        }
        var pvpZonesTag = nbt.getList("pvpZones").orElse(new ListTag());
        pvpZonesTag.forEach(pvpZoneTag -> {
            var zone = PvpZone.loadFromNbt((CompoundTag) pvpZoneTag);
            pvpZones.put(zone.getName(), zone);
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PvpZoneManager instance = PvpZoneManager.getInstance(player.level());
        if (!instance.getZones().isEmpty()) {
            var server = player.level().getServer();
            if (server != null && server.overworld().getGameTime() % instance.boundsCheckTicks() == 0) {
                if (!PlayerFlagManager.INSTANCE.isPlayerFlagged(player)) {
                    if (instance.boundsCheckShouldFlag(player)) {
                        PlayerFlagManager.INSTANCE.flagPlayer(player);
                    } else if (instance.boundsCheckShouldWarn(player)) {
                        PlayerFlagManager.INSTANCE.warnPlayer(player);
                    }
                }
            }
        }
    }
}
