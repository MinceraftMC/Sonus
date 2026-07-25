package dev.minceraft.sonus.common.data;
// Created by booky10 in Sonus (01:16 17.07.2025)

import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class Vec3d {

    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);

    protected final double x;
    protected final double y;
    protected final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3d decode(ByteBuf buf) {
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void encode(ByteBuf buf, Vec3d vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
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

    public Vec3d add(Vec3d other) {
        return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3d sub(Vec3d other) {
        return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public double distanceSquared(Vec3d listenerPos) {
        double dx = this.x - listenerPos.getX();
        double dy = this.y - listenerPos.getY();
        double dz = this.z - listenerPos.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public String toString() {
        return "Vec3d[" + this.x + ", " + this.y + ", " + this.z + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec3d vec3d)) return false;
        return Double.compare(x, vec3d.x) == 0 && Double.compare(y, vec3d.y) == 0 && Double.compare(z, vec3d.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}