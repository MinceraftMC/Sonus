package dev.minceraft.sonus.web.pion.ipc.commonbound;
// Created by booky10 in Sonus (7:03 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import dev.minceraft.sonus.web.pion.ipc.IpcTypes;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class IpcPeerAddIceCandidate extends IpcMessage {

    private final String candidate;
    private final @Nullable String sdpMid;
    private final @Nullable Short sdpMLineIndex;

    public IpcPeerAddIceCandidate(ByteBuf buf) {
        this(
                VarInt.read(buf), Utf8String.read(buf),
                IpcTypes.readNullable(buf, Utf8String::read),
                IpcTypes.readNullable(buf, ByteBuf::readShort)
        );
    }

    public IpcPeerAddIceCandidate(int handlerId, String candidate, @Nullable String sdpMid, @Nullable Short sdpMLineIndex) {
        super(handlerId);
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        Utf8String.write(buf, this.candidate);
        IpcTypes.writeNullable(buf, this.sdpMid, Utf8String::write);
        IpcTypes.writeNullable(buf, this.sdpMLineIndex, (ew, v) -> ew.writeShort(v));
    }

    public String getCandidate() {
        return this.candidate;
    }

    public @Nullable String getSdpMid() {
        return this.sdpMid;
    }

    public @Nullable Short getSdpMLineIndex() {
        return this.sdpMLineIndex;
    }
}
