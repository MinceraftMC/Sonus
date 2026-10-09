package dev.minceraft.sonus.web.protocol.packets.commonbound;
// Created by booky10 in Sonus (5:22 PM 02.03.2026)

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
public class RtcIceCandidatePacket extends WebSocketPacket {

    private @MonotonicNonNull String candidate;
    private @Nullable String sdpMid;
    private @Nullable Short sdpMLineIndex;

    public RtcIceCandidatePacket() {
    }

    public RtcIceCandidatePacket(@MonotonicNonNull String candidate, @Nullable String sdpMid, @Nullable Short sdpMLineIndex) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        Utf8String.write(buf, this.candidate);
        DataTypeUtil.writeNullable(buf, this.sdpMid, Utf8String::write);
        DataTypeUtil.writeNullable(buf, this.sdpMLineIndex,
                (ew, v) -> ew.writeShort(v));
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.candidate = Utf8String.read(buf, 256);
        this.sdpMid = DataTypeUtil.readNullable(buf, ew -> Utf8String.read(ew, 32));
        this.sdpMLineIndex = DataTypeUtil.readNullable(buf, ByteBuf::readShort);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleRtcIceCandidate(this);
    }

    public String getCandidate() {
        return this.candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public @Nullable String getSdpMid() {
        return this.sdpMid;
    }

    public void setSdpMid(@Nullable String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public @Nullable Short getSdpMLineIndex() {
        return this.sdpMLineIndex;
    }

    public void setSdpMLineIndex(@Nullable Short sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }
}
