package dev.minceraft.sonus.web.protocol.packets.commonbound;
// Created by booky10 in Sonus (20:31 28.11.2025)

import dev.minceraft.sonus.common.util.codec.VarLong;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KeepAlivePacket extends WebSocketPacket {

    private long id;

    public KeepAlivePacket(long id) {
        this.id = id;
    }

    public KeepAlivePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        VarLong.write(buf, this.id);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.id = VarLong.read(buf);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleKeepAlive(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
