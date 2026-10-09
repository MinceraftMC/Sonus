package dev.minceraft.sonus.web.pion.ipc;
// Created by booky10 in Sonus (9:06 PM 06.03.2026)

import dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.commonbound.IpcPeerAddIceCandidate;
import dev.minceraft.sonus.web.pion.ipc.commonbound.IpcPeerSdp;
import dev.minceraft.sonus.web.pion.ipc.pionbound.IpcApiAllocatePeer;
import dev.minceraft.sonus.web.pion.ipc.pionbound.IpcLocalTrackSendData;
import dev.minceraft.sonus.web.pion.ipc.pionbound.IpcPeerClose;
import dev.minceraft.sonus.web.pion.ipc.sonusbound.IpcPeerError;
import dev.minceraft.sonus.web.pion.ipc.sonusbound.IpcPeerOnAudioTrack;
import dev.minceraft.sonus.web.pion.ipc.sonusbound.IpcPeerOnConnectionStateChange;
import dev.minceraft.sonus.web.pion.ipc.sonusbound.IpcPeerOnIceConnectionStateChange;
import dev.minceraft.sonus.web.pion.ipc.sonusbound.IpcRemoteTrackOnData;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ANY;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.DECODE;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ENCODE;

@NullMarked
public final class IpcMessageRegistry {

    public static final IpcMessageRegistry REGISTRY = new IpcMessageRegistry(List.of(
            // commonbound
            new Entry<>(IpcPeerAddIceCandidate.class, IpcPeerAddIceCandidate::new, ANY),
            new Entry<>(IpcPeerSdp.class, IpcPeerSdp::new, ANY),
            // pionbound
            new Entry<>(IpcApiAllocatePeer.class, ENCODE),
            new Entry<>(IpcLocalTrackSendData.class, ENCODE),
            new Entry<>(IpcPeerClose.class, ENCODE),
            // sonusbound
            new Entry<>(IpcPeerError.class, IpcPeerError::new, DECODE),
            new Entry<>(IpcPeerOnAudioTrack.class, IpcPeerOnAudioTrack::new, DECODE),
            new Entry<>(IpcPeerOnConnectionStateChange.class, IpcPeerOnConnectionStateChange::new, DECODE),
            new Entry<>(IpcPeerOnIceConnectionStateChange.class, IpcPeerOnIceConnectionStateChange::new, DECODE),
            new Entry<>(IpcRemoteTrackOnData.class, IpcRemoteTrackOnData::new, DECODE)
    ));

    private final List<IpcTypes.Decoder<? extends IpcMessage>> inbound;
    private final Map<Class<? extends IpcMessage>, Integer> outbound;

    private IpcMessageRegistry(List<Entry<?>> entries) {
        List<IpcTypes.Decoder<? extends IpcMessage>> inbound = new ArrayList<>();
        this.outbound = new IdentityHashMap<>();
        for (int i = 0, outboundId = 0, len = entries.size(); i < len; i++) {
            Entry<?> entry = entries.get(i);
            if (entry.direction.isEncode()) {
                this.outbound.put(entry.clazz, outboundId++);
            }
            if (entry.direction.isDecode()) {
                inbound.add(entry.decoder);
            }
        }
        this.inbound = List.copyOf(inbound);
    }

    public IpcMessage decode(ByteBuf buf) {
        int id = VarInt.read(buf);
        if (id < 0 || id >= this.inbound.size()) {
            throw new IllegalArgumentException("Can't find IPC message with id " + id);
        }
        return this.inbound.get(id).decode(buf);
    }

    public void encode(ByteBuf buf, IpcMessage message) {
        Integer id = this.outbound.get(message.getClass());
        if (id == null) {
            throw new IllegalArgumentException("Can't find packet id for IPC message " + message);
        }
        VarInt.write(buf, id);
        message.encode(buf);
    }

    private record Entry<T extends IpcMessage>(
            Class<T> clazz,
            IpcTypes.Decoder<T> decoder,
            MessageDirection direction
    ) {

        private Entry(Class<T> clazz, MessageDirection direction) {
            this(clazz, ew -> {
                throw new UnsupportedOperationException("Can't decode " + clazz);
            }, direction);
        }
    }
}
