package dev.minceraft.sonus.web.adapter.pipeline;

import dev.minceraft.sonus.web.adapter.connection.WebSocketConnection;
import dev.minceraft.sonus.web.adapter.messages.AbstractWebSocketMessage;
import dev.minceraft.sonus.web.adapter.messages.ByteWebSocketMessage;
import dev.minceraft.sonus.web.adapter.util.HttpRequestUtil;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import dev.minceraft.sonus.web.protocol.packets.WsPacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;

import java.util.List;

public class WebSocketSonusCodec extends MessageToMessageCodec<AbstractWebSocketMessage, WebSocketPacket> {

    private final WebSocketConnection connection;

    public WebSocketSonusCodec(WebSocketConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketPacket msg, List<Object> out) {
        ByteBuf buf = ctx.alloc().buffer();
        try {
            WsPacketRegistry.REGISTRY.encode(buf, msg, new WsPacketContext(this.connection.getVersion()));
            out.add(new ByteWebSocketMessage(buf.retain()));
        } finally {
            buf.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, AbstractWebSocketMessage msg, List<Object> out) {
        if (msg instanceof ByteWebSocketMessage binary) {
            WebSocketPacket packet = WsPacketRegistry.REGISTRY.decode(binary.getDirectBuf(), new WsPacketContext(this.connection.getVersion()));
            if (packet != null) {
                out.add(packet);
            }
        } else {
            // unexpected message
            HttpRequestUtil.doSocketClose(ctx, WebSocketCloseStatus.INVALID_MESSAGE_TYPE);
        }
    }
}
