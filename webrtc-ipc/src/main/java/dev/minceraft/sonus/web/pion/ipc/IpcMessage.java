package dev.minceraft.sonus.web.pion.ipc;
// Created by booky10 in Sonus (6:19 PM 06.03.2026)

import dev.minceraft.sonus.common.protocol.registry.ProtocolMessage;
import dev.minceraft.sonus.common.util.codec.VarInt;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class IpcMessage implements ProtocolMessage<IpcHandler> {

    protected final int handlerId;

    public IpcMessage(int handlerId) {
        this.handlerId = handlerId;
    }

    public void encode(ByteBuf buf) {
        VarInt.write(buf, this.handlerId);
    }

    @Override
    public final void handle(IpcHandler handler) {
        handler.handle(this);
    }

    public int getHandlerId() {
        return this.handlerId;
    }
}
