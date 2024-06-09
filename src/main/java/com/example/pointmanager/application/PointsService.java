package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.exception.InvalidPointsException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PointsService {
    Points findPoints(long userId);
    List<PointsHistory> findPointsHistory(long userId);
    Points chargePoints(long userId, long amountToCharge) throws InterruptedException;
    Points usePoints(long userId, long amountToUse) throws InterruptedException;
}
