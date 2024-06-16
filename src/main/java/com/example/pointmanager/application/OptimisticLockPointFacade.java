package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.exception.InvalidPointsException;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("optimistic")
//@RequiredArgsConstructor
public class OptimisticLockPointsFacade extends PointsService {

    private final OptimisticLockPointsService optimisticLockPointsService;

    public OptimisticLockPointsFacade(PointRepository pointRepository,
                                      PointHistoryRepository pointHistoryRepository,
                                      OptimisticLockPointsService optimisticLockPointsService) {
        super(pointRepository, pointHistoryRepository);
        this.optimisticLockPointsService = optimisticLockPointsService;
    }

    @Override
    public Point chargePoints(long userId, long amountToCharge) throws InterruptedException {
        while (true) {
            try {
                return optimisticLockPointsService.chargePoints(userId, amountToCharge);
            } catch (InvalidPointsException e) {
                throw new InvalidPointsException(e.getMessage());
            } catch (Exception e) {
                Thread.sleep(10000);
            }
        }
    }

     @Override
     public Point usePoints(long userId, long amountToUse) throws InterruptedException {
        while (true) {
            try {
                return optimisticLockPointsService.usePoints(userId, amountToUse);
            } catch (InvalidPointsException e) {
                throw new InvalidPointsException(e.getMessage());
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
