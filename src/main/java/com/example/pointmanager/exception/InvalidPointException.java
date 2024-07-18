package com.example.pointmanager.exception;

public class InvalidPointException extends IllegalArgumentException{

    public InvalidPointException(String message) {
        super(message);
    }

}