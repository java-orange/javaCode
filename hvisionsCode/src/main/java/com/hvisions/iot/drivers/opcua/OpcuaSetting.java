package com.hvisions.iot.drivers.opcua;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hvisions.iot.core.base.BaseConnectionConfig;
import com.hvisions.iot.core.base.ConfigDataType;
import com.hvisions.iot.core.base.FieldConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpcuaSetting extends BaseConnectionConfig {
    /**
     * opcua 连接URL
     */
    @FieldConfig(label = "连接地址", required = true, order = 10)
    private String address;
    /**
     * opc连接端口
     */
    @FieldConfig(label = "端口", required = true, configDataType = ConfigDataType.NUMBER, initialValue = "49320", order = 20)
    private Integer port;

    /**
     * 请求超时时间, 毫秒
     */
    @FieldConfig(label = "连接超时", description = "连接超时 单位：毫秒", initialValue = "10000", configDataType = ConfigDataType.NUMBER, order = 30, display = false)
    private Integer connectTimeout = 10000;

    /**
     * 是否匿名访问
     */
    @FieldConfig(label = "是否匿名访问", configDataType = ConfigDataType.BOOL,
            initialValue = "true", group = "login", condition = "anonymous=false", order = 60)
    private Boolean anonymous;
    /**
     * 用户名
     */
    @FieldConfig(label = "用户名", group = "login", order = 80)
    private String username;
    /**
     * 密码
     */
    @FieldConfig(label = "密码", group = "login", order = 90)
    private String password;

//    /**
//     * 安全策略
//     */
//    @FieldConfig(label = "安全策略", configDataType = ConfigDataType.ENUM, initialValue = "None", order = 40)
//    private SecurityPolicy securityPolicy;
//    /**
//     * 消息安全模式
//     */
//    @FieldConfig(label = "消息安全模式", configDataType = ConfigDataType.ENUM, initialValue = "None", order = 50)
//    private MessageSecurityMode securityMode;

    // 证书文件， base64存储
    @FieldConfig(label = "证书文件", description = "上传客户端证书，使用base64编码发送", order = 51)
    private String cert;

    // 证书文件名称（给定默认名称）
    @FieldConfig(label = "证书文件名称", order = 52)
    private String certName = "client-certificate.pfx";

    /**
     * 是否启用订阅模式，启用订阅后，所有属性在OPCUA连接完成后建立值改变订阅函数，获取到的值存在本地缓存，读值时读缓存里的值
     */
    @FieldConfig(label = "订阅模式", description = "是否启用订阅模式，启用订阅后，所有属性在OPCUA连接完成后建立值改变订阅函数，获取到的值存在本地缓存，读值时读缓存里的值",
            configDataType = ConfigDataType.BOOL, initialValue = "false", group="subscribe", condition = "enableSubscribe=true", order = 70)
    private Boolean enableSubscribe = false;

    /**
     * 订阅为Cycle模式时，请求间隔时间, 默认120秒，毫秒
     */
    @FieldConfig(label = "订阅循环周期(毫秒)", description = "opcua连接将使用该周期进行循环读值",
            configDataType = ConfigDataType.NUMBER, initialValue = "500", group="subscribe", order = 71, display = false)
    private Integer cycleTime = -1;
}
