package com.example.pointmanager.repository;

import com.example.pointmanager.domain.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findPointById(long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select p from Point p where p.id=:userId")
    Optional<Point> findPointByUserIdWithOptimisticLock(long userId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.id=:userId")
    Optional<Point> findPointByUserIdWithPessimisticLock(long userId);
}
