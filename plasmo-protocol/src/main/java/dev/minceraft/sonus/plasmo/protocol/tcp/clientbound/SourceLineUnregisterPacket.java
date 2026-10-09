package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceLineUnregisterPacket extends TcpPlasmoPacket<SourceLineUnregisterPacket> {

    private @MonotonicNonNull UUID sourceLineId;

    public SourceLineUnregisterPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceLineId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceLineId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceLineUnregisterPacket(this);
    }

    public UUID getSourceLineId() {
        return this.sourceLineId;
    }

    public void setSourceLineId(UUID sourceLineId) {
        this.sourceLineId = sourceLineId;
    }
}
