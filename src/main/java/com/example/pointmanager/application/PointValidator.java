package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.exception.InvalidPointException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PointValidator {
    public void validate(Optional<Point> point) {
        point.orElseThrow(() -> new InvalidPointException("userId가 유효하지 않습니다."));
    }
}
