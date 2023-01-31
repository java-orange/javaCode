package com.hvisions.iot.core.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: ConnectionExecutor</p>
 * <p>Description: 连接池</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
@Slf4j
public class ConnectionExecutor {
    private static int corePoolSize = 2;
    private static int maxPoolSize = 8;

    /**
     * the capacity of the work queue, maxPoolSize won't take effect unless queued task is
     * larger than WorkQueueCapacity
     */
    private static int WORK_QUEUE_CAPACITY = 200;

    static {
        try {
            int driverCorePoolSize = Integer.parseInt(System.getProperty("driverCorePoolSize"));
            if (Objects.nonNull(driverCorePoolSize)) {
                corePoolSize = driverCorePoolSize;
            }

            int driverMaxPoolSize = Integer.parseInt(System.getProperty("driverMaxPoolSize"));
            if (Objects.nonNull(driverMaxPoolSize)) {
                maxPoolSize = driverMaxPoolSize;
            }

            int driverWorkQueue = Integer.parseInt(System.getProperty("driverWorkQueue"));

            if (Objects.nonNull(driverWorkQueue)) {
                WORK_QUEUE_CAPACITY = driverWorkQueue;
            }
        } catch (Exception e) {
            log.error("get system param error, use default param to init thread pool ", e);
        }
        log.info("init driver common share thread pool " +
                "driverCorePoolSize = {}, driverMaxPoolSize = {}, driverWorkQueue = {} ", corePoolSize, maxPoolSize, WORK_QUEUE_CAPACITY);

    }


    private ExecutorService executor;

    private static class ConnectionExecutorHolder {
        private static ConnectionExecutor instance = new ConnectionExecutor();
    }

    private ConnectionExecutor() {
        executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(WORK_QUEUE_CAPACITY));
    }

    public static ConnectionExecutor inst() {
        return ConnectionExecutorHolder.instance;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
