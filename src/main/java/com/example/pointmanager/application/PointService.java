package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.exception.InvalidPointsException;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;

import java.util.List;

//@Service
//@RequiredArgsConstructor
public abstract class PointsService {
    protected final PointRepository pointRepository;
    protected final PointHistoryRepository pointHistoryRepository;

    public PointsService(PointRepository pointRepository, PointHistoryRepository pointHistoryRepository) {
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public Point enrollUser(long userId) {
        Point point = new Point(userId, 0);
        pointRepository.save(point);
        return point;
    }

    public Point findPoints(long userId) {
        return pointRepository.findPointsByUserId(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }

    public List<PointHistory> findPointsHistory(long userId) {
        List<PointHistory> pointHistory = pointHistoryRepository.findPointsHistoryByUserId(userId);
        if (pointHistory.isEmpty()) {
            throw new InvalidPointsException("userId가 존재하지 않습니다.");
        }
        return pointHistory;
    }

    public abstract Point chargePoints(long userId, long amountToCharge) throws InterruptedException;

    public abstract Point usePoints(long userId, long amountToUse) throws InterruptedException;
}
