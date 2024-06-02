package com.example.pointmanager.domain.points.exceptions;

public class FailToAcquireLockException extends RuntimeException{
    public FailToAcquireLockException(String message) {
        super(message);
    }

    public FailToAcquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
