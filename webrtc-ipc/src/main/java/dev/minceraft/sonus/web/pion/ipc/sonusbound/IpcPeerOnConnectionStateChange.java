package dev.minceraft.sonus.web.pion.ipc.sonusbound;
// Created by booky10 in Sonus (7:22 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import dev.minceraft.sonus.web.pion.ipc.IpcTypes;
import dev.minceraft.sonus.web.pion.ipc.model.PeerConnectionState;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IpcPeerOnConnectionStateChange extends IpcMessage {

    private final PeerConnectionState state;

    public IpcPeerOnConnectionStateChange(ByteBuf buf) {
        this(VarInt.read(buf), IpcTypes.readEnum(buf, PeerConnectionState.values()));
    }

    public IpcPeerOnConnectionStateChange(int handlerId, PeerConnectionState state) {
        super(handlerId);
        this.state = state;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        IpcTypes.writeEnum(buf, this.state);
    }

    public PeerConnectionState getState() {
        return this.state;
    }
}
