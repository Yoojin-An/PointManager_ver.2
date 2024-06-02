package com.example.pointmanager.domain.points;

import com.example.pointmanager.common.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final LockManager lockManager;
    private final PointsValidator pointsValidator;
    private final PointsRepository pointsRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    public Points findPoints(long userId) {
        return pointsValidator.validate(pointsRepository.findPoints(userId));
    }

    public List<PointsHistory> findPointsHistory(long userId) {
        pointsValidator.validate(pointsRepository.findPoints(userId));
        return pointsHistoryRepository.findPointsHistory(userId);
    }

    public Points chargePoints(long userId, long amountToCharge) {
        return lockManager.executeFunctionWithLock(userId, () -> {
            Points points = pointsValidator.validate(pointsRepository.findPoints(userId));
            Points updatedPoints = points.charge(amountToCharge);
            pointsRepository.save(updatedPoints);
            pointsHistoryRepository.save(PointsHistory.of(userId, amountToCharge, PointsHistory.TransactionType.CHARGE));
            return updatedPoints;
        });
    }

    public Points usePoints(long userId, long amountToUse) {
        return lockManager.executeFunctionWithLock(userId, () -> {
            Points points = pointsValidator.validate(pointsRepository.findPoints(userId));
            Points updatedpoints = points.use(amountToUse);
            pointsRepository.save(updatedpoints);
            pointsHistoryRepository.save(PointsHistory.of(userId, amountToUse, PointsHistory.TransactionType.USE));
            return updatedpoints;
        });
    }
}
