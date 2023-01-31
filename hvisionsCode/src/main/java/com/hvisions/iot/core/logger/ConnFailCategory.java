package com.hvisions.iot.core.logger;

import com.hvisions.iot.utils.Description;

import java.io.Serializable;

/**
 * <p>Title: ConnectionExceptionType</p>
 * <p>Description: 连接异常分类</p>
 * <p>Company: www.h-visions.com</p>
 * <p>create date: 2022/6/30</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public enum ConnFailCategory implements Serializable, Description {

    NETWORK_ERROR("连接错误"),

    NETWORK_TIMEOUT("网络超时"),

    REQUEST_ERROR("请求错误"),

    DEVICE_ERROR("设备异常"),

    INNER_ERROR("程序内部处理错误"),

    DEVICE_STATUS("设备上下线"),

    UNKNOWN("未知错误");

    private String label;

    ConnFailCategory(String label) {
        this.label = label;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getLabel() {
        return label;
    }
}