package dev.minceraft.sonus.common.protocol.udp;

import io.netty.util.Recycler;

import java.net.InetSocketAddress;
import java.util.function.Function;

public abstract class UdpBasedContext<T extends UdpBasedContext<T>> {

    private final Recycler.Handle<UdpBasedContext<T>> handle;
    public InetSocketAddress remoteAddress;

    @SuppressWarnings("unchecked")
    protected UdpBasedContext(Recycler.Handle<T> handle) {
        this.handle = (Recycler.Handle<UdpBasedContext<T>>) handle;
    }

    public static <T extends UdpBasedContext<T>> Recycler<T> createRecycler(Function<Recycler.Handle<T>, T> factory) {
        return new Recycler<>() {
            @Override
            protected T newObject(Handle<T> handle) {
                return factory.apply(handle);
            }
        };
    }

    public void recycle() {
        this.clear();
        this.handle.recycle(this);
    }

    protected void clear() {
        this.remoteAddress = null;
    }
}
