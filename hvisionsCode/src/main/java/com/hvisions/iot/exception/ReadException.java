package com.hvisions.iot.exception;

public class ReadException extends RuntimeException{
    public ReadException(String message) {
        super(message);
    }

    public ReadException(Exception e) {
        super(e);
    }

    public ReadException(String message, Throwable t) {
        super(message, t);
    }
}
