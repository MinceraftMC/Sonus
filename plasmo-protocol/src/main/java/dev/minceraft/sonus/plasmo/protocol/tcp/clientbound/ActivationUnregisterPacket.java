package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class ActivationUnregisterPacket extends TcpPlasmoPacket<ActivationUnregisterPacket> {

    private @MonotonicNonNull UUID activationId;

    public ActivationUnregisterPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.activationId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.activationId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleActivationUnregisterPacket(this);
    }

    public UUID getActivationId() {
        return this.activationId;
    }

    public void setActivationId(UUID activationId) {
        this.activationId = activationId;
    }
}
