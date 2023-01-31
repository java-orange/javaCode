package com.hvisions.iot.drivers.opcua;

import com.hvisions.iot.core.base.Driver;
import com.hvisions.iot.core.base.DriverConnection;

/**
 * ClassName: OpcuaDriver
 * Package: IntelliJ IDEA
 * Description: opcua驱动
 *
 * @Author xhjing
 * @Create 2023/1/31 15:44
 * @Version 1.0
 */

@Driver(names = {"opcua"},
        needNewExecutor = false,
        driverConfig = OpcuaSetting.class)
public class OpcuaDriver implements DriverConnection {

    @Override
    public void connect() {

    }

    @Override
    public void getFieldInfo() {

    }
}
