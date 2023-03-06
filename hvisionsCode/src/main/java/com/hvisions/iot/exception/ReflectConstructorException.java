package com.hvisions.iot.exception;

/**
 * <p>Title: ReflectConstructorException</p>
 * <p>Description: 反射构造异常</p>
 * <p>create date: 2022/11/12</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public class ReflectConstructorException extends RuntimeException{
    public ReflectConstructorException() {
    }

    public ReflectConstructorException(String message) {
        super(message);
    }

    public ReflectConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}