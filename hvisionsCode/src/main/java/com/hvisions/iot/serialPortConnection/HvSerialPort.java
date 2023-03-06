package com.hvisions.iot.serialPortConnection;


public interface HvSerialPort extends SweepConnection {

    void write(byte[] command);
}
