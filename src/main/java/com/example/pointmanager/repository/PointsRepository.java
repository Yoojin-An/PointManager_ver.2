package com.example.pointmanager.repository;

import com.example.pointmanager.domain.Points;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointsRepository extends JpaRepository<Points, Long> {

    Optional<Points> findPointsByUserId(long userId);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select p from Points p where p.userId=:userId")
    Optional<Points> findPointsByUserIdWithOptimisticLock(long userId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Points p where p.userId=:userId")
    Optional<Points> findPointsByUserIdWithPessimisticLock(long userId);
}
