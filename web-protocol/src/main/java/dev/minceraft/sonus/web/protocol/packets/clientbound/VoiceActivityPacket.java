package dev.minceraft.sonus.web.protocol.packets.clientbound;
// Created by booky10 in Sonus (00:56 23.12.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class VoiceActivityPacket extends WebSocketPacket {

    private @MonotonicNonNull UUID playerId;
    private boolean active;

    public VoiceActivityPacket(UUID playerId, boolean active) {
        this.playerId = playerId;
        this.active = active;
    }

    public VoiceActivityPacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        buf.writeBoolean(this.active);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.active = buf.readBoolean();
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleVoiceActivity(this);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
