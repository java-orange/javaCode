package com.hvisions.iot.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable t) {
        super(message, t);
    }


}
