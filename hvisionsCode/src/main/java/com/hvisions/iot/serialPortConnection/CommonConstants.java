package com.hvisions.iot.serialPortConnection;


/**
 * ClassName: CommonConstants
 * Package: IntelliJ IDEA
 * Description:
 *
 * @Author xhjing
 * @Create 2023/3/1 11:15
 * @Version 1.0
 */
public interface CommonConstants {

    String batch_sweep_name = "batch_sweep";

    String batch_sweep_ip = "192.168.100.2";

    Integer batch_sweep_port = 2001;

    String batch_sweep_readSign = "start";


    Integer waitTime = 3000;

    String single_sweep_name = "single_sweep";

    String single_sweep_port = "COM5";

    Integer single_sweep_bitrate = 115200;

    Boolean single_sweep_DTR = true;

    String single_sweep_readSign = "T";

}
