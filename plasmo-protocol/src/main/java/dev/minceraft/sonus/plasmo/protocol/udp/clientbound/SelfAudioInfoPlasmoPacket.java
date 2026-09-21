package dev.minceraft.sonus.plasmo.protocol.udp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.cipher.ICipher;
import dev.minceraft.sonus.plasmo.protocol.cipher.IEncryptable;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SelfAudioInfoPlasmoPacket extends UdpPlasmoPacket<SelfAudioInfoPlasmoPacket> implements IEncryptable {

    private @MonotonicNonNull UUID sourceId;
    private long sequenceNumber;
    private byte @MonotonicNonNull [] audioData;
    private short distance;

    public SelfAudioInfoPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceId);
        buf.writeLong(this.sequenceNumber);
        DataTypeUtil.INT.writeByteArray(buf, this.audioData);
        buf.writeShort(this.distance);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceId = DataTypeUtil.readUniqueId(buf);
        this.sequenceNumber = buf.readLong();
        this.audioData = DataTypeUtil.INT.readByteArray(buf);
        this.distance = buf.readShort();
    }

    @Override
    public void encrypt(ICipher cipher) {
        this.audioData = cipher.encrypt(this.audioData);
    }

    @Override
    public void decrypt(ICipher cipher) {
        this.audioData = cipher.decrypt(this.audioData);
    }

    @Override
    public void handle(UdpHandler handler) {
        handler.handleSelfAudioPacket(this);
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

    public byte[] getAudioData() {
        return this.audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }

    public short getDistance() {
        return this.distance;
    }

    public void setDistance(short distance) {
        this.distance = distance;
    }
}
