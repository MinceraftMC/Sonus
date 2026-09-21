package dev.minceraft.sonus.plasmo.protocol.tcp.serverbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class PlayerAudioEndPacket extends TcpPlasmoPacket<PlayerAudioEndPacket> {

    private long sequenceNumber;
    private @MonotonicNonNull UUID activationId;
    private short distance;

    public PlayerAudioEndPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(this.sequenceNumber);
        DataTypeUtil.writeUniqueId(buf, this.activationId);
        buf.writeShort(distance);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sequenceNumber = buf.readLong();
        this.activationId = DataTypeUtil.readUniqueId(buf);
        this.distance = buf.readShort();
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handlePlayerAudioEndPacket(this);
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public UUID getActivationId() {
        return this.activationId;
    }

    public void setActivationId(UUID activationId) {
        this.activationId = activationId;
    }

    public short getDistance() {
        return this.distance;
    }

    public void setDistance(short distance) {
        this.distance = distance;
    }
}
