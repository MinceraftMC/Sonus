package dev.minceraft.sonus.plasmo.protocol.udp.bothbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.cipher.ICipher;
import dev.minceraft.sonus.plasmo.protocol.cipher.IEncryptable;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BaseAudioPlasmoPacket<T extends BaseAudioPlasmoPacket<T>> extends UdpPlasmoPacket<T> implements IEncryptable {

    private long sequenceNumber;
    private byte @MonotonicNonNull [] audioData;

    public BaseAudioPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(this.sequenceNumber);
        DataTypeUtil.INT.writeByteArray(buf, this.audioData);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sequenceNumber = buf.readLong();
        this.audioData = DataTypeUtil.INT.readByteArray(buf, 400);
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
        handler.handleBaseAudioPacket(this);
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
}
