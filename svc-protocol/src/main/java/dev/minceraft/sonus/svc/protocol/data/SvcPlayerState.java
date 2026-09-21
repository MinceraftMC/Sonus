package dev.minceraft.sonus.svc.protocol.data;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class SvcPlayerState {

    private final UUID playerId;
    private final String name;
    private final boolean disabled;
    private final boolean disconnected;
    @Nullable
    private final UUID groupId;

    public SvcPlayerState(UUID playerId, String name, boolean disabled, boolean disconnected, @Nullable UUID groupId) {
        this.playerId = playerId;
        this.name = name;
        this.disabled = disabled;
        this.disconnected = disconnected;
        this.groupId = groupId;
    }

    public SvcPlayerState(ByteBuf buf) {
        this.disabled = buf.readBoolean();
        this.disconnected = buf.readBoolean();
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.name = Utf8String.read(buf);
        this.groupId = DataTypeUtil.readNullable(buf, DataTypeUtil::readUniqueId);
    }

    public void encode(ByteBuf buf) {
        buf.writeBoolean(this.disabled);
        buf.writeBoolean(this.disconnected);
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        Utf8String.write(buf, this.name);
        DataTypeUtil.writeNullable(buf, this.groupId, DataTypeUtil::writeUniqueId);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    @Nullable
    public UUID getGroupId() {
        return this.groupId;
    }

    public boolean hasGroup() {
        return this.groupId != null;
    }

    @Override
    public String toString() {
        return "SonusPlayerState{" +
                "playerId=" + playerId +
                ", name='" + name + '\'' +
                ", disabled=" + disabled +
                ", disconnected=" + disconnected +
                ", groupId=" + groupId +
                '}';
    }
}
