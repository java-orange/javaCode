package com.hvisions.iot.core.connection;

import com.hvisions.iot.core.base.DriverConnection;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>Title: ConnectionImpl</p>
 * <p>Description: 连接实例</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
@Slf4j
public class ConnectionImpl implements Connection {

    /**
     * 驱动连接
     */
    private DriverConnection driverConnection;


    /**
     * 用于保留上层的 连接层， 方便后续用来统一处理，若需要线程池，调度器，统一配置等，放入该层可处理
     * @param driverConnection
     */
    public ConnectionImpl(DriverConnection driverConnection) {
        this.driverConnection = driverConnection;
    }


    @Override
    public void connect() {
        driverConnection.connect();

    }

    @Override
    public void getFieldInfo() {
        throw new UnsupportedOperationException("暂不支持获取属性列表");
    }


}