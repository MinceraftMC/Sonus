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
public class RoomRemovePacket extends WebSocketPacket {

    private @MonotonicNonNull UUID roomId;

    public RoomRemovePacket(UUID roomId) {
        this.roomId = roomId;
    }

    public RoomRemovePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.roomId);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.roomId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRoomRemove(this);
    }

    public UUID getRoomId() {
        return this.roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
}
