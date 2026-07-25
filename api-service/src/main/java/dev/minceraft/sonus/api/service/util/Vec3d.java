package dev.minceraft.sonus.api.service.util;

public class Vec3d {

    public static final Vec3d ZERO = new Vec3d();

    protected final double x;
    protected final double y;
    protected final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d() {
        this(0, 0, 0);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
