package com.hvisions.iot.serialPortConnection;

import java.util.concurrent.TimeUnit;

import static com.hvisions.iot.serialPortConnection.CommonConstants.*;

/**
 * ClassName: Main
 * Package: IntelliJ IDEA
 * Description:
 *
 * @Author xhjing
 * @Create 2023/3/1 10:06
 * @Version 1.0
 */
public class TestHvSerialPort {
    public static void main(String[] args) throws Exception {
        HvSerialPortParam param = new HvSerialPortParam();
        param.port(single_sweep_port).bitrate(single_sweep_bitrate).DTR(single_sweep_DTR);

        HvSerialPort port = new HvSerialPortImpl("单扫串口通信", param);
        port.open();
        System.out.println("port status: " + port.isConnected());

        for (int i = 0; i < 1000; i++) {

            try {
                String s = port.sendCommand(single_sweep_readSign);
                System.out.println("s = " + s);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        port.close();
        System.out.println("port status: " + port.isConnected());
        System.out.println("done ! ");

        System.out.println("over ");
    }
}
