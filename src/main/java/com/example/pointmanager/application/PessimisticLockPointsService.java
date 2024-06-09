package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import com.example.pointmanager.exception.InvalidPointsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@Profile("pessimistic")
@RequiredArgsConstructor
public class PessimisticLockPointsService implements PointsService {

    private final PointsRepository pointsRepository;

    private final PointsHistoryRepository pointsHistoryRepository;

    @Override
    @Transactional
    public Points findPoints(long userId) {
        return pointsRepository.findPointsByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public List<PointsHistory> findPointsHistory(long userId) {
        List<PointsHistory> pointsHistory = pointsHistoryRepository.findPointsHistoryByUserIdWithPessimisticLock(userId);
        if (pointsHistory.isEmpty()) {
            throw new InvalidPointsException("userId가 존재하지 않습니다.");
        };
        return pointsHistory;
    }

    @Override
    @Transactional
    public Points chargePoints(long userId, long amountToCharge) {
        Points points = pointsRepository.findPointsByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));

        Points updatedPoints = points.charge(amountToCharge);

        pointsRepository.save(updatedPoints);
        pointsHistoryRepository.save(PointsHistory.of(userId, amountToCharge, PointsHistory.TransactionType.CHARGE));

        return updatedPoints;
    }

    @Override
    @Transactional
    public Points usePoints(long userId, long amountToUse) {
        Points points = pointsRepository.findPointsByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));

        Points updatedPoints = points.use(amountToUse);

        Points updatedpoints = pointsRepository.save(updatedPoints);
        pointsHistoryRepository.save(PointsHistory.of(userId, amountToUse, PointsHistory.TransactionType.USE));

        return updatedpoints;
    }
}
