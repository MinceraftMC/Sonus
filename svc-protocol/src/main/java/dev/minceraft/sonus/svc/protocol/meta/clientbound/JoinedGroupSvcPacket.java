package dev.minceraft.sonus.svc.protocol.meta.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.IMetaSvcHandler;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.util.SvcPluginChannels;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class JoinedGroupSvcPacket extends SvcMetaPacket {

    private @Nullable UUID groupId;
    private boolean wrongPassword;

    public JoinedGroupSvcPacket() {
        super(SvcPluginChannels.JOINED_GROUP);
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeNullable(buf, this.groupId, DataTypeUtil::writeUniqueId);
        buf.writeBoolean(this.wrongPassword);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.groupId = DataTypeUtil.readNullable(buf, DataTypeUtil::readUniqueId);
        this.wrongPassword = buf.readBoolean();
    }

    @Override
    public void handle(IMetaSvcHandler handler) {
        handler.handleJoinedGroupPacket(this);
    }

    public @Nullable UUID getGroupId() {
        return this.groupId;
    }

    public void setGroupId(@Nullable UUID groupId) {
        this.groupId = groupId;
    }

    public boolean isWrongPassword() {
        return this.wrongPassword;
    }

    public void setWrongPassword(boolean wrongPassword) {
        this.wrongPassword = wrongPassword;
    }
}
