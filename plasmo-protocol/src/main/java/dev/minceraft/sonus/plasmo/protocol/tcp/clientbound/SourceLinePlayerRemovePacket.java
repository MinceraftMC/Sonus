package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceLinePlayerRemovePacket extends TcpPlasmoPacket<SourceLinePlayerRemovePacket> {

    private @MonotonicNonNull UUID sourceLineId;
    private @MonotonicNonNull UUID playerId;

    public SourceLinePlayerRemovePacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceLineId);
        DataTypeUtil.writeUniqueId(buf, this.playerId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceLineId = DataTypeUtil.readUniqueId(buf);
        this.playerId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceLinePlayerRemovePacket(this);
    }

    public UUID getSourceLineId() {
        return this.sourceLineId;
    }

    public void setSourceLineId(UUID sourceLineId) {
        this.sourceLineId = sourceLineId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
