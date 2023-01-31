package com.hvisions.iot.example;

import com.hvisions.iot.core.base.FieldConfigInfo;
import com.hvisions.iot.core.manager.DriverManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * ClassName: Main
 * Package: IntelliJ IDEA
 * Description:
 *
 * @Author xhjing
 * @Create 2023/1/31 15:14
 * @Version 1.0
 */

@Slf4j
public class Main {
    public static void main(String[] args) {

        DriverManager driverManager = DriverManager.inst();

        List<FieldConfigInfo> opcua = driverManager.getDriverConfigByModel("opcua");
        log.info("opcua = {}", opcua);

        driverManager.parseAllConnectionByFile("driverConnection.yml");

        Set<String> connectionNames = driverManager.getConnectionNames();
        log.info("connectionNames = {}", connectionNames);

    }

}
