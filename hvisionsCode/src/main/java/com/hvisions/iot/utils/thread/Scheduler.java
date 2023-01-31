package com.hvisions.iot.utils.thread;

public interface Scheduler {

    /**
     * 创建cron task
     * @param name task唯一标识
     * @param cron cron表达式
     * @return job id
     */
    void runCronTask(String name, String cron, Runnable runner);


    /**
     * 取消cron task
     * @param name task唯一标识
     */
    void cancelCronTask(String name);
}
