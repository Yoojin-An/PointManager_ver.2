package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 낙관 락을 사용한 동시성 테스트
@SpringBootTest
public class OptimisticLockPointsFacadeTest {
    private final OptimisticLockPointsFacade optimisticLockPointsFacade;
    private final PointsRepository pointsRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    @Autowired
    public OptimisticLockPointsFacadeTest(OptimisticLockPointsFacade optimisticLockPointsFacade,
                                          PointsRepository pointsRepository,
                                          PointsHistoryRepository pointsHistoryRepository) {
        this.optimisticLockPointsFacade = optimisticLockPointsFacade;
        this.pointsRepository = pointsRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
    }

    @AfterEach
    public void after() {
        pointsRepository.deleteAll();
        pointsHistoryRepository.deleteAll();
    }

    @Test
    void 동시에_100명의_유저가_충전에_성공한다() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockPointsFacade.chargePoints(1L, 1L);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Points points = pointsRepository.findPointsByUserIdWithOptimisticLock(1L).orElseThrow();
        System.out.println(points.getVersion());

       assertEquals(100, points.getAmount());
    }
}
