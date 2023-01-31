package com.hvisions.iot.core.base;

import lombok.Data;

/**
 * <p>Title: BaseConnectionConfig</p>
 * <p>Description: 连接基础配置</p>
 * <p>Company: www.h-visions.com</p>
 * <p>create date: 2022/6/16</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
@Data
public class BaseConnectionConfig {

    /**
     * 连接id
     */
    @FieldConfig(label = "连接id", description = "连接id", required = true, configDataType = ConfigDataType.NUMBER, order = 1000)
    private String id;

    /**
     * 连接名称
     */
    @FieldConfig(label = "连接名称", description = "连接名称", required = true, order = 2000)
    private String name;


    @FieldConfig(label = "model", description = "连接模块", required = true, display = false, order = 3000)
    private String model;

}