package com.example.pointmanager.exception;

public class FailToAcquireLockException extends RuntimeException{
    public FailToAcquireLockException(String message) {
        super(message);
    }

    public FailToAcquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
