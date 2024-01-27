package io.redspace.pvp_flagging.core;

import io.redspace.pvp_flagging.data.PvpDataStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;

public class PvpZoneManager implements INBTSerializable<CompoundTag> {
    public static PvpZoneManager INSTANCE;

    public static void init() {
        INSTANCE = new PvpZoneManager();
    }

    private ArrayList<PvpZone> pvpZones = new ArrayList<>();

    public boolean addZone(PvpZone pvpZone) {
        if (!pvpZones.contains(pvpZone)) {
            pvpZones.add(pvpZone);
            PvpDataStorage.INSTANCE.setDirty();
            return true;
        }
        return false;
    }

    public void removeZone(String name) {
        pvpZones.stream()
                .filter(zone -> zone.name.equals(name))
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
}
