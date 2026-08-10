package dev.minceraft.sonus.web.protocol.model;
// Created by booky10 in Sonus (20:36 28.11.2025)

import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.participant.builtin.RoomAudioType;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_BYPASS_PASSWORD;
import static net.kyori.adventure.text.Component.text;

@NullMarked
public class SonusWebRoom {

    private static final int FLAG_PASSWORD = 1 << 0;
    private static final int FLAG_JOINABLE = 1 << 1;
    private static final int FLAG_SPEAK_TO_OTHERS = 1 << 2;
    private static final int FLAG_LISTEN_TO_OTHERS = 1 << 3;

    private final UUID uniqueId;
    private final Component name;
    private final boolean password;
    private final boolean joinable;
    private final boolean speakToOthers;
    private final boolean listenToOthers;

    public SonusWebRoom(
            UUID uniqueId, Component name, boolean password,
            boolean joinable, boolean speakToOthers, boolean listenToOthers
    ) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.password = password;
        this.joinable = joinable;
        this.speakToOthers = speakToOthers;
        this.listenToOthers = listenToOthers;
    }

    public static SonusWebRoom fromRoom(IRoom room, ISonusPlayer viewer) {
        boolean bypassPassword = viewer.hasPermission(PERMISSION_GROUPS_BYPASS_PASSWORD, false);
        UUID uniqueId = room.getUniqueId();
        Component name = viewer.renderComponent(text(room.getName()));
        boolean password = room.getPassword() != null && !bypassPassword;
        boolean joinable = room.isVisible();
        RoomAudioType audioType = room.getRoomAudioType();
        boolean speakToOthers = audioType.isSpeakToOthers();
        boolean listenToOthers = audioType.isListenToOthers();
        return new SonusWebRoom(uniqueId, name, password, joinable, speakToOthers, listenToOthers);
    }

    public static void encode(ByteBuf buf, SonusWebRoom group) {
        DataTypeUtil.writeUniqueId(buf, group.uniqueId);
        DataTypeUtil.writeComponentJson(buf, group.name);
        buf.writeByte(group.packFlags());
    }

    public static SonusWebRoom decode(ByteBuf buf) {
        UUID uniqueId = DataTypeUtil.readUniqueId(buf);
        Component name = DataTypeUtil.readComponentJson(buf);
        short flags = buf.readUnsignedByte();
        boolean password = (flags & FLAG_PASSWORD) != 0;
        boolean joinable = (flags & FLAG_JOINABLE) != 0;
        boolean speakToOthers = (flags & FLAG_SPEAK_TO_OTHERS) != 0;
        boolean listenToOthers = (flags & FLAG_LISTEN_TO_OTHERS) != 0;
        return new SonusWebRoom(uniqueId, name, password, joinable, speakToOthers, listenToOthers);
    }

    public byte packFlags() {
        return (byte) (0
                | (this.password ? FLAG_PASSWORD : 0)
                | (this.joinable ? FLAG_JOINABLE : 0)
                | (this.speakToOthers ? FLAG_SPEAK_TO_OTHERS : 0)
                | (this.listenToOthers ? FLAG_LISTEN_TO_OTHERS : 0));
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Component getName() {
        return this.name;
    }

    public boolean isPassword() {
        return this.password;
    }

    public boolean isJoinable() {
        return this.joinable;
    }

    public boolean isSpeakToOthers() {
        return this.speakToOthers;
    }

    public boolean isListenToOthers() {
        return this.listenToOthers;
    }
}
