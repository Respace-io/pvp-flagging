package io.redspace.pvp_flagging.core;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PvpZone {
    public final String name;
    public final int buffer;
    public final ZoneBounds zoneBounds;
    private ZoneBounds bufferedZoneBounds = null;

    public PvpZone(@NotNull String name, int x1, int z1, int x2, int z2, int buffer) {
        this.zoneBounds = new ZoneBounds(x1, z1, x2, z2);
        this.name = Objects.requireNonNull(name);
        this.buffer = buffer;
    }

    public ZoneBounds getBufferedZoneBounds() {
        if (bufferedZoneBounds == null) {
            bufferedZoneBounds = this.zoneBounds.inflate(buffer);
        }
        return bufferedZoneBounds;
    }

    public ZoneBounds getBounds() {
        return this.zoneBounds;
    }

    boolean contains(Vec3 pos) {
        return zoneBounds.contains(pos.x, pos.z);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PvpZone pvpZone) && this.name != null && this.name.equals(pvpZone.name);
    }

    @Override
    public String toString() {
        return String.format("Name:%s, Buffer:%d, %s", name, buffer, zoneBounds);
    }
}
