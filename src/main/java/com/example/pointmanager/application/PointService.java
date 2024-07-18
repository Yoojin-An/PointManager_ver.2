package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final LockHandlerFactory LockHandlerFactory;
    private final PointValidator pointValidator;
    private final PointHistoryValidator pointHistoryValidator;

    public Point findPoint(long userId) {
        Optional<Point> point = pointRepository.findPointById(userId);
        pointValidator.validate(point);
        return point.get();
    }

    public List<PointHistory> findPointHistories(long userId) {
        List<PointHistory> pointHistories = pointHistoryRepository.findAllPointHistoriesByUserId(userId);
        pointHistoryValidator.validate(pointHistories);
        return pointHistories;
    }

    @Transactional
    public Point chargePoint(long userId, long chargeAmount, boolean usePessimisticLock) {
        LockStrategyHandler lockStrategyHandler = LockHandlerFactory.getLockHandler(usePessimisticLock);
        Point point = lockStrategyHandler.getOrCreatePointWithLock(pointRepository, userId);
        Point updatedPoint = point.charge(chargeAmount);

        pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, chargeAmount, PointHistory.TransactionType.CHARGE));

        return updatedPoint;
    }

    @Transactional
    public Point usePoint(long userId, long useAmount, boolean usePessimisticLock) {
        LockStrategyHandler lockStrategyHandler = LockHandlerFactory.getLockHandler(usePessimisticLock);
        Point point = lockStrategyHandler.getPointWithLock(pointRepository, userId);
        Point updatedPoint = point.charge(useAmount);

        pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, useAmount, PointHistory.TransactionType.CHARGE));

        return updatedPoint;
    }
}
