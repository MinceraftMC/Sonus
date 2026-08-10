package dev.minceraft.sonus.web.protocol.packets.servicebound;
// Created by booky10 in Sonus (20:34 28.11.2025)

import dev.minceraft.sonus.common.participant.builtin.RoomAudioType;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RoomCreatePacket extends WebSocketPacket {

    static final int MAX_ROOM_NAME_LENGTH = 32;
    static final int MAX_ROOM_PASSWORD_LENGTH = 32;

    private @MonotonicNonNull String name;
    private @Nullable String password;
    private boolean speakToOthers;
    private boolean listenToOthers;

    public RoomCreatePacket(String name, @Nullable String password, boolean speakToOthers, boolean listenToOthers) {
        this.name = name;
        this.password = password;
        this.speakToOthers = speakToOthers;
        this.listenToOthers = listenToOthers;
    }

    public RoomCreatePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        Utf8String.write(buf, this.name);
        DataTypeUtil.writeNullable(buf, this.password, Utf8String::write);
        buf.writeBoolean(this.speakToOthers);
        buf.writeBoolean(this.listenToOthers);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.name = Utf8String.read(buf, MAX_ROOM_NAME_LENGTH).trim();
        this.password = DataTypeUtil.readNullable(buf, ew ->
                Utf8String.read(ew, MAX_ROOM_PASSWORD_LENGTH).trim());
        this.speakToOthers = buf.readBoolean();
        this.listenToOthers = buf.readBoolean();

        if (this.name.isEmpty()) {
            throw new IllegalStateException("Can't create room with empty name " + this);
        } else if (this.password != null && this.password.isEmpty()) {
            throw new IllegalStateException("Can't create room with non-null but empty password " + this);
        } else if (this.speakToOthers && !this.listenToOthers) {
            throw new IllegalStateException("Can't create room with passthrough speaking without listening " + this);
        }
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRoomCreate(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable String getPassword() {
        return this.password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public RoomAudioType getAudioType() {
        if (!this.listenToOthers && !this.speakToOthers) {
            return RoomAudioType.ISOLATED;
        } else if (this.speakToOthers && this.listenToOthers) {
            return RoomAudioType.OPEN;
        } else {
            // we don't support rooms where you can speak to others without listening
            return RoomAudioType.NORMAL;
        }
    }

    public boolean isSpeakToOthers() {
        return this.speakToOthers;
    }

    public void setSpeakToOthers(boolean speakToOthers) {
        this.speakToOthers = speakToOthers;
    }

    public boolean isListenToOthers() {
        return this.listenToOthers;
    }

    public void setListenToOthers(boolean listenToOthers) {
        this.listenToOthers = listenToOthers;
    }
}
