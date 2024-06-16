package com.example.pointmanager.repository;

import com.example.pointmanager.domain.PointHistory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findAllPointHistoriesByUserId(long userId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PointHistory p where p.id=:userId")
    List<PointHistory> findAllPointHistoriesByUserIdWithPessimisticLock(long userId);
}
