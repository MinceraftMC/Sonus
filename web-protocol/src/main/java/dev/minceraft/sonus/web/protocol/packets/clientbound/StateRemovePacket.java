package dev.minceraft.sonus.web.protocol.packets.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class StateRemovePacket extends WebSocketPacket {

    private @MonotonicNonNull UUID playerId;

    public StateRemovePacket(UUID playerId) {
        this.playerId = playerId;
    }

    public StateRemovePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.playerId);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.playerId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleStateRemove(this);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
