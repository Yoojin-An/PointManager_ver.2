package com.example.pointmanager.domain.points;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findPointsByUserIdWithOptimisticLock(long userId);
    Optional<Points> findPointsByUserIdWithPessimisticLock(long userId);
}
