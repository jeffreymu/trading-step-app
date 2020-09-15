package com.cjhxfund.step.application.health;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleTimer {

    private ScheduledExecutorService exec = null;

    public SimpleTimer() {
        exec = Executors.newScheduledThreadPool(10);
    }

    public SimpleTimer(String name) {
        this();
    }

    public void cancel() {
        exec.shutdown();
    }

    public void scheduleAtFixedRate(SimpleTimerTask task, long delay,
                                    long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        exec.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
    }

    public void schedule(SimpleTimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        exec.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative
     * @throws IllegalStateException    if task was already scheduled or cancelled, or timer was
     *                                  cancelled.
     */
    public void schedule(SimpleTimerTask task, long delay) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        exec.schedule(task, delay, TimeUnit.MILLISECONDS);
    }
}
