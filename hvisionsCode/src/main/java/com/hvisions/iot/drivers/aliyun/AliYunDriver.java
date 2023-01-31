package com.hvisions.iot.drivers.aliyun;

import com.hvisions.iot.core.base.Driver;
import com.hvisions.iot.core.base.DriverConnection;
import com.hvisions.iot.core.logger.Logger;
import com.hvisions.iot.utils.thread.Timer;

import java.util.concurrent.ExecutorService;

/**
 * ClassName: AliYunDriver
 * Package: IntelliJ IDEA
 * Description: 阿里云驱动实现
 *
 * @Author xhjing
 * @Create 2023/1/31 15:17
 * @Version 1.0
 */

@Driver(names = {"aliyun"}, driverConfig = AliyunSetting.class)
public class AliYunDriver implements DriverConnection {

    private final AliyunSetting aliyunSetting;

    private final ExecutorService executorService;

    private final Timer timer;

    private final Logger log;

    public AliYunDriver(AliyunSetting aliyunSetting, ExecutorService executorService, Timer timer, Logger log) {
        this.aliyunSetting = aliyunSetting;
        this.executorService = executorService;
        this.timer = timer;
        this.log = log;
    }

    @Override
    public void connect() {
        log.info("aliyunSetting = {}", aliyunSetting);
        log.info("executorService = {}", executorService);
        log.info("timer = {}", timer);
        log.info("log = {}", log);
    }

    @Override
    public void getFieldInfo() {

    }
}
