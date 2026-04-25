package dev.minceraft.sonus.api.service.data;

import net.kyori.adventure.key.Key;

import java.util.Objects;

public class WorldRotatedVec3d extends RotatedVec3d {

    protected Key dimension;

    public WorldRotatedVec3d(double x, double y, double z, float yaw, float pitch, Key dimension) {
        super(x, y, z, yaw, pitch);
        this.dimension = dimension;
    }

    public Key getDimension() {
        return this.dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorldRotatedVec3d)) return false;
        if (!super.equals(o)) return false;
        WorldRotatedVec3d that = (WorldRotatedVec3d) o;
        return Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dimension);
    }
}
