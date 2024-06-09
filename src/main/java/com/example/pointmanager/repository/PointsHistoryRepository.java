package com.example.pointmanager.repository;

import com.example.pointmanager.domain.PointsHistory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {

    List<PointsHistory> findPointsHistoryByUserId(long userId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PointsHistory p where p.userId=:userId")
    List<PointsHistory> findPointsHistoryByUserIdWithPessimisticLock(long userId);
}
