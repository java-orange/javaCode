package org.example;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * ClassName: Java8CompletableFutureTimeout
 * Package: IntelliJ IDEA
 * Description: java8 中 completureFuture超时取消
 *
 * @Author xhjing
 * @Create 2023/1/31 16:37
 * @Version 1.0
 */
public class Java8CompletableFutureTimeout {
    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        final CompletableFuture<T> timeoutFuture = timeoutAfter(timeout, unit);
        // 哪个先完成 就apply哪一个结果 这是一个关键的API
        return future.applyToEither(timeoutFuture, Function.identity());
    }

    public static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<T>();
        // timeout 时间后 抛出TimeoutException 类似于sentinel / watcher
        Delayer.delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

    /**
     * Singleton delay scheduler, used only for starting and * cancelling tasks.
     */
    static final class Delayer {
        static ScheduledFuture<?> delay(Runnable command, long delay,
                                        TimeUnit unit) {
            return delayer.schedule(command, delay, unit);
        }

        static final class DaemonThreadFactory implements ThreadFactory {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("CompletableFutureDelayScheduler");
                return t;
            }
        }

        static final ScheduledThreadPoolExecutor delayer;

        // 注意，这里使用一个线程就可以搞定 因为这个线程并不真的执行请求 而是仅仅抛出一个异常
        static {
            (delayer = new ScheduledThreadPoolExecutor(
                    1, new DaemonThreadFactory())).
                    setRemoveOnCancelPolicy(true);
        }
    }

}
