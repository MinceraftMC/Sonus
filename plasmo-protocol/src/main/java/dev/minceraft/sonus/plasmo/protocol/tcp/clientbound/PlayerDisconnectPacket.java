package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class PlayerDisconnectPacket extends TcpPlasmoPacket<PlayerDisconnectPacket> {

    private @MonotonicNonNull UUID uniqueId;

    public PlayerDisconnectPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.uniqueId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.uniqueId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handlePlayerDisconnectPacket(this);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
