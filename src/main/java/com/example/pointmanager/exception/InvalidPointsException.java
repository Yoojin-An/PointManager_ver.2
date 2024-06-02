package com.example.pointmanager.domain.points.exceptions;

public class InvalidPointsException extends IllegalArgumentException{

    public InvalidPointsException(String message) {
        super(message);
    }
}