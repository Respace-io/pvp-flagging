package io.redspace.pvp_flagging.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PvpZone implements INBTSerializable<CompoundTag> {
    public String name;
    public int buffer;
    public ZoneBounds zoneBounds;
    private ZoneBounds bufferedZoneBounds = null;

    private PvpZone() {
    }

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

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putString("name", name);
        tag.putInt("buffer", buffer);
        tag.put("zoneBounds", zoneBounds.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        name = nbt.getString("name");
        buffer = nbt.getInt("buffer");
        zoneBounds = ZoneBounds.getZoneBounds(nbt.getCompound("zoneBounds"));
    }

    public static PvpZone getPvpZone(CompoundTag nbt) {
        var pvpZone = new PvpZone();
        pvpZone.deserializeNBT(nbt);
        return pvpZone;
    }
}
