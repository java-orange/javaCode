package com.hvisions.iot.core.base;

import com.hvisions.iot.utils.BaseInfo;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: DriverConfigInfo</p>
 * <p>Description: 属性配置详情</p>
 * <p>Company: www.h-visions.com</p>
 * <p>create date: 2022/6/16</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */

@Data
public class FieldConfigInfo {

    /**
     * key
     */
    private String key;

    /**
     * label
     */
    private String label;

    /**
     * description
     */
    private String description;

    /**
     * configDataType
     */
    private ConfigDataType configDataType;

    /**
     * 是否展示
     */
    private Boolean display;

    /**
     * 初始值
     */
    private Object initialValue;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否为数组
     */
    private Boolean isArray;

    /**
     * 当数据类型是  ConfigDataType.ENUM 时 可获取枚举成员 作为选项
     */
    private List<BaseInfo> options;

    /**
     * 属性类型是 object 添加子属性列表
     */
    private List<FieldConfigInfo> objectConfig;

    /**
     * 排列顺序
     */
    private Integer order;

    /**
     * 当同组配置的显示条件
     */
    private String condition;

    /**
     * 同组配置
     */
    private String group;

}