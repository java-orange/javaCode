package com.hvisions.iot.utils.thread;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
public class SchedulerImpl implements Scheduler {
    private final static String JobKey = "scheduler";
    private final static String GroupName = "scheduler-group";

    private org.quartz.Scheduler scheduler;

    private final static Map<String, Task> tasks = new ConcurrentHashMap<>();

    public SchedulerImpl() {
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();

            scheduler.start();
        } catch (Exception e) {
            log.error("Create scheduler failed", e);
        }
    }

    @Override
    public void runCronTask(String name, String cron, Runnable runner) {
        try {
            if (tasks.containsKey(name)) {
                cancelCronTask(name);
                log.info("Task {} is already exists, cancel the previous first", name);
            }

            Task task = createCronTask(name, cron);
            task.runner(runner);

            tasks.put(name, task);

            // run it as the first time
            runner.run();
        } catch (Exception e) {
            log.error("Create task {} with cron {} failed", name, cron, e);
        }
    }

    @Override
    public void cancelCronTask(String name) {
        if (!tasks.containsKey(name)) {
            log.info("Task {} is not create", name);

            return;
        }

        Task task = tasks.get(name);

        try {
            scheduler.deleteJob(task.job().getKey());
        } catch (Exception e) {
            log.error("Cancel task {} failed with: {}", e.getMessage(), e);
        }
    }


    private Task createCronTask(String name, String cron) throws SchedulerException {
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger-" + name, GroupName)
                .withSchedule(cronSchedule(cron))
                .build();

        JobDetail job = newJob(JobRunner.class)
                .withIdentity(name, GroupName)
                .build();

        scheduler.scheduleJob(job, trigger);

        return new Task().name(name)
                .job(job);
    }

    public static class JobRunner implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            String jobName = jobExecutionContext.getJobDetail().getKey().getName();

            Task task = tasks.get(jobName);
            if (task != null) {
                task.runner.run();
            }
        }
    }

    @Data
    @Accessors(fluent = true)
    private static class Task {
        private String name;
        private Runnable runner;
        private JobDetail job;
    }
}
