package dev.minceraft.sonus.common.protocol.util;

import io.netty.util.Recycler;

import java.util.function.Function;

public class AbstractRecyclerPair<T extends AbstractRecyclerPair<T, A, B>, A, B> {

    private final Recycler.Handle<AbstractRecyclerPair<T, A, B>> handle;
    private A first;
    private B second;

    @SuppressWarnings("unchecked")
    protected AbstractRecyclerPair(Recycler.Handle<T> handle) {
        this.handle = (Recycler.Handle<AbstractRecyclerPair<T, A, B>>) handle;
    }

    public static <T extends AbstractRecyclerPair<T, A, B>, A, B> Recycler<T> createRecycler(
            Function<Recycler.Handle<T>, T> factory) {
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

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public void clear() {
        this.first = null;
        this.second = null;
    }
}
