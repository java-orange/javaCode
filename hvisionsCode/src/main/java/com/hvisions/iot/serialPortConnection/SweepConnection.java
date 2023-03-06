package com.hvisions.iot.serialPortConnection;

/**
 * ClassName: SweepConnection
 * Package: IntelliJ IDEA
 * Description: 扫码设备
 *
 * @Author xhjing
 * @Create 2023/3/2 16:24
 * @Version 1.0
 */
public interface SweepConnection {

    void open();

    void close();

    default void reconnect() {
        close();
        open();
    }

    Boolean isConnected();

    String sendCommand(String command);


}
