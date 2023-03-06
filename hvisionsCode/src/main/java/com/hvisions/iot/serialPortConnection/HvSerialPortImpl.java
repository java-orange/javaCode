package com.hvisions.iot.serialPortConnection;


import com.hvisions.iot.exception.ConnectionException;
import com.hvisions.iot.exception.ReadException;
import com.hvisions.iot.utils.LittleTools;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import static com.hvisions.iot.serialPortConnection.CommonConstants.waitTime;


/**
 * 同步式读取，断掉程序会自动重连
 */
@Slf4j
public class HvSerialPortImpl implements SerialPortEventListener, HvSerialPort {

    /**
     * 名称，应用程序设定
     */
    private String name;
    /**
     * 串口连接参数
     */
    private HvSerialPortParam param;
    /**
     * RS232串口
     */
    private SerialPort serialPort;

    private InputStream inputStream;

    private OutputStream outputStream;

    public HvSerialPortImpl(String name, HvSerialPortParam param) {
        this.name = name;
        this.param = param;
    }

    @Override
    public void open() {
        if (serialPort != null) {
            close();
        }

        CommPortIdentifier portId = getPortId(param.port());

        if (portId == null) {
            throw new ConnectionException("There is no serial port " + param.port());
        }

        try {
            // open:打开串口
            serialPort = (SerialPort) portId.open(name, waitTime);

            // 设置串口监听
            serialPort.addEventListener(this);
            // 设置串口数据时间有效(可监听)
            serialPort.notifyOnDataAvailable(true);
            // 设置当通信中断时唤醒中断线程
            serialPort.notifyOnBreakInterrupt(true);
            serialPort.notifyOnRingIndicator(true);
            serialPort.notifyOnCarrierDetect(true);
            serialPort.notifyOnFramingError(true);
            serialPort.notifyOnOverrunError(true);
            serialPort.notifyOnParityError(true);
            serialPort.notifyOnOutputEmpty(true);
            // 开启异步接受
            serialPort.enableReceiveTimeout(waitTime);
            // 设置串口通讯参数
            // 波特率，数据位，停止位和校验方式
            serialPort.setSerialPortParams(param.bitrate(), param.dataBits(), param.stopBits(), param.parity());
            serialPort.setDTR(param.DTR());
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            log.info("Open serial port {} successfully", param.port());
        } catch (TooManyListenersException | UnsupportedCommOperationException | PortInUseException | IOException e) {
            log.error(e.getMessage(), e);
            serialPort = null;
            throw new ConnectionException(e);
        }
    }

    @Override
    public void close() {
        LittleTools.closeAll(inputStream, outputStream);
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
        serialPort = null;
        log.info("close serial port {} successfully", param.port());
    }

    @Override
    public Boolean isConnected() {
        return serialPort != null;
    }

    @Override
    public String sendCommand(String command) {
        try {
            sendMsg(command.getBytes());
            String result = receiveMsg();
            return result;
        } catch (Exception e) {
            log.error("read error", e);
            close();
            return null;
        }

    }

    private synchronized String receiveMsg() {
        try {
            if (inputStream == null) {
                return null;
            }

            int available = inputStream.available();
            if (available > 0) {
                byte[] buffer = new byte[available];
                int size = inputStream.read(buffer);
                if (size > 0) {
                    return new String(buffer);
                }
            } else {
                return null;
            }

        } catch (Throwable e) {
            log.error("receive message error", e);
            return null;
        }
        return null;
    }


    @Override
    public void write(byte[] command) {
        sendMsg(command);
    }

    private CommPortIdentifier getPortId(String port) {
        // 获取系统中所有的通讯端口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = portList.nextElement();
            // 判断是否是串口
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (port.equals(portId.getName())) {
                    return portId;
                }
            }
        }
        return null;
    }


    /**
     * 实现接口SerialPortEventListener中的方法 读取从串口中接收的数据
     *
     * @param event 读取从串口中接收的数据
     */
    @Override
    public void serialEvent(SerialPortEvent event) {

        switch (event.getEventType()) {

            // 有数据到达
            case SerialPortEvent.DATA_AVAILABLE:
//                log.info("Listener data arriver");
//                readComm();
                break;
            // 输出缓冲区已清空
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;

            // 通讯中断
            case SerialPortEvent.BI:
                // 溢位错误
            case SerialPortEvent.OE:
                // 帧错误
            case SerialPortEvent.FE:
                // 奇偶校验错误
            case SerialPortEvent.PE:
                // 载波检测
            case SerialPortEvent.CD:
                // 清除发送
            case SerialPortEvent.CTS:
                // 数据设备准备好
            case SerialPortEvent.DSR:
                // 响铃侦测
                // 处理DSR（数据终端就绪）信号变化事件
            case SerialPortEvent.RI:
            default:
                log.error("Listener event {} ,close connect", event.getEventType());
                break;
        }
    }

    /**
     * 向串口发送数据
     * @param command 数据信息
     */
    private void sendMsg(byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            log.error("Write to serial port failed: {}", command, e);
            throw new ReadException(e);
        }
    }
}
