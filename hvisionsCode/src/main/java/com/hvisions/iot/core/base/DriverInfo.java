package com.hvisions.iot.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: DriverInfo</p>
 * <p>Description: 驱动信息</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverInfo {

    // 驱动名称
    private String[] driverNames;

    // 是否需要新的线程池
    private Boolean needNewExecutor;




}