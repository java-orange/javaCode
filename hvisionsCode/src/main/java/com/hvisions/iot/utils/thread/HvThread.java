package com.hvisions.iot.utils.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HvThread {
    /**
     * The thread name
     */
    private String name;
    private Thread thread;
    private boolean isRunning = true;

    /**
     * 任务执行间隔时间, 单位毫秒
     */
    private Integer runPeriod;

    /**
     * 任务轮询间隔时间，单位毫秒，默认500毫秒
     */
    private Integer pollInterval = 500;

    private Runnable runTask;

    private Runnable preRun;
    private Runnable postRun;

    public HvThread() {

    }

    public HvThread(String name) {
        this.name = name;
    }

    public HvThread start() {
        if (thread == null) {
            thread = new Thread(this::run);
        }

        isRunning = true;
        if (StringUtils.isNotEmpty(name)) {
            thread.setName(name);
        }
        thread.start();

        log.info("Begin to start thread {}", thread.getId());

        return this;
    }

    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    private void run() {
        if (preRun != null) {
            preRun.run();
        }

        // 默认1秒
        long period = runPeriod == null ? 1000 : runPeriod;

        long startAt = 0;
        // 目前仅支持循环轮询模式
        while (isRunning) {
            long current = System.currentTimeMillis();

            if ((current - startAt) >= period) {
                startAt = current;

                try {
                    if (runTask != null) {
                        runTask.run();
                    }
                } catch (Exception e) {
                    log.error("Failed to run the thread task in thread {}/{}", thread.getId(), thread.getName(), e);
                }

            }

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (postRun != null) {
            postRun.run();
        }
        log.info("Thread {} exited", thread.getName());

        isRunning = true;
    }

    public HvThread setPreRun(Runnable preRun) {
        this.preRun = preRun;

        return this;
    }

    public HvThread setPostRun(Runnable postRun) {
        this.postRun = postRun;

        return this;
    }
    public HvThread setRunTask(Runnable runTask) {
        this.runTask = runTask;

        return this;
    }

    public HvThread setRunPeriod(Integer runPeriod) {
        this.runPeriod = runPeriod;

        return this;
    }

    public HvThread setPollInterval(Integer pollInterval) {
        this.pollInterval = pollInterval;

        return this;
    }


    public HvThread close() {
        isRunning = false;

        if (!thread.isAlive()) {
            return this;
        }

        while (!isRunning && thread.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        isRunning = false;

        return this;
    }
}
