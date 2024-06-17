package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.exception.InvalidPointsException;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OptimisticLockHandler optimisticLockHandler;
    private final PessimisticLockHandler pessimisticLockHandler;

    public Point findPoint(long userId) {
        return pointRepository.findPointById(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }

    public List<PointHistory> findPointHistories(long userId) {
        List<PointHistory> pointHistories = pointHistoryRepository.findAllPointHistoriesByUserId(userId);
        if (pointHistories.isEmpty()) {
            throw new InvalidPointsException("userId가 존재하지 않습니다.");
        }
        return pointHistories;
    }

    @Transactional
    public Point chargePoint(long userId, long chargeAmount, boolean useOptimisticLock) {
        Point point;
        if (useOptimisticLock) {
            point = optimisticLockHandler.getOrCreatePointWithOptimisticLock(pointRepository, userId);
        } else {
            point = pessimisticLockHandler.getOrCreatePointWithPessimisticLock(pointRepository, userId);
        }

        Point updatedPoint = point.charge(chargeAmount);

        pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, chargeAmount, PointHistory.TransactionType.CHARGE));

        return updatedPoint;
    }

    @Transactional
    public Point usePoint(long userId, long useAmount, boolean useOptimisticLock) {
        Point point;
        if (useOptimisticLock) {
            point = optimisticLockHandler.getPointWithOptimisticLock(pointRepository, userId);
        } else {
            point = pessimisticLockHandler.getPointWithPessimisticLock(pointRepository, userId);
        }

        Point updatedPoint = point.charge(useAmount);

        pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, useAmount, PointHistory.TransactionType.CHARGE));

        return updatedPoint;
    }
}
