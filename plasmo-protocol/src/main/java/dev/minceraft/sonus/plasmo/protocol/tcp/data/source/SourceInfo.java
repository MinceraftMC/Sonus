package dev.minceraft.sonus.plasmo.protocol.tcp.data.source;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CodecInfo;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public abstract class SourceInfo {

    protected final SourceType sourceType;
    protected final String addonId;
    protected final UUID id;
    protected UUID voiceLineId;
    protected final @Nullable String name;
    protected final @Nullable CodecInfo codecInfo;
    protected final boolean stereo;
    protected final boolean iconVisible;
    protected final int angle;
    protected byte state;

    protected SourceInfo(SourceType sourceType, String addonId, UUID id, UUID voiceLineId, @Nullable String name,
                         byte state, @Nullable CodecInfo codecInfo, boolean stereo, boolean iconVisible, int angle) {
        this.sourceType = sourceType;
        this.addonId = addonId;
        this.id = id;
        this.voiceLineId = voiceLineId;
        this.name = name;
        this.state = state;
        this.codecInfo = codecInfo;
        this.stereo = stereo;
        this.iconVisible = iconVisible;
        this.angle = angle;
    }

    protected SourceInfo(ByteBuf buf, SourceType sourceType) {
        this.sourceType = sourceType;
        this.addonId = Utf8String.readUnsignedShort(buf);
        this.id = DataTypeUtil.readUniqueId(buf);
        this.name = DataTypeUtil.readNullable(buf, Utf8String::readUnsignedShort);
        this.state = buf.readByte();
        this.codecInfo = DataTypeUtil.readNullable(buf, CodecInfo::new);
        this.stereo = buf.readBoolean();
        this.voiceLineId = DataTypeUtil.readUniqueId(buf);
        this.iconVisible = buf.readBoolean();
        this.angle = buf.readInt();
    }

    public void write(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.addonId);
        DataTypeUtil.writeUniqueId(buf, this.id);
        DataTypeUtil.writeNullable(buf, this.name, Utf8String::writeUnsignedShort);
        buf.writeByte(this.state);
        DataTypeUtil.writeNullable(buf, this.codecInfo, (b, c) -> c.write(b));
        buf.writeBoolean(this.stereo);
        DataTypeUtil.writeUniqueId(buf, this.voiceLineId);
        buf.writeBoolean(this.iconVisible);
        buf.writeInt(this.angle);
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public String getAddonId() {
        return this.addonId;
    }

    public UUID getId() {
        return this.id;
    }

    public UUID getVoiceLineId() {
        return this.voiceLineId;
    }

    public void setVoiceLineId(UUID voiceLineId) {
        this.voiceLineId = voiceLineId;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public byte getState() {
        return this.state;
    }
    
    public void setState(byte state) {
        this.state = state;
    }
    
    public void resetState() {
        this.state = 0;
    }
    
    public void markDirty(){
        this.state = 1;
    }
    
    public void markRemoved(){
        this.state = 10;
    }

    public @Nullable CodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public boolean isIconVisible() {
        return this.iconVisible;
    }

    public int getAngle() {
        return this.angle;
    }
}
