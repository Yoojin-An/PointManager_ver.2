package com.example.pointmanager.application;

import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.exception.InvalidPointException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointHistoryValidator {
    public void validate(List<PointHistory> pointHistories) {
        if (pointHistories.isEmpty()) {
            throw new InvalidPointException("유효하지 않은 userId 입니다.");
        }
    }
}
