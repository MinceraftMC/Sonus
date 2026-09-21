package dev.minceraft.sonus.svc.protocol.voice.commonbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.voice.IVoiceSvcHandler;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class PingSvcPacket extends SvcVoicePacket {

    private @MonotonicNonNull UUID id;
    private long timestamp;

    public PingSvcPacket() {
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.id);
        buf.writeLong(this.timestamp);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.id = DataTypeUtil.readUniqueId(buf);
        this.timestamp = buf.readLong();
    }

    @Override
    public void handle(IVoiceSvcHandler handler) {
        handler.handlePingPacket(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
