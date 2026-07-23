package dev.minceraft.sonus.service;

import dev.minceraft.sonus.service.service.IScheduledTask;
import dev.minceraft.sonus.service.service.ISonusScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SonusScheduler implements ISonusScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);

    @Override
    public void execute(Runnable task) {
        this.scheduler.execute(new WrappedRunnable(task));
    }

    @Override
    public IScheduledTask schedule(Runnable task, long delay, long period, TimeUnit unit) {
        ScheduledFuture<?> scheduledFuture = this.scheduler.scheduleAtFixedRate(new WrappedRunnable(task), delay, period, unit);
        return new ScheduledTask(scheduledFuture);
    }

    @Override
    public IScheduledTask schedule(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> scheduledFuture = this.scheduler.schedule(new WrappedRunnable(task), delay, unit);
        return new ScheduledTask(scheduledFuture);
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }

    private record WrappedRunnable(Runnable runnable) implements Runnable {

        @Override
        public void run() {
            try {
                this.runnable.run();
            } catch (Throwable throwable) {
                LOGGER.error("An error occurred while executing scheduled task: {}", throwable.getMessage(), throwable);
            }
        }
    }

    private record ScheduledTask(ScheduledFuture<?> task) implements IScheduledTask {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.task.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.task.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.task.isDone();
        }
    }
}
