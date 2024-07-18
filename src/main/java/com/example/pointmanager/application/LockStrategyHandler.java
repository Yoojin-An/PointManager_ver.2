package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.repository.PointRepository;

public interface LockStrategyHandler {
    Point getOrCreatePointWithLock(PointRepository pointRepository, long userId);
    Point getPointWithLock(PointRepository pointRepository, long userId);
}
