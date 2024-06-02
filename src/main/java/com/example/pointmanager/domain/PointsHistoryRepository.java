package com.example.pointmanager.domain.points;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointsHistoryRepository extends JpaRepository<Points, Long> {
    Optional<List<PointsHistory>> findPointsHistoryByUserIdWithOptimisticLock(long userId);
    Optional<Points> findPointsHistoryByUserIdWithPessimisticLock(long userId);
//    void save(PointsHistory pointsHistory);
}
