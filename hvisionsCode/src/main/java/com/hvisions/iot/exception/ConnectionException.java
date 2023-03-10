package com.hvisions.iot.exception;

public class ConnectionException extends RuntimeException {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Exception e) {
        super(e);
    }

    public ConnectionException(String message, Throwable t) {
        super(message, t);
    }
}
