package dev.minceraft.sonus.plasmo.protocol.tcp.data;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class VoicePlayerInfo {

    private UUID uniqueId;
    private String name;
    private boolean muted;
    private boolean voiceDisabled;
    private boolean microphoneDisabled;

    public VoicePlayerInfo(ByteBuf buf) {
        this.uniqueId = DataTypeUtil.readUniqueId(buf);
        this.name = Utf8String.readUnsignedShort(buf);
        this.muted = buf.readBoolean();
        this.voiceDisabled = buf.readBoolean();
        this.microphoneDisabled = buf.readBoolean();
    }

    public VoicePlayerInfo(UUID uniqueId, String name, boolean muted, boolean voiceDisabled, boolean microphoneDisabled) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.muted = muted;
        this.voiceDisabled = voiceDisabled;
        this.microphoneDisabled = microphoneDisabled;
    }

    public void write(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.uniqueId);
        Utf8String.writeUnsignedShort(buf, this.name);
        buf.writeBoolean(this.muted);
        buf.writeBoolean(this.voiceDisabled);
        buf.writeBoolean(this.microphoneDisabled);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isVoiceDisabled() {
        return this.voiceDisabled;
    }

    public void setVoiceDisabled(boolean voiceDisabled) {
        this.voiceDisabled = voiceDisabled;
    }

    public boolean isMicrophoneDisabled() {
        return this.microphoneDisabled;
    }

    public void setMicrophoneDisabled(boolean microphoneDisabled) {
        this.microphoneDisabled = microphoneDisabled;
    }
}
