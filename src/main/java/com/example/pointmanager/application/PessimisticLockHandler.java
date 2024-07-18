package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.exception.InvalidPointException;
import com.example.pointmanager.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PessimisticLockHandler implements LockStrategyHandler{

    private final PointValidator pointValidator;

    @Override
    public Point getOrCreatePointWithLock(PointRepository pointRepository, long userId) {
        return pointRepository.findPointByUserIdWithPessimisticLock(userId)
                .orElseGet(() -> new Point(userId, 0));
    }

    @Override
    public Point getPointWithLock(PointRepository pointRepository, long userId) {
        Optional<Point> point = pointRepository.findPointByUserIdWithPessimisticLock(userId);
        pointValidator.validate(point);
        return point.get();
    }
}
