package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.exception.InvalidPointsException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PointsValidator {
    public Points validate(Optional<Points> optionalPoints) {
        return optionalPoints.orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }
}