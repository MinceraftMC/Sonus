package dev.minceraft.sonus.plasmo.protocol.tcp.data.source;


import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CodecInfo;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class StaticSourceInfo extends SourceInfo {

    private Vec3d position;
    private Vec3d lookAngle;

    public StaticSourceInfo(ByteBuf buf) {
        super(buf, SourceType.STATIC);
        this.position = Vec3d.decode(buf);
        this.lookAngle = Vec3d.decode(buf);
    }

    public StaticSourceInfo(String addonId, UUID id, UUID voiceLineId, @Nullable String name,
                            byte state, @Nullable CodecInfo codecInfo, boolean stereo, boolean iconVisible, int angle,
                            Vec3d position, Vec3d lookAngle) {
        super(SourceType.STATIC, addonId, id, voiceLineId, name, state, codecInfo, stereo, iconVisible, angle);
        this.position = position;
        this.lookAngle = lookAngle;
    }

    @Override
    public void write(ByteBuf buf) {
        super.write(buf);
        Vec3d.encode(buf, this.position);
        Vec3d.encode(buf, this.lookAngle);
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public Vec3d getLookAngle() {
        return this.lookAngle;
    }

    public void setLookAngle(Vec3d lookAngle) {
        this.lookAngle = lookAngle;
    }
}
