package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import com.example.pointmanager.exception.InvalidPointsException;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("pessimistic")
//@RequiredArgsConstructor
public class PessimisticLockPointsService extends PointsService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    public PessimisticLockPointsService(PointRepository pointRepository, PointHistoryRepository pointHistoryRepository) {
        super(pointRepository, pointHistoryRepository);
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }


    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public Point chargePoints(long userId, long amountToCharge) {
        Point point = super.pointRepository.findPointsByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));

        Point updatedPoint = point.charge(amountToCharge);

        pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, amountToCharge, PointHistory.TransactionType.CHARGE));

        return updatedPoint;
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public Point usePoints(long userId, long amountToUse) {
        Point point = pointRepository.findPointsByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new InvalidPointsException("userId가 존재하지 않습니다."));

        Point updatedPoint = point.use(amountToUse);

        Point updatedpoints = pointRepository.save(updatedPoint);
        pointHistoryRepository.save(PointHistory.of(userId, amountToUse, PointHistory.TransactionType.USE));

        return updatedpoints;
    }
}
