package io.redspace.pvp_flagging.core;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ZoneBounds {
    private static final double EPSILON = 1.0E-7D;
    public final double minX;
    public final double minZ;
    public final double maxX;
    public final double maxZ;

    public ZoneBounds(double pX1, double pZ1, double pX2, double pZ2) {
        this.minX = Math.min(pX1, pX2);
        this.minZ = Math.min(pZ1, pZ2);
        this.maxX = Math.max(pX1, pX2);
        this.maxZ = Math.max(pZ1, pZ2);
    }

    public ZoneBounds(BlockPos pPos) {
        this((double) pPos.getX(), (double) pPos.getZ(), (double) (pPos.getX() + 1), (double) (pPos.getZ() + 1));
    }

    public ZoneBounds(BlockPos pStart, BlockPos pEnd) {
        this((double) pStart.getX(), (double) pStart.getZ(), (double) pEnd.getX(), (double) pEnd.getZ());
    }

    public ZoneBounds(Vec3 pStart, Vec3 pEnd) {
        this(pStart.x, pStart.z, pEnd.x, pEnd.z);
    }

    public ZoneBounds setMinX(double pMinX) {
        return new ZoneBounds(pMinX, this.minZ, this.maxX, this.maxZ);
    }

    public ZoneBounds setMinZ(double pMinZ) {
        return new ZoneBounds(this.minX, pMinZ, this.maxX, this.maxZ);
    }

    public ZoneBounds setMaxX(double pMaxX) {
        return new ZoneBounds(this.minX, this.minZ, pMaxX, this.maxZ);
    }

    public ZoneBounds setMaxZ(double pMaxZ) {
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

    /**
     * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount, with positive changes decreasing
     * max values and negative changes increasing min values.
     * <br/>
     * If the amount to contract by is larger than the length of a side, then the side will wrap (still creating a valid
     * ZoneBounds - see last sample).
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(2, 2, 2)</code></pre></td><td><pre><samp>box[0.0,
     * 0.0, 0.0 -> 2.0, 2.0, 2.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(-2, -2, -
     * 2)</code></pre></td><td><pre><samp>box[2.0, 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).contract(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0,
     * 5.0, 6.0 -> 7.0, 6.0, 7.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(-2, -2, -2, 2, 2, 2).contract(4, -4, 0)</code></pre></td><td><pre><samp>box[-
     * 8.0, 2.0, -2.0 -> -2.0, 8.0, 2.0]</samp></pre></td></tr>
     * </table>
     *
     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #expand(double, double, double)} - like this, except for expanding.</li>
     * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
     * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
     * </ul>
     *
     * @return A new modified bounding box.
     */
    public ZoneBounds contract(double pX, double pZ) {
        double d0 = this.minX;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d5 = this.maxZ;
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
        return this.expandTowards(pVector.x, pVector.z);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that has been expanded by the given amount, with positive changes increasing
     * max values and negative changes decreasing min values.
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(2, 2, 2)</code></pre></td><td><pre><samp>box[0, 0, 0
     * -> 3, 3, 3]</samp></pre></td><td>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(-2, -2, -2)</code></pre></td><td><pre><samp>box[-2,
     * -2, -2 -> 1, 1, 1]</samp></pre></td><td>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).expand(0, 1, -1)</code></pre></td><td><pre><samp>box[5, 5,
     * 4, 7, 8, 7]</samp></pre></td><td>
     * </table>
     *
     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #contract(double, double, double)} - like this, except for shrinking.</li>
     * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
     * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
     * </ul>
     *
     * @return A modified bounding box that will always be equal or greater in volume to this bounding box.
     */
    public ZoneBounds expandTowards(double pX, double pZ) {
        double d0 = this.minX;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d5 = this.maxZ;
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

    /**
     * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount in both directions. Negative
     * values will shrink the ZoneBounds instead of expanding it.
     * <br/>
     * Side lengths will be increased by 2 times the value of the parameters, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid ZoneBounds - see last ample).
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(2, 2, 2)</code></pre></td><td><pre><samp>box[-2.0, -
     * 2.0, -2.0 -> 3.0, 3.0, 3.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 6, 6, 6).grow(-2, -2, -2)</code></pre></td><td><pre><samp>box[2.0,
     * 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).grow(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0,
     * 4.0, 6.0 -> 7.0, 8.0, 6.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(1, 1, 1, 3, 3, 3).grow(-4, -2, -3)</code></pre></td><td><pre><samp>box[-1.0,
     * 1.0, 0.0 -> 5.0, 3.0, 4.0]</samp></pre></td></tr>
     * </table>
     *
     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #expand(double, double, double)} - expands in only one direction.</li>
     * <li>{@link #contract(double, double, double)} - contracts in only one direction.</li>
     * <lu>{@link #grow(double)} - version of this that expands in all directions from one parameter.</li>
     * <li>{@link #shrink(double)} - contracts in all directions</li>
     * </ul>
     *
     * @return A modified bounding box.
     */
    public ZoneBounds inflate(double pX, double pZ) {
        double d0 = this.minX - pX;
        double d2 = this.minZ - pZ;
        double d3 = this.maxX + pX;
        double d5 = this.maxZ + pZ;
        return new ZoneBounds(d0, d2, d3, d5);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
     * #grow(double, double, double)} with the given value for all 3 params. Negative values will shrink the ZoneBounds.
     * <br/>
     * Side lengths will be increased by 2 times the value of the parameter, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid ZoneBounds - see samples on {@link #grow(double, double, double)}).
     *
     * @return A modified ZoneBounds.
     */
    public ZoneBounds inflate(double pValue) {
        return this.inflate(pValue, pValue);
    }

    public ZoneBounds intersect(ZoneBounds pOther) {
        double d0 = Math.max(this.minX, pOther.minX);
        double d2 = Math.max(this.minZ, pOther.minZ);
        double d3 = Math.min(this.maxX, pOther.maxX);
        double d5 = Math.min(this.maxZ, pOther.maxZ);
        return new ZoneBounds(d0, d2, d3, d5);
    }

    public ZoneBounds minmax(ZoneBounds pOther) {
        double d0 = Math.min(this.minX, pOther.minX);
        double d2 = Math.min(this.minZ, pOther.minZ);
        double d3 = Math.max(this.maxX, pOther.maxX);
        double d5 = Math.max(this.maxZ, pOther.maxZ);
        return new ZoneBounds(d0, d2, d3, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    public ZoneBounds move(double pX, double pY, double pZ) {
        return new ZoneBounds(this.minX + pX, this.minZ + pZ, this.maxX + pX, this.maxZ + pZ);
    }

    public ZoneBounds move(BlockPos pPos) {
        return new ZoneBounds(this.minX + (double) pPos.getX(), this.minZ + (double) pPos.getZ(), this.maxX + (double) pPos.getX(), this.maxZ + (double) pPos.getZ());
    }

    public ZoneBounds move(Vec3 pVec) {
        return this.move(pVec.x, pVec.y, pVec.z);
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

    public ZoneBounds deflate(double pX, double pY, double pZ) {
        return this.inflate(-pX, -pZ);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
     * #grow(double)} with value set to the negative of the value provided here. Passing a negative value to this method
     * values will grow the ZoneBounds.
     * <br/>
     * Side lengths will be decreased by 2 times the value of the parameter, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid ZoneBounds - see samples on {@link #grow(double, double, double)}).
     *
     * @return A modified ZoneBounds.
     */
    public ZoneBounds deflate(double pValue) {
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

    public static ZoneBounds ofSize(Vec3 pCenter, double pXSize, double pZSize) {
        return new ZoneBounds(pCenter.x - pXSize / 2.0D, pCenter.z - pZSize / 2.0D, pCenter.x + pXSize / 2.0D, pCenter.z + pZSize / 2.0D);
    }
}