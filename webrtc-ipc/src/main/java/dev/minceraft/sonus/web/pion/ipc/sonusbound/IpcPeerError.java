package dev.minceraft.sonus.web.pion.ipc.sonusbound;
// Created by booky10 in Sonus (7:20 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IpcPeerError extends IpcMessage {

    private final String error;

    public IpcPeerError(ByteBuf buf) {
        this(VarInt.read(buf), Utf8String.read(buf));
    }

    public IpcPeerError(int handlerId, String error) {
        super(handlerId);
        this.error = error;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        Utf8String.write(buf, this.error);
    }

    public String getError() {
        return this.error;
    }
}
