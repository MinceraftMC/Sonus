package dev.minceraft.sonus.service.api;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class ApiDelegation<T> {

    protected final T delegate;

    public ApiDelegation(T delegate) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return this.delegate;
    }
}
