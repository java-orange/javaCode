package com.hvisions.iot.drivers.aliyun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: AliyunSetting</p>
 * <p>Description: 阿里云配置</p>
 * <p>create date: 2022/11/23</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliyunSetting {
    // 阿里云host
    private String host;
    // 阿里云api授权key
    private String accessKey;
    // 阿里云api授权secret
    private String accessSecret;
    // 超时时间, 默认10秒
    private Integer timeout = 1000 * 10;
    // 最大闲置连接数 默认30
    private Integer maxIdleConnections = 30;
    // 支持的最大请求数 默认200
    private Integer maxRequests = 200;

}