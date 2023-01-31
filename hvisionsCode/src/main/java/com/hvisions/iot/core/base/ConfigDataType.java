package com.hvisions.iot.core.base;

/**
 * <p>Title: ConfigDataType</p>
 * <p>Description: 可配置选项</p>
 * <p>Company: www.h-visions.com</p>
 * <p>create date: 2022/6/16</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public enum ConfigDataType {

    TEXT("普通文本数据"),
    NUMBER("数字文本数据"),
    SCRIPT("脚本解析数据"),
    BOOL("布尔变量数据"),
    ENUM("枚举数据"),   // 枚举类型必须实现 com.hvisions.iot.utils.Description
    OBJECT("对象数据"),
    MAP("键值对 map 数据");





    private String label;

    ConfigDataType(String label) {
        this.label = label;
    }
}