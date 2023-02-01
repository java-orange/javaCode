package org.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: Java8CompletableFutureTimeoutTest
 * Package: IntelliJ IDEA
 * Description:
 *
 * @Author xhjing
 * @Create 2023/1/31 16:39
 * @Version 1.0
 */

@Slf4j
public class Java8CompletableFutureTimeoutTest {

    /**
     * 并行跑任务， 规定限时，多余抛弃, 可自行增加线程池进行控制
     */
    @Test
    public void testWithIn() {

        log.info("Java8CompletableFutureTimeoutTest.testWithIn begin");
        Integer timeOut = 5;

        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        List<CompletableFuture<Integer>> completableFutureList = new ArrayList<>();

        list.stream().forEach(num -> {
            CompletableFuture<Integer> within = Java8CompletableFutureTimeout.within(CompletableFuture.supplyAsync(() -> {
                log.info("num = {} start!", num);
                try {
                    TimeUnit.SECONDS.sleep(num);
                } catch (InterruptedException e) {
                    log.error("num {} InterruptedException", e);
                }
                log.info("num = {} end!", num);
                return num;
            }), timeOut, TimeUnit.SECONDS);
            completableFutureList.add(within);
        });

        try {
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
        } catch (Exception e) {
            log.error("some future time out", e);
        }
        log.info("Java8CompletableFutureTimeoutTest.testWithIn over");

    }
}