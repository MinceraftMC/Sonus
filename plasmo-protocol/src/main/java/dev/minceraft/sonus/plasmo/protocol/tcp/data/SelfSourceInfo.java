package dev.minceraft.sonus.plasmo.protocol.tcp.data;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.SourceInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.SourceType;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class SelfSourceInfo {

    private final SourceInfo sourceInfo;
    private final UUID playerId;
    private final UUID activationId;
    private final long sequenceNumber;

    public SelfSourceInfo(ByteBuf buf) {
        this.sourceInfo = SourceType.decode(buf);
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.activationId = DataTypeUtil.readUniqueId(buf);
        this.sequenceNumber = buf.readLong();
    }

    public SelfSourceInfo(SourceInfo sourceInfo, UUID playerId, UUID activationId, long sequenceNumber) {
        this.sourceInfo = sourceInfo;
        this.playerId = playerId;
        this.activationId = activationId;
        this.sequenceNumber = sequenceNumber;
    }

    public void write(ByteBuf buf) {
        SourceType.encode(buf, this.sourceInfo);
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        DataTypeUtil.writeUniqueId(buf, this.activationId);
        buf.writeLong(this.sequenceNumber);
    }

    public SourceInfo getSourceInfo() {
        return this.sourceInfo;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public UUID getActivationId() {
        return this.activationId;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }
}
