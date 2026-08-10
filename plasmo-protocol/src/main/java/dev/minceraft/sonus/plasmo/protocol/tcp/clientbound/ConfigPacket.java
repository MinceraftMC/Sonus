package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CaptureInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.EncryptionInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceActivation;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceSourceLine;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@NullMarked
public class ConfigPacket extends ConfigPlayerInfoPacket<ConfigPacket> {

    private @MonotonicNonNull UUID serverId;

    private @MonotonicNonNull CaptureInfo captureInfo;
    private @Nullable EncryptionInfo encryptionInfo;
    private @MonotonicNonNull Collection<VoiceSourceLine> sourceLines;
    private @MonotonicNonNull Collection<VoiceActivation> activations;

    public ConfigPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.serverId);
        this.captureInfo.write(buf);
        DataTypeUtil.writeNullable(buf, this.encryptionInfo, (b, e) -> e.write(b));
        DataTypeUtil.INT.writeCollection(buf, this.sourceLines, (b, s) -> s.write(b));
        DataTypeUtil.INT.writeCollection(buf, this.activations, (b, a) -> a.write(b));

        super.encode(buf); // at the end
    }

    @Override
    public void decode(ByteBuf buf) {
        this.serverId = DataTypeUtil.readUniqueId(buf);
        this.captureInfo = new CaptureInfo(buf);
        this.encryptionInfo = DataTypeUtil.readNullable(buf, EncryptionInfo::new);
        this.sourceLines = DataTypeUtil.INT.readCollection(buf, VoiceSourceLine::new, HashSet::new);
        this.activations = DataTypeUtil.INT.readCollection(buf, VoiceActivation::new, HashSet::new);

        super.decode(buf); // at the end
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleConfigPacket(this);
    }

    public UUID getServerId() {
        return this.serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public CaptureInfo getCaptureInfo() {
        return this.captureInfo;
    }

    public void setCaptureInfo(CaptureInfo captureInfo) {
        this.captureInfo = captureInfo;
    }

    public @Nullable EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }

    public void setEncryptionInfo(@Nullable EncryptionInfo encryptionInfo) {
        this.encryptionInfo = encryptionInfo;
    }

    public Collection<VoiceSourceLine> getSourceLines() {
        return this.sourceLines;
    }

    public void setSourceLines(Collection<VoiceSourceLine> sourceLines) {
        this.sourceLines = sourceLines;
    }

    public Collection<VoiceActivation> getActivations() {
        return this.activations;
    }

    public void setActivations(Collection<VoiceActivation> activations) {
        this.activations = activations;
    }

    @Override
    public String toString() {
        return "ConfigPacket{" +
                "serverId=" + serverId +
                ", captureInfo=" + captureInfo +
                ", encryptionInfo=" + encryptionInfo +
                ", sourceLines=" + sourceLines +
                ", activations=" + activations +
                '}';
    }
}
