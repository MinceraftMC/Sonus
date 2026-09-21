package dev.minceraft.sonus.plasmo.protocol.udp.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.BaseAudioPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceAudioPlasmoPacket extends BaseAudioPlasmoPacket<SourceAudioPlasmoPacket> {

    private @MonotonicNonNull UUID sourceId;
    private byte sourceState;
    private short distance;

    public SourceAudioPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        DataTypeUtil.writeUniqueId(buf, this.sourceId);
        buf.writeByte(this.sourceState);
        buf.writeShort(this.distance);
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        this.sourceId = DataTypeUtil.readUniqueId(buf);
        this.sourceState = buf.readByte();
        this.distance = buf.readShort();
    }

    @Override
    public void handle(UdpHandler handler) {
        handler.handleSourceAudioPacket(this);
    }

    public UUID getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public byte getSourceState() {
        return this.sourceState;
    }

    public void setSourceState(byte sourceState) {
        this.sourceState = sourceState;
    }

    public short getDistance() {
        return this.distance;
    }

    public void setDistance(short distance) {
        this.distance = distance;
    }
}
