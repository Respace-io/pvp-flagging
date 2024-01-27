package io.redspace.pvp_flagging.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

public class ZoneBounds implements INBTSerializable<CompoundTag> {
    public int minX;
    public int minZ;
    public int maxX;
    public int maxZ;

    private ZoneBounds() {
    }

    public ZoneBounds(int pX1, int pZ1, int pX2, int pZ2) {
        this.minX = Math.min(pX1, pX2);
        this.minZ = Math.min(pZ1, pZ2);
        this.maxX = Math.max(pX1, pX2);
        this.maxZ = Math.max(pZ1, pZ2);
    }

    public ZoneBounds(BlockPos pPos) {
        this(pPos.getX(), pPos.getZ(), (pPos.getX() + 1), (pPos.getZ() + 1));
    }

    public ZoneBounds(BlockPos pStart, BlockPos pEnd) {
        this(pStart.getX(), pStart.getZ(), pEnd.getX(), pEnd.getZ());
    }

    public ZoneBounds(Vec3 pStart, Vec3 pEnd) {
        this((int) pStart.x, (int) pStart.z, (int) pEnd.x, (int) pEnd.z);
    }

    public ZoneBounds setMinX(int pMinX) {
        return new ZoneBounds(pMinX, this.minZ, this.maxX, this.maxZ);
    }

    public ZoneBounds setMinZ(int pMinZ) {
        return new ZoneBounds(this.minX, pMinZ, this.maxX, this.maxZ);
    }

    public ZoneBounds setMaxX(int pMaxX) {
        return new ZoneBounds(this.minX, this.minZ, pMaxX, this.maxZ);
    }

    public ZoneBounds setMaxZ(int pMaxZ) {
        return new ZoneBounds(this.minX, this.minZ, this.maxX, pMaxZ);
    }

    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof ZoneBounds)) {
            return false;
        } else {
            ZoneBounds ZoneBounds = (ZoneBounds) pOther;
            if (Double.compare(ZoneBounds.minX, this.minX) != 0) {
                return false;
            } else if (Double.compare(ZoneBounds.minZ, this.minZ) != 0) {
                return false;
            } else if (Double.compare(ZoneBounds.maxX, this.maxX) != 0) {
                return false;
            } else {
                return Double.compare(ZoneBounds.maxZ, this.maxZ) == 0;
            }
        }
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.minX);
        int j = (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minZ);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxX);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxZ);
        return 31 * j + (int) (i ^ i >>> 32);
    }

    public ZoneBounds contract(int pX, int pZ) {
        int d0 = this.minX;
        int d2 = this.minZ;
        int d3 = this.maxX;
        int d5 = this.maxZ;
        if (pX < 0.0D) {
            d0 -= pX;
        } else if (pX > 0.0D) {
            d3 -= pX;
        }

        if (pZ < 0.0D) {
            d2 -= pZ;
        } else if (pZ > 0.0D) {
            d5 -= pZ;
        }

        return new ZoneBounds(d0, d2, d3, d5);
    }

    public ZoneBounds expandTowards(Vec3 pVector) {
        return this.expandTowards((int) pVector.x, (int) pVector.z);
    }

    public ZoneBounds expandTowards(int pX, int pZ) {
        int d0 = this.minX;
        int d2 = this.minZ;
        int d3 = this.maxX;
        int d5 = this.maxZ;
        if (pX < 0.0D) {
            d0 += pX;
        } else if (pX > 0.0D) {
            d3 += pX;
        }

        if (pZ < 0.0D) {
            d2 += pZ;
        } else if (pZ > 0.0D) {
            d5 += pZ;
        }

        return new ZoneBounds(d0, d2, d3, d5);
    }

    public ZoneBounds inflate(int pX, int pZ) {
        int d0 = this.minX - pX;
        int d2 = this.minZ - pZ;
        int d3 = this.maxX + pX;
        int d5 = this.maxZ + pZ;
        return new ZoneBounds(d0, d2, d3, d5);
    }

    public ZoneBounds inflate(int pValue) {
        return this.inflate(pValue, pValue);
    }

    public ZoneBounds intersect(ZoneBounds pOther) {
        int d0 = Math.max(this.minX, pOther.minX);
        int d2 = Math.max(this.minZ, pOther.minZ);
        int d3 = Math.min(this.maxX, pOther.maxX);
        int d5 = Math.min(this.maxZ, pOther.maxZ);
        return new ZoneBounds(d0, d2, d3, d5);
    }

    public ZoneBounds minmax(ZoneBounds pOther) {
        int d0 = Math.min(this.minX, pOther.minX);
        int d2 = Math.min(this.minZ, pOther.minZ);
        int d3 = Math.max(this.maxX, pOther.maxX);
        int d5 = Math.max(this.maxZ, pOther.maxZ);
        return new ZoneBounds(d0, d2, d3, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    public ZoneBounds move(int pX, int pY, int pZ) {
        return new ZoneBounds(this.minX + pX, this.minZ + pZ, this.maxX + pX, this.maxZ + pZ);
    }

    public ZoneBounds move(BlockPos pPos) {
        return new ZoneBounds(this.minX + pPos.getX(), this.minZ + pPos.getZ(), this.maxX + pPos.getX(), this.maxZ + pPos.getZ());
    }

    public ZoneBounds move(Vec3 pVec) {
        return this.move((int) pVec.x, (int) pVec.y, (int) pVec.z);
    }

    /**
     * Checks if the bounding box intersects with another.
     */
    public boolean intersects(ZoneBounds pOther) {
        return this.intersects(pOther.minX, pOther.minZ, pOther.maxX, pOther.maxZ);
    }

    public boolean intersects(double pX1, double pZ1, double pX2, double pZ2) {
        return this.minX < pX2 && this.maxX > pX1 && this.minZ < pZ2 && this.maxZ > pZ1;
    }

    public boolean intersects(Vec3 pMin, Vec3 pMax) {
        return this.intersects(Math.min(pMin.x, pMax.x), Math.min(pMin.z, pMax.z), Math.max(pMin.x, pMax.x), Math.max(pMin.z, pMax.z));
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean contains(Vec3 pVec) {
        return this.contains(pVec.x, pVec.z);
    }

    public boolean contains(double pX, double pZ) {
        return pX >= this.minX && pX < this.maxX && pZ >= this.minZ && pZ < this.maxZ;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getSize() {
        double d0 = this.getXsize();
        double d2 = this.getZsize();
        return (d0 + d2) / 2.0D;
    }

    public double getXsize() {
        return this.maxX - this.minX;
    }

    public double getZsize() {
        return this.maxZ - this.minZ;
    }

    public ZoneBounds deflate(int pX, int pY, int pZ) {
        return this.inflate(-pX, -pZ);
    }

    public ZoneBounds deflate(int pValue) {
        return this.inflate(-pValue);
    }

    public double distanceToSqr(Vec3 pVec) {
        double d0 = Math.max(Math.max(this.minX - pVec.x, pVec.x - this.maxX), 0.0D);
        double d2 = Math.max(Math.max(this.minZ - pVec.z, pVec.z - this.maxZ), 0.0D);
        return Mth.lengthSquared(d0, d2);
    }

    public String toString() {
        return "ZoneBounds[" + this.minX + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxZ + "]";
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxZ);
    }

    public Vec3 getCenter() {
        return new Vec3(Mth.lerp(0.5D, this.minX, this.maxX), 0, Mth.lerp(0.5D, this.minZ, this.maxZ));
    }

    public static ZoneBounds ofSize(Vec3 pCenter, int pXSize, int pZSize) {
        return new ZoneBounds((int) pCenter.x - pXSize / 2, (int) pCenter.z - pZSize / 2, (int) pCenter.x + pXSize / 2, (int) pCenter.z + pZSize / 2);
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putInt("minX", minX);
        tag.putInt("minZ", minZ);
        tag.putInt("maxX", maxX);
        tag.putInt("maxZ", maxZ);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        minX = nbt.getInt("minX");
        minZ = nbt.getInt("minZ");
        maxX = nbt.getInt("maxX");
        maxZ = nbt.getInt("maxZ");
    }

    public static ZoneBounds getZoneBounds(CompoundTag nbt) {
        var zoneBounds = new ZoneBounds();
        zoneBounds.deserializeNBT(nbt);
        return zoneBounds;
    }
}