package dev.minceraft.sonus.plasmo.protocol.tcp.data.source;


import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.data.GameProfile;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CodecInfo;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class DirectSourceInfo extends SourceInfo {

    private final @Nullable GameProfile profile;
    private Vec3d relativePosition;
    private Vec3d lookAngle;
    private boolean cameraRelative;

    public DirectSourceInfo(ByteBuf buf) {
        super(buf, SourceType.DIRECT);
        this.profile = DataTypeUtil.readNullable(buf, b -> DataTypeUtil.INT.readGameProfile(b, Utf8String::readUnsignedShort));
        this.relativePosition = DataTypeUtil.readNullable(buf, Vec3d::decode);
        this.lookAngle = DataTypeUtil.readNullable(buf, Vec3d::decode);
        this.cameraRelative = buf.readBoolean();
    }

    public DirectSourceInfo(String addonId, UUID id, UUID voiceLineId, @Nullable String name,
                            byte state, @Nullable CodecInfo codecInfo, boolean stereo, boolean iconVisible, int angle,
                            @Nullable GameProfile profile, Vec3d relativePosition, Vec3d lookAngle, boolean cameraRelative) {
        super(SourceType.DIRECT, addonId, id, voiceLineId, name, state, codecInfo, stereo, iconVisible, angle);
        this.profile = profile;
        this.relativePosition = relativePosition;
        this.lookAngle = lookAngle;
        this.cameraRelative = cameraRelative;
    }

    @Override
    public void write(ByteBuf buf) {
        super.write(buf);
        DataTypeUtil.writeNullable(buf, this.profile, (b, profile) ->
                DataTypeUtil.INT.writeGameProfile(b, profile, Utf8String::writeUnsignedShort));
        DataTypeUtil.writeNullable(buf, this.relativePosition, Vec3d::encode);
        DataTypeUtil.writeNullable(buf, this.lookAngle, Vec3d::encode);
        buf.writeBoolean(this.cameraRelative);
    }

    @Nullable
    public GameProfile getProfile() {
        return this.profile;
    }

    public Vec3d getRelativePosition() {
        return this.relativePosition;
    }

    public void setRelativePosition(Vec3d relativePosition) {
        this.relativePosition = relativePosition;
    }

    public Vec3d getLookAngle() {
        return this.lookAngle;
    }

    public void setLookAngle(Vec3d lookAngle) {
        this.lookAngle = lookAngle;
    }

    public boolean isCameraRelative() {
        return this.cameraRelative;
    }

    public void setCameraRelative(boolean cameraRelative) {
        this.cameraRelative = cameraRelative;
    }
}
