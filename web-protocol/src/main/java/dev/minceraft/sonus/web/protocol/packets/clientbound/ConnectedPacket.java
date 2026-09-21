package dev.minceraft.sonus.web.protocol.packets.clientbound;
// Created by booky10 in Sonus (20:39 28.11.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ConnectedPacket extends WebSocketPacket {

    private @MonotonicNonNull UUID playerId;
    private @MonotonicNonNull Component username;
    private @Nullable UUID serverId;
    private @Nullable Component serverName;
    private @Nullable String serverType;

    public ConnectedPacket(
            UUID playerId, Component username,
            @Nullable UUID serverId, @Nullable Component serverName, @Nullable String serverType
    ) {
        this.playerId = playerId;
        this.username = username;
        this.serverId = serverId;
        this.serverName = serverName;
        this.serverType = serverType;
    }

    public ConnectedPacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        DataTypeUtil.writeComponentJson(buf, this.username);
        if (this.serverId != null) {
            buf.writeBoolean(true);
            DataTypeUtil.writeUniqueId(buf, this.serverId);
            DataTypeUtil.writeNullable(buf, this.serverName, DataTypeUtil::writeComponentJson);
            DataTypeUtil.writeNullable(buf, this.serverType, Utf8String::write);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.username = DataTypeUtil.readComponentJson(buf);
        if (buf.readBoolean()) {
            this.serverId = DataTypeUtil.readUniqueId(buf);
            this.serverName = DataTypeUtil.readNullable(buf, DataTypeUtil::readComponentJson);
            this.serverType = DataTypeUtil.readNullable(buf, Utf8String::read);
        }
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleConnected(this);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Component getUsername() {
        return this.username;
    }

    public void setUsername(Component username) {
        this.username = username;
    }

    public @Nullable UUID getServerId() {
        return this.serverId;
    }

    public void setServerId(@Nullable UUID serverId) {
        this.serverId = serverId;
    }

    public @Nullable Component getServerName() {
        return this.serverName;
    }

    public void setServerName(@Nullable Component serverName) {
        this.serverName = serverName;
    }

    public @Nullable String getServerType() {
        return this.serverType;
    }

    public void setServerType(@Nullable String serverType) {
        this.serverType = serverType;
    }
}
