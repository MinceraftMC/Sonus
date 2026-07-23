package dev.minceraft.sonus.svc.protocol.data;

import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.common.rooms.IRoom;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class SonusClientGroup {

    private final UUID groupId;
    private final String name;
    private final boolean password;
    private final boolean persistent;
    private final boolean hidden;
    private final SonusGroupType type;

    public SonusClientGroup(String name, UUID groupId, boolean password, boolean persistent, boolean hidden, SonusGroupType type) {
        this.name = name;
        this.groupId = groupId;
        this.password = password;
        this.persistent = persistent;
        this.hidden = hidden;
        this.type = type;
    }

    public SonusClientGroup(IRoom room, boolean bypassPassword) {
        this.name = room.getName();
        this.groupId = room.getId();
        this.password = room.getPassword() != null && !bypassPassword;
        this.persistent = false; // dummy value, not used on client
        this.hidden = !room.isVisible();
        this.type = SonusGroupType.fromSonus(room.getRoomAudioType());
    }

    public SonusClientGroup(ByteBuf buf) {
        this.groupId = new UUID(buf.readLong(), buf.readLong());
        this.name = Utf8String.read(buf, 512);
        this.password = buf.readBoolean();
        this.persistent = buf.readBoolean();
        this.hidden = buf.readBoolean();
        this.type = SonusGroupType.values()[buf.readShort()];
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPassword() {
        return this.password;
    }

    public UUID getId() {
        return this.groupId;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public SonusGroupType getSonusGroupType() {
        return this.type;
    }

    public void encode(ByteBuf buf) {
        buf.writeLong(this.groupId.getMostSignificantBits());
        buf.writeLong(this.groupId.getLeastSignificantBits());
        Utf8String.write(buf, this.name);
        buf.writeBoolean(this.password);
        buf.writeBoolean(this.persistent);
        buf.writeBoolean(this.hidden);
        buf.writeShort(this.type.ordinal());
    }

    @Override
    public String toString() {
        return "SonusClientGroup{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", password=" + password +
                ", persistent=" + persistent +
                ", hidden=" + hidden +
                ", type=" + type +
                '}';
    }
}
