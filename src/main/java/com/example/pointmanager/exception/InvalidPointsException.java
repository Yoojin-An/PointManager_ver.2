package com.example.pointmanager.exception;

public class InvalidPointsException extends IllegalArgumentException{

    public InvalidPointsException(String message) {
        super(message);
    }

}