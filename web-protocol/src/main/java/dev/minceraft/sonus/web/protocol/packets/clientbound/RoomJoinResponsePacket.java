package dev.minceraft.sonus.web.protocol.packets.clientbound;
// Created by booky10 in Sonus (20:34 28.11.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class RoomJoinResponsePacket extends WebSocketPacket {

    private @MonotonicNonNull UUID roomId;
    private boolean success;

    public RoomJoinResponsePacket(UUID roomId, boolean success) {
        this.roomId = roomId;
        this.success = success;
    }

    public RoomJoinResponsePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.roomId);
        buf.writeBoolean(this.success);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.roomId = DataTypeUtil.readUniqueId(buf);
        this.success = buf.readBoolean();
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRoomJoinResponse(this);
    }

    public UUID getRoomId() {
        return this.roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
