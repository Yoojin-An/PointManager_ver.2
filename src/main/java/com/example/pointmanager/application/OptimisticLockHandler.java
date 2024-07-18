package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.exception.InvalidPointException;
import com.example.pointmanager.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class OptimisticLockHandler implements LockStrategyHandler {

    private final PointValidator pointValidator;

    @Override
    public Point getOrCreatePointWithLock(PointRepository pointRepository, long userId) {
        while (true) {
            try {
                return pointRepository.findPointByUserIdWithOptimisticLock(userId)
                        .orElseGet(() -> new Point(userId, 0));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Point getPointWithLock(PointRepository pointRepository, long userId) {
        while (true) {
            try {
                Optional<Point> point = pointRepository.findPointByUserIdWithOptimisticLock(userId);
                pointValidator.validate(point);
                return point.get();
            } catch (InvalidPointException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
