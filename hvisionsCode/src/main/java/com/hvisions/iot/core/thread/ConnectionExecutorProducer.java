package com.hvisions.iot.core.thread;
/**
 * @author xhjing
 * @create 2021-09-15 15:44
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: ConnectionExecutors</p>
 * <p>Description: 线程池</p>
 * <p>create date: 2021/9/15</p>
 *@author : xhjing
 *@version :1.0.0
 */
public class ConnectionExecutorProducer {
    private static int corePoolSize = 4;
    private static int maxPoolSize = 16;

    private static int workQueueCapacity = 200;

    private ConnectionExecutorProducer() {
    }

    /**
     * 获取默认线程池
     * @return
     */
    public static ExecutorService getDefaultExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(workQueueCapacity));
    }

    /**
     * 获取自定义线程池
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @return
     */
    public static ExecutorService getCustomExecutor(Integer corePoolSize, Integer maxPoolSize) {
        Integer tempCorePoolSize = corePoolSize == null ? 4 : corePoolSize;
        Integer tempMaxPoolSize = maxPoolSize == null ? 16 : maxPoolSize;
        return new ThreadPoolExecutor(
                tempCorePoolSize,
                tempMaxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(workQueueCapacity));
    }

}