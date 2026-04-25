package dev.minceraft.sonus.api.service.data;

import java.util.Objects;

public class RotatedVec3d extends Vec3d {

    protected final float yaw;
    protected final float pitch;

    public RotatedVec3d(double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RotatedVec3d that)) return false;
        if (!super.equals(o)) return false;
        return Float.compare(yaw, that.yaw) == 0 && Float.compare(pitch, that.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), yaw, pitch);
    }
}
