package com.hvisions.iot.utils.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class Timer {
    private static final int DefaultTaskPoolSize = 4;

    private final AtomicLong timerCounter = new AtomicLong(1);

    private volatile ConcurrentMap<Long, ScheduledFuture<?>> timerTasks = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;

    public Timer() {
        this(DefaultTaskPoolSize);
    }

    public Timer(int taskPoolSize) {
        scheduler = Executors.newScheduledThreadPool(taskPoolSize);
    }

    /**
     * 设置周期执行任务
     *
     * @param period 定时间隔时间，单位为毫秒
     * @param task 周期执行的任务
     * @return 定时器ID
     */
    public long setPeriodic(int period, Runnable task) {
        Runnable runnable = () -> {
            try {
                task.run();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        };

        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MILLISECONDS);

        long id = timerCounter.getAndIncrement();
        timerTasks.put(id, sf);

        return id;
    }

    public long setPeriodicDelay(int period, Runnable task) {
        Runnable runnable = () -> {
            try {
                task.run();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        };

        ScheduledFuture<?> sf = scheduler.scheduleWithFixedDelay(runnable, 0, period, TimeUnit.MILLISECONDS);

        long id = timerCounter.getAndIncrement();
        timerTasks.put(id, sf);

        return id;
    }

    /**
     * 设置延时执行的任务
     *
     * @param delay 延时执行时间，毫秒
     * @param task 执行的任务
     */
    public void runAfter(int delay, Runnable task) {
        Runnable runnable = () -> {
            try {
                task.run();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        };

        scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public boolean cancelTimer(long id) {
        ScheduledFuture<?> sf = timerTasks.get(id);

        if (sf == null) {
            log.warn("The timer {} doesn't exist", id);
            return false;
        }
        try {
            log.info("Stop the timer {}", id);
            return sf.cancel(true);
        } catch (Exception e) {
            log.error("Failed to stop the timer {}", id, e);
            return false;
        }
    }
}
