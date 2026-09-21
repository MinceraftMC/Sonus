package dev.minceraft.sonus.svc.protocol.meta.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.IMetaSvcHandler;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.util.SvcPluginChannels;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class RemoveGroupSvcPacket extends SvcMetaPacket {

    private @MonotonicNonNull UUID groupId;

    public RemoveGroupSvcPacket() {
        super(SvcPluginChannels.REMOVE_GROUP);
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.groupId);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.groupId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(IMetaSvcHandler handler) {
        handler.handleRemoveGroupPacket(this);
    }

    public UUID getGroupId() {
        return this.groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
