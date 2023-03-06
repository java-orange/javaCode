package com.hvisions.iot.exception;

public class DisconnectedException extends RuntimeException {
    public DisconnectedException(String message) {
        super(message);
    }

    public DisconnectedException(Exception e) {
        super(e);
    }

    public DisconnectedException(String message, Throwable t) {
        super(message, t);
    }
}
