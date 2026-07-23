package dev.minceraft.sonus.service.service;

public interface IScheduledTask {

    default void cancel() {
        cancel(false);
    }

    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();

    boolean isDone();
}
