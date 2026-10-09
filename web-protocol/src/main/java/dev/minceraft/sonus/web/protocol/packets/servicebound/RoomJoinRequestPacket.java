package dev.minceraft.sonus.web.protocol.packets.servicebound;
// Created by booky10 in Sonus (20:34 28.11.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static dev.minceraft.sonus.web.protocol.packets.servicebound.RoomCreatePacket.MAX_ROOM_PASSWORD_LENGTH;

@NullMarked
public class RoomJoinRequestPacket extends WebSocketPacket {

    private @MonotonicNonNull UUID roomId;
    private @Nullable String password;

    public RoomJoinRequestPacket(UUID roomId, @Nullable String password) {
        this.roomId = roomId;
        this.password = password;
    }

    public RoomJoinRequestPacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.roomId);
        DataTypeUtil.writeNullable(buf, this.password, Utf8String::write);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.roomId = DataTypeUtil.readUniqueId(buf);
        this.password = DataTypeUtil.readNullable(buf, ew ->
                Utf8String.read(ew, MAX_ROOM_PASSWORD_LENGTH).trim());
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRoomJoinRequest(this);
    }

    public UUID getRoomId() {
        return this.roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public @Nullable String getPassword() {
        return this.password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }
}
