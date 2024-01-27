package io.redspace.pvp_flagging.core;

import java.util.ArrayList;

public class PvpZoneManager {
    public static PvpZoneManager INSTANCE;

    public static void init() {
        INSTANCE = new PvpZoneManager();
    }

    private ArrayList<PvpZone> pvpZones = new ArrayList<>();

    public boolean addZone(PvpZone pvpZone) {
        if (!pvpZones.contains(pvpZone)) {
            pvpZones.add(pvpZone);
            return true;
        }
        return false;
    }

    public void removeZone(String name) {
        pvpZones.stream()
                .filter(zone -> zone.name.equals(name))
                .findFirst()
                .ifPresent(zone -> pvpZones.remove(zone));
    }

    public ArrayList<PvpZone> getZones() {
        return pvpZones;
    }
}
