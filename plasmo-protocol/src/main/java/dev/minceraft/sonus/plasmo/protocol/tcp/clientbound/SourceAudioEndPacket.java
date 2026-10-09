package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceAudioEndPacket extends TcpPlasmoPacket<SourceAudioEndPacket> {

    private @MonotonicNonNull UUID sourceId;
    private long sequenceNumber;

    public SourceAudioEndPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceId);
        buf.writeLong(this.sequenceNumber);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceId = DataTypeUtil.readUniqueId(buf);
        this.sequenceNumber = buf.readLong();
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceAudioEndPacket(this);
    }

    public UUID getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}

