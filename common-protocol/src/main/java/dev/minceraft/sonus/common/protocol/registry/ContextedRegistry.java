package dev.minceraft.sonus.common.protocol.registry;
// Created by booky10 in Sonus (01:46 17.07.2025)

import dev.minceraft.sonus.common.protocol.util.ObjIntObjectConsumer;
import dev.minceraft.sonus.common.protocol.util.TriConsumer;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

@NullMarked
public class ContextedRegistry<D, T extends ProtocolMessage<?>, C> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");
    // bounded so remote senders cycling through ids can't grow this set indefinitely
    private static final int MAX_WARNED_UNKNOWN_IDS = 128;

    private final Codec<D, T, C> codec;
    private final IdCodec<D, C> idCodec;

    private final Map<Integer, Supplier<? extends T>> constructors;
    private final Map<Class<? extends T>, Integer> packetIds;
    private final Set<Integer> warnedUnknownIds = ConcurrentHashMap.newKeySet();

    protected ContextedRegistry(Codec<D, T, C> codec, IdCodec<D, C> idCodec, List<Entry<? extends T>> packets, BiConsumer<Integer, T> idConsumer) {
        this.codec = codec;
        this.idCodec = idCodec;
        this.constructors = new HashMap<>(packets.size());
        this.packetIds = new IdentityHashMap<>(packets.size());
        for (Entry<? extends T> packet : packets) {
            if (packet.decode()) {
                this.constructors.put(packet.id(), packet.ctor());
            }
            if (packet.encode()) {
                this.packetIds.put(packet.clazz(), packet.id());
            }
            idConsumer.accept(packet.id(), packet.ctor().get());
        }
    }

    public @Nullable T decode(D data, @Nullable C ctx) {
        int packetId = this.idCodec.decoder.applyAsInt(data, ctx);
        Supplier<? extends T> ctor = this.constructors.get(packetId);
        if (ctor == null) {
            // modified or newer clients may send packet ids this registry doesn't know about;
            // drop the packet like upstream implementations do, warning only once per id
            if (this.warnedUnknownIds.size() < MAX_WARNED_UNKNOWN_IDS && this.warnedUnknownIds.add(packetId)) {
                LOGGER.warn("Received packet with unknown id {}, ignoring it (and any further packets with this id)", packetId);
            } else {
                LOGGER.debug("Received packet with unknown id {}, ignoring it", packetId);
            }
            return null;
        }
        T packet = ctor.get();
        this.codec.decoder.accept(data, packet, ctx);
        return packet;
    }

    public void encode(D data, T packet, @Nullable C ctx) {
        Integer packetId = this.packetIds.get(packet.getClass());
        if (packetId == null) {
            throw new IllegalStateException("Can't find id for encoding packet " + packet);
        }
        this.idCodec.encoder.accept(data, packetId, ctx);
        this.codec.encoder.accept(data, packet, ctx);
    }

    public <A extends T> int getPacketId(Class<A> clazz) {
        return this.packetIds.get(clazz);
    }

    public static class Builder<D, T extends ProtocolMessage<?>, C, S extends Builder<D, T, C, S>> {

        protected final List<Entry<? extends T>> packets = new ArrayList<>();
        protected @MonotonicNonNull Codec<D, T, C> codec;
        protected @MonotonicNonNull IdCodec<D, C> idCodec;
        protected BiConsumer<Integer, T> idConsumer = (id, packet) -> {
        };
        protected int nextId = 0;
        protected boolean implicitIdUsed = false;

        protected Builder() {
        }

        public static <D, T extends ProtocolMessage<?>, C> Builder<D, T, C, ?> createContext() {
            return new Builder<>();
        }

        public ContextedRegistry<D, T, C> build() {
            if (this.codec == null) {
                throw new IllegalStateException("Codec must be set before building the registry.");
            }
            if (this.idCodec == null) {
                throw new IllegalStateException("IdCodec must be set before building the registry.");
            }
            return new ContextedRegistry<>(this.codec, this.idCodec, this.packets, this.idConsumer);
        }

        @SuppressWarnings("unchecked")
        protected S getThis() {
            return (S) this;
        }

        public S idOffset(int firstId) {
            if (!this.packets.isEmpty()) {
                throw new IllegalStateException("Can't change id offset after packets have already been registered");
            }
            this.nextId = firstId;
            return this.getThis();
        }

        public <Z extends T> S register(Class<Z> clazz, Supplier<Z> ctor) {
            return this.register(clazz, ctor, MessageDirection.ANY);
        }

        public <Z extends T> S register(Class<Z> clazz, Supplier<Z> ctor, MessageDirection dir) {
            this.implicitIdUsed = true;
            this.packets.add(new Entry<>(this.nextId++, clazz, ctor, dir));
            return this.getThis();
        }

        public <Z extends T> S register(int id, Class<Z> clazz, Supplier<Z> ctor) {
            return this.register(id, clazz, ctor, MessageDirection.ANY);
        }

        public <Z extends T> S register(int id, Class<Z> clazz, Supplier<Z> ctor, MessageDirection dir) {
            if (this.implicitIdUsed) {
                throw new IllegalStateException("Can't register packets with explicit id after implicit ids have been used");
            }
            this.packets.add(new Entry<>(id, clazz, ctor, dir));
            return this.getThis();
        }

        public S codec(TriConsumer<D, T, C> decoder, TriConsumer<D, T, C> encoder) {
            this.codec = new Codec<>(decoder, encoder);
            return this.getThis();
        }

        public S idCodec(ToIntBiFunction<D, C> reader, ObjIntObjectConsumer<D, C> writer) {
            this.idCodec = new IdCodec<>(reader, writer);
            return this.getThis();
        }

        public S idConsumer(BiConsumer<Integer, T> idMapper) {
            this.idConsumer = idMapper;
            return this.getThis();
        }
    }

    protected record Entry<T extends ProtocolMessage<?>>(
            int id, Class<T> clazz, Supplier<T> ctor,
            boolean decode, boolean encode
    ) {

        protected Entry(int id, Class<T> clazz, Supplier<T> ctor, MessageDirection dir) {
            this(id, clazz, ctor, dir.decode, dir.encode);
        }
    }

    protected record IdCodec<D, C>(
            ToIntBiFunction<D, @Nullable C> decoder,
            ObjIntObjectConsumer<D, @Nullable C> encoder
    ) {
    }

    protected record Codec<D, T, C>(
            TriConsumer<D, T, @Nullable C> decoder,
            TriConsumer<D, T, @Nullable C> encoder
    ) {
    }

    public enum MessageDirection {

        ANY(true, true),
        DECODE(true, false),
        ENCODE(false, true),
        NONE(false, false),
        ;

        private final boolean decode;
        private final boolean encode;

        MessageDirection(boolean decode, boolean encode) {
            this.decode = decode;
            this.encode = encode;
        }

        public boolean isDecode() {
            return this.decode;
        }

        public boolean isEncode() {
            return this.encode;
        }
    }
}
