package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.exception.InvalidPointsException;
import com.example.pointmanager.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PessimisticLockHandler {

    public Point getOrCreatePointWithPessimisticLock(PointRepository pointRepository,
                                                     long userId) {
        return pointRepository.findPointByUserIdWithPessimisticLock(userId)
                .orElseGet(() -> new Point(userId, 0));
    }

    public Point getPointWithPessimisticLock(PointRepository pointRepository,
                                             long userId) {
        return pointRepository.findPointByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }
}
