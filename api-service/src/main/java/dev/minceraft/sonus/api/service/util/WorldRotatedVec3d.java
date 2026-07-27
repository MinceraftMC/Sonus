package dev.minceraft.sonus.api.service.util;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WorldRotatedVec3d extends Vec3d {

    protected final float yaw;
    protected final float pitch;
    protected final Key dimension;

    public WorldRotatedVec3d(double x, double y, double z, float yaw, float pitch, Key dimension) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Key getDimension() {
        return this.dimension;
    }
}
