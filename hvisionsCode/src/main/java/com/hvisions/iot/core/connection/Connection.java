package com.hvisions.iot.core.connection;


/**
 * <p>Title: Connection</p>
 * <p>Description: 连接接口</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public interface Connection {

    /**
     * 连接
     */
    void connect();


    /**
     * 获取点位信息
     */
    void getFieldInfo();

}

    