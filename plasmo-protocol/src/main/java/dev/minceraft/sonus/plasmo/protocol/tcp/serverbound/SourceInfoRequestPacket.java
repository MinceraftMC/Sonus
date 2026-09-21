package dev.minceraft.sonus.plasmo.protocol.tcp.serverbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceInfoRequestPacket extends TcpPlasmoPacket<SourceInfoRequestPacket> {

    private @MonotonicNonNull UUID sourceId;

    public SourceInfoRequestPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceInfoRequestPacket(this);
    }

    public UUID getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }
}
