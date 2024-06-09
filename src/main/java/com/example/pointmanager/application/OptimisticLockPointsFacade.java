package com.example.pointmanager.application;

import com.example.pointmanager.application.OptimisticLockPointsService;
import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.exception.InvalidPointsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("optimistic")
@RequiredArgsConstructor
public class OptimisticLockPointsFacade implements PointsService {

    private final OptimisticLockPointsService optimisticLockPointsService;

    @Override
    public Points findPoints(long userId) {
        return optimisticLockPointsService.findPoints(userId);
    }

    @Override
    public List<PointsHistory> findPointsHistory(long userId) {
        return optimisticLockPointsService.findPointsHistory(userId);
    }

    @Override
    public Points chargePoints(long userId, long amountToCharge) throws InterruptedException {
        while (true) {
            try {
                return optimisticLockPointsService.chargePoints(userId, amountToCharge);
            } catch (InvalidPointsException e) {
                throw new InvalidPointsException(e.getMessage());
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

     @Override
     public Points usePoints(long userId, long amountToUse) throws InterruptedException {
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
