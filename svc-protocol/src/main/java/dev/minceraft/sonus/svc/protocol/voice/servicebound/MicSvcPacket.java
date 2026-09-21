package dev.minceraft.sonus.svc.protocol.voice.servicebound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.voice.IVoiceSvcHandler;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MicSvcPacket extends SvcVoicePacket {

    private byte @MonotonicNonNull [] data;
    private boolean whispering;
    private long sequenceNumber;

    public MicSvcPacket() {
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.VAR_INT.writeByteArray(buf, this.data);
        buf.writeLong(this.sequenceNumber);
        buf.writeBoolean(this.whispering);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.data = DataTypeUtil.VAR_INT.readByteArray(buf);
        this.sequenceNumber = buf.readLong();
        this.whispering = buf.readBoolean();
    }

    @Override
    public void handle(IVoiceSvcHandler handler) {
        handler.handleMicPacket(this);
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isWhispering() {
        return this.whispering;
    }

    public void setWhispering(boolean whispering) {
        this.whispering = whispering;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
