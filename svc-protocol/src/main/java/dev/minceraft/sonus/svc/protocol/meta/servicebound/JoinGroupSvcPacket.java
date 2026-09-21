package dev.minceraft.sonus.svc.protocol.meta.servicebound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.IMetaSvcHandler;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.util.SvcPluginChannels;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class JoinGroupSvcPacket extends SvcMetaPacket {

    private @MonotonicNonNull UUID groupId;
    private @Nullable String password;

    public JoinGroupSvcPacket() {
        super(SvcPluginChannels.SET_GROUP);
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.groupId);
        DataTypeUtil.writeNullable(buf, this.password, Utf8String::write);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.groupId = DataTypeUtil.readUniqueId(buf);
        this.password = DataTypeUtil.readNullable(buf, b -> Utf8String.read(b, 512));
    }

    @Override
    public void handle(IMetaSvcHandler handler) {
        handler.handleJoinGroupPacket(this);
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public @Nullable String getPassword() {
        return this.password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }
}
