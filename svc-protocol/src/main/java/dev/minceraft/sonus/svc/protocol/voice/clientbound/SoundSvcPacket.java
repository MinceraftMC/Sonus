package dev.minceraft.sonus.svc.protocol.voice.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public abstract class SoundSvcPacket extends SvcVoicePacket {

    public static final byte WHISPER_MASK = 0b1;
    public static final byte HAS_CATEGORY_MASK = 0b10;

    protected @MonotonicNonNull UUID channelId;
    protected @MonotonicNonNull UUID sender;
    protected byte @MonotonicNonNull [] data;
    protected long sequenceNumber;
    protected @Nullable String category;

    protected SoundSvcPacket() {
    }

    protected static boolean hasFlag(byte data, byte mask) {
        return (data & mask) != 0b0;
    }

    protected static byte setFlag(byte data, byte mask) {
        return (byte) (data | mask);
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.channelId);
        DataTypeUtil.writeUniqueId(buf, this.sender);
        DataTypeUtil.VAR_INT.writeByteArray(buf,this.data);
        buf.writeLong(this.sequenceNumber);

        byte data = 0b0;
        if (this.category != null) {
            data = setFlag(data, HAS_CATEGORY_MASK);
        }
        buf.writeByte(data);
        if (this.category != null) {
            Utf8String.write(buf, this.category);
        }
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.channelId = DataTypeUtil.readUniqueId(buf);
        this.sender = DataTypeUtil.readUniqueId(buf);
        this.data = DataTypeUtil.VAR_INT.readByteArray(buf);
        this.sequenceNumber = buf.readLong();

        byte data = buf.readByte();
        if (hasFlag(data, HAS_CATEGORY_MASK)) {
            this.category = Utf8String.read(buf, 16);
        } else {
            this.category = null;
        }
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public @Nullable String getCategory() {
        return category;
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }
}
