package com.hvisions.iot.exception;

/**
 * <p>Title: WriteException</p>
 * <p>Description: 写入错误</p>
 * <p>Company: www.h-visions.com</p>
 * <p>create date: 2022/5/18</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public class WriteException extends RuntimeException{
    public WriteException() {
        super();
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}