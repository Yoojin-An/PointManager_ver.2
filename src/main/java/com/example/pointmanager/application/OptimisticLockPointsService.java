package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import com.example.pointmanager.exception.InvalidPointsException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.List;

@Service
@Profile("optimistic")
@RequiredArgsConstructor
public class OptimisticLockPointsService {

    private final PointsRepository pointsRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public Points findPoints(long userId) {
        return pointsRepository.findPointsByUserId(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public List<PointsHistory> findPointsHistory(long userId) {
        List<PointsHistory> pointsHistory = pointsHistoryRepository.findPointsHistoryByUserId(userId);
        if (pointsHistory.isEmpty()) {
            throw new InvalidPointsException("userId가 존재하지 않습니다.");
        }
        return pointsHistory;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public Points chargePoints(long userId, long amountToCharge) {
        Points points = pointsRepository.findPointsByUserIdWithOptimisticLock(userId)
                .orElseGet(() ->new Points(userId, 0)); // 조회 결과 없으면 잔고 0인 새로운 Points 객체 생성

        Points updatedPoints = points.charge(amountToCharge);

        pointsRepository.save(updatedPoints);
        pointsHistoryRepository.save(PointsHistory.of(userId, amountToCharge, PointsHistory.TransactionType.CHARGE));

        return updatedPoints;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public Points usePoints(long userId, long amountToUse) {
        Points points = pointsRepository.findPointsByUserIdWithOptimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));

        Points updatedPoints = points.use(amountToUse);

        pointsRepository.save(updatedPoints);
        pointsHistoryRepository.save(PointsHistory.of(userId, amountToUse, PointsHistory.TransactionType.USE));

        return updatedPoints;
    }
}
