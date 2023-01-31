package com.hvisions.iot.core.base;

/**
 * <p>Title: DriverConnection</p>
 * <p>Description: 驱动连接</p>
 * <p>create date: 2022/11/3</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public interface DriverConnection {

    /**
     * 连接
     */
    void connect();

    /**
     * 获取属性源信息
     *
     */
    void getFieldInfo();


}

    