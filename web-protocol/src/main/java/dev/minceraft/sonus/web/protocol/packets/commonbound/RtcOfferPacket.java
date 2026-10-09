package dev.minceraft.sonus.web.protocol.packets.commonbound;
// Created by booky10 in Sonus (5:22 PM 02.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RtcOfferPacket extends WebSocketPacket {

    private @MonotonicNonNull Type type;
    private @MonotonicNonNull String sdp;

    public RtcOfferPacket() {
    }

    public RtcOfferPacket(Type type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        VarInt.write(buf, this.type.ordinal());
        Utf8String.write(buf, this.sdp);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.type = Type.TYPES[VarInt.read(buf)];
        this.sdp = Utf8String.read(buf, 8192);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRtcOffer(this);
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSdp() {
        return this.sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public enum Type {

        OFFER,
        ANSWER,
        ;

        private static final Type[] TYPES = values();
    }
}
