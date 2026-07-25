package dev.minceraft.sonus.web.pion.ipc.pionbound;
// Created by booky10 in Sonus (6:30 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import dev.minceraft.sonus.web.pion.ipc.IpcTypes;
import dev.minceraft.sonus.web.pion.ipc.model.BundlePolicy;
import dev.minceraft.sonus.web.pion.ipc.model.IceServer;
import dev.minceraft.sonus.web.pion.ipc.model.IceTransportPolicy;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class IpcApiAllocatePeer extends IpcMessage {

    private final List<IceServer> iceServers;
    private final IceTransportPolicy iceTransportPolicy;
    private final BundlePolicy bundlePolicy;
    private final String id;

    public IpcApiAllocatePeer(
            int handlerId, List<IceServer> iceServers, IceTransportPolicy iceTransportPolicy,
            BundlePolicy bundlePolicy, String id
    ) {
        super(handlerId);
        this.iceServers = iceServers;
        this.iceTransportPolicy = iceTransportPolicy;
        this.bundlePolicy = bundlePolicy;
        this.id = id;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        IpcTypes.writeCollection(buf, this.iceServers, IpcTypes::writeIceServer);
        IpcTypes.writeEnum(buf, this.iceTransportPolicy);
        IpcTypes.writeEnum(buf, this.bundlePolicy);
        Utf8String.write(buf, this.id);
    }

    public List<IceServer> getIceServers() {
        return this.iceServers;
    }

    public IceTransportPolicy getIceTransportPolicy() {
        return this.iceTransportPolicy;
    }

    public BundlePolicy getBundlePolicy() {
        return this.bundlePolicy;
    }

    public String getId() {
        return this.id;
    }
}
