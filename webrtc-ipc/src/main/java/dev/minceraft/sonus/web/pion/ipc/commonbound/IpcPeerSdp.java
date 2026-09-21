package dev.minceraft.sonus.web.pion.ipc.commonbound;
// Created by booky10 in Sonus (7:03 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IpcPeerSdp extends IpcMessage {

    private final String sdp;

    public IpcPeerSdp(ByteBuf buf) {
        this(VarInt.read(buf), Utf8String.read(buf));
    }

    public IpcPeerSdp(int handlerId, String sdp) {
        super(handlerId);
        this.sdp = sdp;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        Utf8String.write(buf, this.sdp);
    }

    public String getSdp() {
        return this.sdp;
    }
}
