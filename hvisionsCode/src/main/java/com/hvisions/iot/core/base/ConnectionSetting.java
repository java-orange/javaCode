package com.hvisions.iot.core.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionSetting {

    public static final String CONNECTION_NAME = "connectionName";

    public static final String CONNECTION_ID = "connectionId";

    /**
     * 连接ID
     */
    private String connectionId;


    /**
     * 连接标识
     */
    private String connectionName;

}
